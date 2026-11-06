"""
Attendora SaaS - Authentication & Authorization Test Suite
Verifies JWT token issuance, password hashing, registration validation,
anti-enumeration login security, current user profile, RBAC enforcement, and logout.
"""
import os
import sys
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'src')))

import pytest
from app import app
from auth import create_access_token


@pytest.fixture
def client():
    app.config['TESTING'] = True
    os.environ['TESTING'] = 'true'
    with app.test_client() as client:
        yield client


def test_registration_success(client):
    payload = {
        "name": "Jane Doe",
        "email": "jane.doe@attendora.edu",
        "password": "StrongPassword!123",
        "confirm_password": "StrongPassword!123",
        "role": "TEACHER",
        "department": "Computer Science"
    }
    res = client.post("/api/v1/auth/register", json=payload)
    assert res.status_code == 201
    data = res.get_json()
    assert data["success"] is True
    assert "token" in data
    assert data["user"]["email"] == "jane.doe@attendora.edu"
    assert "password_hash" not in data["user"]
    assert "password" not in data["user"]

    # Check HttpOnly cookie
    cookie_header = res.headers.get("Set-Cookie")
    assert cookie_header is not None
    assert "attendora_token=" in cookie_header
    assert "HttpOnly" in cookie_header


def test_registration_duplicate_email_rejected(client):
    payload = {
        "name": "Jane Duplicate",
        "email": "jane.doe@attendora.edu",
        "password": "StrongPassword!123",
        "role": "STUDENT"
    }
    res = client.post("/api/v1/auth/register", json=payload)
    assert res.status_code == 409
    data = res.get_json()
    assert data["success"] is False
    assert "already exists" in data["message"].lower()


def test_registration_invalid_email_format(client):
    payload = {
        "name": "Invalid Email",
        "email": "not-an-email",
        "password": "StrongPassword!123"
    }
    res = client.post("/api/v1/auth/register", json=payload)
    assert res.status_code == 400
    data = res.get_json()
    assert "email" in data["errors"]


def test_registration_weak_password_rejected(client):
    payload = {
        "name": "Weak Pass",
        "email": "weak@attendora.edu",
        "password": "123"
    }
    res = client.post("/api/v1/auth/register", json=payload)
    assert res.status_code == 400
    data = res.get_json()
    assert "password" in data["errors"]


def test_registration_password_mismatch_rejected(client):
    payload = {
        "name": "Mismatch Pass",
        "email": "mismatch@attendora.edu",
        "password": "StrongPassword!123",
        "confirm_password": "DifferentPassword!456"
    }
    res = client.post("/api/v1/auth/register", json=payload)
    assert res.status_code == 400
    data = res.get_json()
    assert "confirm_password" in data["errors"]


def test_login_success(client):
    payload = {
        "email": "admin@attendora.edu",
        "password": "Admin@12345"
    }
    res = client.post("/api/v1/auth/login", json=payload)
    assert res.status_code == 200
    data = res.get_json()
    assert data["success"] is True
    assert "token" in data
    assert data["user"]["role"] == "ADMIN"
    assert "password_hash" not in data["user"]

    cookie_header = res.headers.get("Set-Cookie")
    assert cookie_header is not None
    assert "attendora_token=" in cookie_header


def test_login_invalid_password_returns_generic_error(client):
    payload = {
        "email": "admin@attendora.edu",
        "password": "WrongPassword!999"
    }
    res = client.post("/api/v1/auth/login", json=payload)
    assert res.status_code == 401
    data = res.get_json()
    assert data["success"] is False
    # Generic message prevents email enumeration
    assert data["message"] == "Invalid email or password"


def test_login_nonexistent_user_returns_generic_error(client):
    payload = {
        "email": "unknown_ghost_user@attendora.edu",
        "password": "AnyPassword!123"
    }
    res = client.post("/api/v1/auth/login", json=payload)
    assert res.status_code == 401
    data = res.get_json()
    assert data["success"] is False
    assert data["message"] == "Invalid email or password"


def test_login_missing_fields(client):
    res = client.post("/api/v1/auth/login", json={"email": ""})
    assert res.status_code == 400
    data = res.get_json()
    assert "email" in data["errors"]


def test_get_current_user_with_bearer_token(client):
    # Log in first to obtain token
    login_res = client.post("/api/v1/auth/login", json={
        "email": "faculty@attendora.edu",
        "password": "Faculty@12345"
    })
    token = login_res.get_json()["token"]

    headers = {"Authorization": f"Bearer {token}"}
    me_res = client.get("/api/v1/auth/me", headers=headers)
    assert me_res.status_code == 200
    me_data = me_res.get_json()
    assert me_data["success"] is True
    assert me_data["user"]["email"] == "faculty@attendora.edu"
    assert me_data["user"]["role"] == "TEACHER"
    assert "password_hash" not in me_data["user"]


def test_get_current_user_with_cookie(client):
    client.post("/api/v1/auth/login", json={
        "email": "smitkulkarni96@gmail.com",
        "password": "Smit@12345"
    })
    # The test client preserves cookies across requests
    me_res = client.get("/api/v1/auth/me")
    assert me_res.status_code == 200
    me_data = me_res.get_json()
    assert me_data["success"] is True
    assert me_data["user"]["email"] == "smitkulkarni96@gmail.com"


def test_get_current_user_unauthenticated():
    with app.test_client() as unauth_client:
        me_res = unauth_client.get("/api/v1/auth/me")
        assert me_res.status_code == 401


def test_logout_clears_cookie():
    with app.test_client() as c:
        # Log in
        c.post("/api/v1/auth/login", json={
            "email": "admin@attendora.edu",
            "password": "Admin@12345"
        })
        # Log out
        res = c.post("/api/v1/auth/logout")
        assert res.status_code == 200
        data = res.get_json()
        assert data["success"] is True

        # After logout, accessing /api/v1/auth/me should fail
        me_res = c.get("/api/v1/auth/me")
        assert me_res.status_code == 401


def test_role_authorization_student_forbidden_admin_endpoint(client):
    # Issue a student JWT token
    student_token = create_access_token({
        "id": "s_1",
        "email": "student@attendora.edu",
        "name": "Student Test",
        "role": "STUDENT"
    })
    headers = {"Authorization": f"Bearer {student_token}"}
    payload = {"name": "Unauthorized Class", "code": "CS-999"}
    # POST /api/v1/classes requires ADMIN role
    res = client.post("/api/v1/classes", json=payload, headers=headers)
    assert res.status_code == 403
    data = res.get_json()
    assert "Forbidden" in data["error"] or "Forbidden" in data.get("message", "")


def test_role_authorization_admin_allowed_endpoint(client):
    # Issue an admin JWT token
    admin_token = create_access_token({
        "id": "u_admin_1",
        "email": "admin@attendora.edu",
        "name": "Admin Test",
        "role": "ADMIN"
    })
    headers = {"Authorization": f"Bearer {admin_token}"}
    res = client.get("/api/v1/students", headers=headers)
    assert res.status_code == 200
    data = res.get_json()
    assert "students" in data


def test_email_verification_success(client):
    # 1. Register a new user
    reg_res = client.post("/api/v1/auth/register", json={
        "name": "Mark Spencer",
        "email": "mark.spencer@attendora.edu",
        "password": "StrongPassword!123",
        "confirm_password": "StrongPassword!123",
        "role": "STUDENT"
    })
    assert reg_res.status_code == 201
    reg_data = reg_res.get_json()
    assert reg_data["requires_verification"] is True
    code = reg_data["dev_code"]

    # 2. Verify with correct code
    ver_res = client.post("/api/v1/auth/verify-email", json={
        "email": "mark.spencer@attendora.edu",
        "code": code
    })
    assert ver_res.status_code == 200
    ver_data = ver_res.get_json()
    assert ver_data["success"] is True
    assert "verified" in ver_data["message"].lower()
    assert "token" in ver_data


def test_email_verification_invalid_code(client):
    ver_res = client.post("/api/v1/auth/verify-email", json={
        "email": "mark.spencer@attendora.edu",
        "code": "000000"
    })
    assert ver_res.status_code == 400
    ver_data = ver_res.get_json()
    assert ver_data["success"] is False


def test_resend_verification_code(client):
    res = client.post("/api/v1/auth/resend-code", json={
        "email": "mark.spencer@attendora.edu"
    })
    assert res.status_code in [200, 400]
    data = res.get_json()
    assert "message" in data


def test_login_unverified_requires_verification(client):
    # Register user but do not verify
    client.post("/api/v1/auth/register", json={
        "name": "Unverified User",
        "email": "unverified@attendora.edu",
        "password": "StrongPassword!123",
        "confirm_password": "StrongPassword!123"
    })

    # Attempt to log in
    login_res = client.post("/api/v1/auth/login", json={
        "email": "unverified@attendora.edu",
        "password": "StrongPassword!123"
    })
    assert login_res.status_code == 403
    login_data = login_res.get_json()
    assert login_data["requires_verification"] is True
    assert "verify your email" in login_data["message"].lower()

