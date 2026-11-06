"""
Attendora SaaS - Authentication & Multi-Tenant Context Middleware
Handles JWT Bearer verification, secure HttpOnly cookie authentication,
and strict Role-Based Access Control (RBAC).
"""
from functools import wraps
import os
from flask import request, jsonify, g
from auth import verify_access_token

# Flag for Firebase Admin (optional fallback)
FIREBASE_INITIALIZED = False
try:
    import firebase_admin
    from firebase_admin import auth as fb_auth, credentials
    cred_path = os.getenv("FIREBASE_SERVICE_ACCOUNT_JSON", "firebase-service-account.json")
    if os.path.exists(cred_path):
        cred = credentials.Certificate(cred_path)
        firebase_admin.initialize_app(cred)
        FIREBASE_INITIALIZED = True
except Exception:
    FIREBASE_INITIALIZED = False


def extract_token_from_request() -> str:
    """
    Extracts authentication token from:
    1. HTTP-only secure cookie ('attendora_token')
    2. Authorization header ('Bearer <token>')
    """
    # 1. Check HTTP-only cookie first (preferred for browsers)
    cookie_token = request.cookies.get("attendora_token")
    if cookie_token:
        return cookie_token

    # 2. Check Authorization header
    auth_header = request.headers.get("Authorization")
    if auth_header and auth_header.startswith("Bearer "):
        parts = auth_header.split(" ")
        if len(parts) == 2:
            return parts[1].strip()

    return ""


def require_auth(allowed_roles=None):
    """
    Decorator to enforce JWT Authentication and Role-Based Access Control (RBAC).
    Validates token, extracts tenant context for PostgreSQL Row-Level Security,
    and sets g.user and request.user.
    """
    if allowed_roles is None:
        allowed_roles = ["ADMIN", "TEACHER", "STUDENT"]

    normalized_allowed_roles = [r.upper() for r in allowed_roles]

    def decorator(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
            dev_role = request.headers.get("X-Dev-Role")
            dev_inst = request.headers.get("X-Institution-ID", "00000000-0000-0000-0000-000000000001")
            dev_uid = request.headers.get("X-Dev-User-ID", "usr_demo_101")

            token = extract_token_from_request()
            user_data = None

            # 1. Real JWT Verification (Cookie or Bearer token)
            if token:
                payload = verify_access_token(token)
                if payload:
                    user_data = {
                        "uid": payload.get("sub"),
                        "id": payload.get("sub"),
                        "email": payload.get("email"),
                        "name": payload.get("name"),
                        "role": payload.get("role", "STUDENT").upper(),
                        "department": payload.get("department", "Computer Science"),
                        "institution_id": payload.get("institution_id", dev_inst)
                    }
                else:
                    return jsonify({
                        "success": False,
                        "error": "Unauthorized: Invalid or expired authentication token",
                        "code": "TOKEN_INVALID"
                    }), 401

            # 2. Firebase Token Verification fallback if configured
            elif FIREBASE_INITIALIZED and request.headers.get("Authorization", "").startswith("Bearer "):
                raw_fb_token = request.headers.get("Authorization").split("Bearer ")[1].strip()
                try:
                    decoded = fb_auth.verify_id_token(raw_fb_token)
                    user_data = {
                        "uid": decoded.get("uid"),
                        "id": decoded.get("uid"),
                        "email": decoded.get("email", "user@attendora.edu"),
                        "name": decoded.get("name", "Firebase User"),
                        "role": decoded.get("role", "TEACHER").upper(),
                        "department": "Computer Science",
                        "institution_id": decoded.get("institution_id", dev_inst)
                    }
                except Exception as ex:
                    return jsonify({
                        "success": False,
                        "error": "Unauthorized: Invalid or expired Firebase token",
                        "details": str(ex)
                    }), 401

            # 3. Development / Test Harness Bypass Header
            elif dev_role:
                role = dev_role.upper()
                user_data = {
                    "uid": dev_uid,
                    "id": dev_uid,
                    "email": f"{role.lower()}@attendora.edu",
                    "name": f"Dev {role.capitalize()}",
                    "role": role,
                    "department": "Computer Science",
                    "institution_id": dev_inst
                }

            # 4. If neither valid token nor dev header is present: reject
            if not user_data:
                return jsonify({
                    "success": False,
                    "error": "Unauthorized: Authentication required. Please log in.",
                    "code": "AUTH_REQUIRED"
                }), 401

            # 5. Strict Role-Based Access Control (RBAC)
            user_role = user_data.get("role", "STUDENT").upper()
            if normalized_allowed_roles and user_role not in normalized_allowed_roles:
                return jsonify({
                    "success": False,
                    "error": "Forbidden: Insufficient permissions for this resource",
                    "required_roles": normalized_allowed_roles,
                    "current_role": user_role,
                    "code": "FORBIDDEN"
                }), 403

            # 6. Bind user and tenant context to Flask request & g
            request.user = user_data
            g.user = user_data
            g.institution_id = user_data["institution_id"]

            return f(*args, **kwargs)
        return decorated_function
    return decorator


def require_roles(*roles):
    """
    Secondary authorization decorator explicitly checking specific role(s).
    Can be used in conjunction with require_auth().
    """
    allowed = [r.upper() for r in roles]
    def decorator(f):
        @wraps(f)
        def wrapper(*args, **kwargs):
            user = getattr(g, "user", None) or getattr(request, "user", None)
            if not user:
                return jsonify({
                    "success": False,
                    "error": "Unauthorized: Authentication required",
                    "code": "AUTH_REQUIRED"
                }), 401
            
            user_role = str(user.get("role", "")).upper()
            if user_role not in allowed:
                return jsonify({
                    "success": False,
                    "error": f"Forbidden: This endpoint requires one of roles: {', '.join(allowed)}",
                    "code": "FORBIDDEN"
                }), 403
            return f(*args, **kwargs)
        return wrapper
    return decorator
