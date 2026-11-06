"""
Attendora SaaS - Phase 6 Automated Test Suite
Verifies Gemini AI Predictive Risk Model, PII Sanitization, Academic Intervention Plan Generation,
and Enterprise Multi-Campus Federation Endpoints.
"""
import os
import sys
import json
import pytest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'src')))

from app import app
from ai_engine import (
    sanitize_student_profile,
    desanitize_text,
    assert_no_pii,
    predict_student_risk,
    generate_academic_intervention,
    generate_institutional_insights,
    get_enterprise_campus_analytics,
    RISK_CRITICAL,
    RISK_HIGH,
    RISK_MODERATE,
    RISK_LOW
)
from db import get_in_memory_db


@pytest.fixture
def client():
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client


@pytest.fixture
def sample_students():
    return get_in_memory_db()["students"]


# ---------------------------------------------------------------------------
# 1. PII Sanitization & Desanitization Tests
# ---------------------------------------------------------------------------

def test_pii_sanitization_removes_personal_identifiers(sample_students):
    student = sample_students[1]  # Aarav Sharma
    sanitized, mapping = sanitize_student_profile(student)

    # Verify no raw name, roll number, or email in sanitized payload
    assert sanitized["pseudonym_id"].startswith("Candidate_STU_")
    assert "Aarav" not in sanitized["pseudonym_id"]
    assert "aarav.sharma@attendora.edu" not in json.dumps(sanitized)
    assert student["roll_no"] not in json.dumps(sanitized)

    # Verify academic attributes are preserved
    assert sanitized["overall_percentage"] == student["overall_percentage"]
    assert len(sanitized["subjects"]) == len(student["subjects"])

    # Verify assert_no_pii utility
    sanitized_text = json.dumps(sanitized)
    assert assert_no_pii(sanitized_text, student) is True


def test_pii_desanitization_restores_name():
    student = {
        "id": "s_demo",
        "name": "Prachi Kulkarni",
        "roll_no": "2026999",
        "email": "prachi.k@attendora.edu",
        "overall_percentage": 58.0
    }
    sanitized, mapping = sanitize_student_profile(student)

    llm_output = f"Evaluation completed for {sanitized['pseudonym_id']}. Recommendation: issue notice."
    hydrated = desanitize_text(llm_output, mapping)

    assert "Prachi Kulkarni" in hydrated
    assert sanitized["pseudonym_id"] not in hydrated


# ---------------------------------------------------------------------------
# 2. Predictive Risk Model Tests
# ---------------------------------------------------------------------------

def test_predict_risk_defaulter_student(sample_students):
    # Aarav Sharma has 61.8% attendance (< 65%) with Theory of Computation at 55%
    defaulter = sample_students[1]
    risk = predict_student_risk(defaulter)

    assert risk["risk_level"] in [RISK_CRITICAL, RISK_HIGH]
    assert risk["risk_score"] >= 60.0
    assert risk["predicted_final_percentage"] <= defaulter["overall_percentage"]
    assert len(risk["risk_factors"]) >= 1
    assert any("Theory of Computation" in rf for rf in risk["risk_factors"])
    assert "ai_summary" in risk


def test_predict_risk_good_standing_student(sample_students):
    # Smit Kulkarni has 84.5% overall attendance
    good_student = sample_students[0]
    risk = predict_student_risk(good_student)

    assert risk["risk_level"] in [RISK_LOW, RISK_MODERATE]
    assert risk["risk_score"] < 45.0
    assert len(risk["recommendations"]) >= 1


# ---------------------------------------------------------------------------
# 3. Academic Intervention Plan & Counseling Letter Tests
# ---------------------------------------------------------------------------

def test_generate_academic_intervention_defaulter(sample_students):
    defaulter = sample_students[1]  # 61.8%
    intervention = generate_academic_intervention(defaulter)

    assert intervention["student_id"] == defaulter["id"]
    assert intervention["risk_level"] in [RISK_CRITICAL, RISK_HIGH]
    assert intervention["classes_needed_for_eligibility"] > 0
    assert len(intervention["action_items"]) >= 3

    letter = intervention["counseling_letter"]
    assert "CONFIDENTIAL: ACADEMIC ATTENDANCE INTERVENTION NOTICE" in letter
    assert defaulter["name"] in letter
    assert defaulter["roll_no"] in letter
    assert "75.0%" in letter
    assert "Dean of Academic Affairs" in letter


# ---------------------------------------------------------------------------
# 4. Institutional Insights & Multi-Campus Analytics
# ---------------------------------------------------------------------------

def test_institutional_insights_aggregation(sample_students):
    insights = generate_institutional_insights(sample_students)

    assert insights["total_students"] == len(sample_students)
    assert insights["at_risk_count"] >= 1
    assert "risk_distribution" in insights
    assert insights["risk_distribution"][RISK_CRITICAL] + insights["risk_distribution"][RISK_HIGH] >= 1
    assert "system_health_index" in insights
    assert "executive_summary" in insights


def test_enterprise_campus_analytics():
    analytics = get_enterprise_campus_analytics("inst_demo_system")

    assert analytics["total_campuses"] == 3
    assert analytics["total_enrolled_students"] > 3000
    assert analytics["system_average_attendance"] > 75.0
    assert len(analytics["campuses"]) == 3
    assert analytics["top_performing_campus"] == "Pune Central Campus"
    assert "ai_strategic_brief" in analytics


# ---------------------------------------------------------------------------
# 5. REST API Endpoints Verification
# ---------------------------------------------------------------------------

def test_api_predict_risk_endpoint(client):
    headers = {"X-Dev-Role": "TEACHER", "X-Dev-User-ID": "u_teacher_1"}
    res = client.post(
        "/api/v1/ai/predict-risk",
        headers=headers,
        json={"student_id": "s_2"}
    )
    assert res.status_code == 200
    data = res.get_json()
    assert data["success"] is True
    assert data["student_id"] == "s_2"
    assert data["risk_assessment"]["risk_level"] in [RISK_CRITICAL, RISK_HIGH]


def test_api_predict_risk_student_not_found(client):
    headers = {"X-Dev-Role": "ADMIN"}
    res = client.post(
        "/api/v1/ai/predict-risk",
        headers=headers,
        json={"student_id": "s_non_existent"}
    )
    assert res.status_code == 404


def test_api_intervention_plan_endpoint(client):
    headers = {"X-Dev-Role": "ADMIN", "X-Dev-User-ID": "u_admin_1"}
    res = client.post(
        "/api/v1/ai/intervention-plan",
        headers=headers,
        json={"student_id": "s_2"}
    )
    assert res.status_code == 200
    data = res.get_json()
    assert data["success"] is True
    plan = data["intervention_plan"]
    assert plan["classes_needed_for_eligibility"] > 0
    assert "counseling_letter" in plan


def test_api_institutional_ai_insights_endpoint(client):
    headers = {"X-Dev-Role": "ADMIN"}
    res = client.get("/api/v1/ai/insights", headers=headers)
    assert res.status_code == 200
    data = res.get_json()
    assert data["success"] is True
    assert "insights" in data
    assert data["insights"]["total_students"] >= 3


def test_api_campuses_endpoint(client):
    headers = {"X-Dev-Role": "STUDENT", "X-Dev-User-ID": "s_1"}
    res = client.get("/api/v1/campuses", headers=headers)
    assert res.status_code == 200
    data = res.get_json()
    assert data["success"] is True
    assert data["total_campuses"] == 3
    assert len(data["campuses"]) == 3

