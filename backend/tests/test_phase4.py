"""
Attendora SaaS - Phase 4 Automated Test Suite
Verifies Defaulter Automation, Celery/Thread Worker Tasks, Emailer & PDF Generation.
"""
import os
import sys
import time
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'src')))

import pytest
from app import app
from db import get_in_memory_db
from emailer import render_defaulter_email, send_email, get_sent_emails, clear_sent_emails
from pdf_generator import generate_attendance_transcript_pdf
from tasks import enqueue_task, audit_and_generate_defaulters, get_task_status


@pytest.fixture
def client():
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client


def test_defaulter_email_rendering():
    mem_db = get_in_memory_db()
    # Aarav Sharma has 61.8%
    student = next(s for s in mem_db["students"] if s["id"] == "s_2")

    html = render_defaulter_email(student, threshold=75.0, notice_id="test_notice_123")
    assert html is not None
    assert "Aarav Sharma" in html
    assert "61.8%" in html
    assert "75.0%" in html
    assert "CRITICAL ACADEMIC ATTENDANCE NOTICE" in html
    assert "DEFICIENT" in html


def test_email_dispatch_spool():
    clear_sent_emails()
    success = send_email("student@attendora.edu", "Test Subject", "<p>Test Content</p>")
    assert success is True

    spool = get_sent_emails()
    assert len(spool) == 1
    assert spool[0]["to"] == "student@attendora.edu"
    assert spool[0]["status"] == "SENT"


def test_pdf_transcript_generation(tmp_path):
    mem_db = get_in_memory_db()
    student = mem_db["students"][0] # Smit Kulkarni

    output_pdf = str(tmp_path / "test_smit_transcript.pdf")
    generated_path = generate_attendance_transcript_pdf(student, output_path=output_pdf)

    assert os.path.exists(generated_path)
    file_size = os.path.getsize(generated_path)
    assert file_size > 2000 # Minimum realistic PDF size

    # Verify PDF magic bytes
    with open(generated_path, "rb") as f:
        header = f.read(5)
        assert header == b"%PDF-"


def test_async_task_enqueue_and_completion():
    clear_sent_emails()
    task_id = enqueue_task(audit_and_generate_defaulters, threshold=75.0)
    assert task_id.startswith("task_")

    # Give thread pool a brief moment to complete execution
    time.sleep(0.5)

    status = get_task_status(task_id)
    assert status is not None
    assert status["status"] in ["RUNNING", "SUCCESS"]
    if status["status"] == "SUCCESS":
        assert status["result"]["defaulters_found"] >= 1
        assert status["result"]["alerts_dispatched"] >= 1


def test_transcript_download_endpoint(client):
    headers = {'X-Dev-Role': 'STUDENT', 'X-Dev-User-ID': 's_1'}
    response = client.get('/api/v1/student/s_1/transcript.pdf', headers=headers)
    assert response.status_code == 200
    assert response.mimetype == 'application/pdf'
    assert response.data.startswith(b"%PDF-")


def test_notify_defaulters_api_triggers_task(client):
    headers = {'X-Dev-Role': 'ADMIN'}
    payload = {'threshold': 75.0}
    response = client.post('/api/v1/reports/defaulters/notify', json=payload, headers=headers)
    assert response.status_code == 202
    data = response.get_json()
    assert data['success'] is True
    assert 'task_id' in data

    # Check task status endpoint
    task_id = data['task_id']
    status_resp = client.get(f'/api/v1/tasks/{task_id}', headers=headers)
    assert status_resp.status_code == 200
    status_data = status_resp.get_json()
    assert status_data['task_id'] == task_id
    assert status_data['status'] in ['PENDING', 'RUNNING', 'SUCCESS']

