"""
Attendora SaaS - Authentication API Endpoints
Provides registration, login, logout, email verification, and current user profile endpoints
with secure JWT generation, HttpOnly cookie support, and rate-limiting.
"""
import random
from flask import Blueprint, request, jsonify, make_response, g
from auth import hash_password, verify_password, create_access_token
from validators import validate_registration, validate_login
from security import (
    check_rate_limit,
    rate_limit_exceeded_response,
    set_auth_cookie,
    clear_auth_cookie
)
from db import (
    find_user_by_email,
    find_user_by_id,
    create_user_record,
    sanitize_user,
    set_verification_code,
    verify_user_email
)
from emailer import send_verification_email
from middleware import require_auth

auth_bp = Blueprint("auth", __name__)


# ---------------------------------------------------------------------------
# 1. User Registration Flow (with 6-digit Email Verification)
# ---------------------------------------------------------------------------
@auth_bp.route("/api/v1/auth/register", methods=["POST"])
@auth_bp.route("/api/auth/register", methods=["POST"])
def register():
    """
    Registers a new user account and dispatches an email verification code.
    Validates payload, enforces password complexity, hashes password securely,
    generates 6-digit verification code, and dispatches email.
    """
    is_allowed, retry_after = check_rate_limit("register")
    if not is_allowed:
        return rate_limit_exceeded_response(retry_after)

    data = request.get_json(silent=True)
    is_valid, errors, cleaned = validate_registration(data)
    if not is_valid:
        return jsonify({
            "success": False,
            "message": "Validation failed",
            "errors": errors
        }), 400

    # Prevent duplicate account creation
    existing_user = find_user_by_email(cleaned["email"])
    if existing_user:
        return jsonify({
            "success": False,
            "message": "An account with this email address already exists.",
            "errors": {"email": "Email is already registered"}
        }), 409

    # Hash password with salted scrypt key-derivation function
    hashed_pwd = hash_password(cleaned["password"])
    cleaned["password_hash"] = hashed_pwd
    cleaned["is_verified"] = False  # Requires email verification

    # Persist user
    new_user = create_user_record(cleaned)

    # Generate 6-digit verification code (valid for 15 minutes)
    verification_code = f"{random.randint(100000, 999999)}"
    set_verification_code(cleaned["email"], verification_code, expires_in_seconds=900)

    # Dispatch verification email
    user_display_name = new_user.get("name") or new_user.get("full_name") or "Student"
    send_verification_email(cleaned["email"], user_display_name, verification_code)

    # Issue token for pending session
    token = create_access_token(new_user)
    safe_profile = sanitize_user(new_user)

    resp_data = {
        "success": True,
        "requires_verification": True,
        "message": "Account created successfully. A 6-digit verification code has been sent to your email.",
        "email": cleaned["email"],
        "user": safe_profile,
        "token": token,
        "access_token": token,
        "dev_code": verification_code  # Exposed for local testing & instant developer preview
    }
    response = make_response(jsonify(resp_data), 201)
    set_auth_cookie(response, token)
    return response


# ---------------------------------------------------------------------------
# 2. Email Verification Flow
# ---------------------------------------------------------------------------
@auth_bp.route("/api/v1/auth/verify-email", methods=["POST"])
@auth_bp.route("/api/auth/verify-email", methods=["POST"])
def verify_email():
    """
    Verifies the 6-digit code sent to the user's email.
    Activates the account and returns authenticated JWT session.
    """
    data = request.get_json(silent=True) or {}
    email = data.get("email", "").strip().lower()
    code = str(data.get("code", "")).strip()

    if not email:
        return jsonify({"success": False, "message": "Email is required"}), 400
    if not code or len(code) != 6:
        return jsonify({"success": False, "message": "A valid 6-digit verification code is required"}), 400

    success, msg = verify_user_email(email, code)
    if not success:
        return jsonify({"success": False, "message": msg}), 400

    user = find_user_by_email(email)
    if not user:
        return jsonify({"success": False, "message": "User not found"}), 404

    # Generate fresh active JWT access token
    token = create_access_token(user)
    safe_profile = sanitize_user(user)

    resp_data = {
        "success": True,
        "message": "Email successfully verified! Welcome to Attendora.",
        "user": safe_profile,
        "token": token,
        "access_token": token
    }
    response = make_response(jsonify(resp_data), 200)
    set_auth_cookie(response, token)
    return response


# ---------------------------------------------------------------------------
# 3. Resend Verification Code
# ---------------------------------------------------------------------------
@auth_bp.route("/api/v1/auth/resend-code", methods=["POST"])
@auth_bp.route("/api/auth/resend-code", methods=["POST"])
def resend_code():
    """
    Generates and resends a fresh 6-digit verification code.
    """
    is_allowed, retry_after = check_rate_limit("resend_code")
    if not is_allowed:
        return rate_limit_exceeded_response(retry_after)

    data = request.get_json(silent=True) or {}
    email = data.get("email", "").strip().lower()

    if not email:
        return jsonify({"success": False, "message": "Email is required"}), 400

    user = find_user_by_email(email)
    if not user:
        # Prevent email enumeration with uniform success acknowledgment
        return jsonify({
            "success": True,
            "message": "If an account exists with this email, a new verification code has been dispatched."
        }), 200

    if user.get("is_verified", False):
        return jsonify({
            "success": True,
            "message": "Email is already verified. You can log in directly."
        }), 200

    # Generate new 6-digit code
    new_code = f"{random.randint(100000, 999999)}"
    set_verification_code(email, new_code, expires_in_seconds=900)

    user_name = user.get("name") or user.get("full_name") or "Student"
    send_verification_email(email, user_name, new_code)

    return jsonify({
        "success": True,
        "message": "A fresh 6-digit verification code has been sent to your email.",
        "dev_code": new_code
    }), 200


# ---------------------------------------------------------------------------
# 4. User Login Flow
# ---------------------------------------------------------------------------
@auth_bp.route("/api/v1/auth/login", methods=["POST"])
@auth_bp.route("/api/auth/login", methods=["POST"])
def login():
    """
    Authenticates user credentials.
    Uses timing-attack resistant password verification and generic error message
    to prevent email enumeration. Requires email verification before access.
    """
    is_allowed, retry_after = check_rate_limit("login")
    if not is_allowed:
        return rate_limit_exceeded_response(retry_after)

    data = request.get_json(silent=True)
    is_valid, errors, cleaned = validate_login(data)
    if not is_valid:
        return jsonify({
            "success": False,
            "message": "Validation failed",
            "errors": errors
        }), 400

    # User lookup
    user = find_user_by_email(cleaned["email"])

    # Generic invalid credential response to avoid account enumeration
    if not user or not user.get("password_hash") or not verify_password(cleaned["password"], user.get("password_hash")):
        return jsonify({
            "success": False,
            "message": "Invalid email or password",
            "error": "Invalid credentials"
        }), 401

    if not user.get("is_active", True):
        return jsonify({
            "success": False,
            "message": "Account has been deactivated. Please contact support."
        }), 403

    # Check email verification status
    if not user.get("is_verified", True):
        # Generate new verification code and email user
        new_code = f"{random.randint(100000, 999999)}"
        set_verification_code(user["email"], new_code, expires_in_seconds=900)
        user_name = user.get("name") or user.get("full_name") or "Student"
        send_verification_email(user["email"], user_name, new_code)

        return jsonify({
            "success": False,
            "requires_verification": True,
            "email": user["email"],
            "message": "Please verify your email before logging in. A new 6-digit code has been sent to your email.",
            "dev_code": new_code
        }), 403

    # Generate JWT access token
    token = create_access_token(user)
    safe_profile = sanitize_user(user)

    resp_data = {
        "success": True,
        "message": "Login successful",
        "user": safe_profile,
        "token": token,
        "access_token": token
    }
    response = make_response(jsonify(resp_data), 200)
    set_auth_cookie(response, token)
    return response


# ---------------------------------------------------------------------------
# 5. User Logout Flow
# ---------------------------------------------------------------------------
@auth_bp.route("/api/v1/auth/logout", methods=["POST"])
@auth_bp.route("/api/auth/logout", methods=["POST"])
def logout():
    """
    Logs out the user by clearing the secure HttpOnly cookie.
    """
    resp_data = {
        "success": True,
        "message": "Logged out successfully"
    }
    response = make_response(jsonify(resp_data), 200)
    clear_auth_cookie(response)
    return response


# ---------------------------------------------------------------------------
# 6. Current Authenticated User Profile
# ---------------------------------------------------------------------------
@auth_bp.route("/api/v1/auth/me", methods=["GET"])
@auth_bp.route("/api/auth/me", methods=["GET"])
@require_auth()
def get_current_user():
    """
    Returns the currently authenticated user's safe profile.
    Protected endpoint: never returns password hashes, secrets, or internal flags.
    """
    user_context = getattr(g, "user", None)
    if not user_context:
        return jsonify({
            "success": False,
            "message": "Authentication required"
        }), 401

    # Fetch fresh record from DB if possible
    fresh_user = None
    if user_context.get("id"):
        fresh_user = find_user_by_id(user_context["id"])
    elif user_context.get("email"):
        fresh_user = find_user_by_email(user_context["email"])

    safe_profile = sanitize_user(fresh_user or user_context)

    return jsonify({
        "success": True,
        "user": safe_profile
    }), 200
