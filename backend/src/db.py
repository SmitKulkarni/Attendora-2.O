"""
Attendora SaaS - Database Connection & Multi-Tenant Context Manager
"""
import os
import logging
from contextlib import contextmanager
import uuid
from auth import hash_password

logger = logging.getLogger(__name__)

DATABASE_URL = os.getenv("DATABASE_URL")

# In-memory thread-safe fallback store for local development without active PostgreSQL
_IN_MEMORY_DB = {
    "students": [
        {
            "id": "s_1",
            "name": "Smit Kulkarni",
            "roll_no": "2016001",
            "class_name": "Class 1",
            "department": "Computer Science",
            "email": "smitkulkarni96@gmail.com",
            "status": "PRESENT",
            "check_in_time": "6:30 PM",
            "overall_percentage": 84.5,
            "institution_id": "00000000-0000-0000-0000-000000000001",
            "subjects": [
                {"name": "Artificial Intelligence", "code": "CS-501", "attended": 34, "total": 40, "percentage": 85.0},
                {"name": "Cloud Computing", "code": "CS-502", "attended": 36, "total": 40, "percentage": 90.0},
                {"name": "Theory of Computation", "code": "CS-503", "attended": 26, "total": 40, "percentage": 65.0},
                {"name": "Database Management", "code": "CS-504", "attended": 38, "total": 40, "percentage": 95.0}
            ]
        },
        {
            "id": "s_2",
            "name": "Aarav Sharma",
            "roll_no": "2010002",
            "class_name": "Class 1",
            "department": "Computer Science",
            "email": "aarav.sharma@attendora.edu",
            "status": "ABSENT",
            "check_in_time": "-",
            "overall_percentage": 61.8,
            "institution_id": "00000000-0000-0000-0000-000000000001",
            "subjects": [
                {"name": "Artificial Intelligence", "code": "CS-501", "attended": 24, "total": 40, "percentage": 60.0},
                {"name": "Cloud Computing", "code": "CS-502", "attended": 28, "total": 40, "percentage": 70.0},
                {"name": "Theory of Computation", "code": "CS-503", "attended": 22, "total": 40, "percentage": 55.0},
                {"name": "Database Management", "code": "CS-504", "attended": 25, "total": 40, "percentage": 62.5}
            ]
        },
        {
            "id": "s_3",
            "name": "Priya Patel",
            "roll_no": "2018003",
            "class_name": "Class 1",
            "department": "Computer Science",
            "email": "priya.patel@attendora.edu",
            "status": "LATE",
            "check_in_time": "6:30 PM",
            "overall_percentage": 77.2,
            "institution_id": "00000000-0000-0000-0000-000000000001",
            "subjects": [
                {"name": "Artificial Intelligence", "code": "CS-501", "attended": 34, "total": 40, "percentage": 85.0},
                {"name": "Cloud Computing", "code": "CS-502", "attended": 36, "total": 40, "percentage": 90.0}
            ]
        }
    ],
    "classes": [
        {
            "id": "c_1",
            "name": "Class 1",
            "code": "CS-2A",
            "course_name": "Artificial Intelligence & Foundations",
            "academic_term": "Fall 2026",
            "department": "Computer Science",
            "teacher_name": "Prof. Priya Saxena",
            "enrolled_count": 60,
            "schedule_time": "09:00 AM - 10:30 AM",
            "room_location": "Lecture Hall 101",
            "institution_id": "00000000-0000-0000-0000-000000000001"
        },
        {
            "id": "c_2",
            "name": "Class 2",
            "code": "IT-2B",
            "course_name": "Cloud Computing & Distributed Systems",
            "academic_term": "Fall 2026",
            "department": "Information Technology",
            "teacher_name": "Dr. Arvind Sharma",
            "enrolled_count": 60,
            "schedule_time": "11:00 AM - 12:30 PM",
            "room_location": "Lecture Hall 204",
            "institution_id": "00000000-0000-0000-0000-000000000001"
        }
    ],
    "attendance_sessions": [],
    "users": [
        {
            "id": "u_admin_1",
            "name": "Institutional Admin",
            "full_name": "Institutional Admin",
            "email": "admin@attendora.edu",
            "password_hash": hash_password("Admin@12345"),
            "role": "ADMIN",
            "department": "Computer Science",
            "institution_id": "00000000-0000-0000-0000-000000000001",
            "is_active": True,
            "is_verified": True,
            "created_at": "2026-01-01T00:00:00Z"
        },
        {
            "id": "u_faculty_1",
            "name": "Prof. Priya Saxena",
            "full_name": "Prof. Priya Saxena",
            "email": "faculty@attendora.edu",
            "password_hash": hash_password("Faculty@12345"),
            "role": "TEACHER",
            "department": "Computer Science",
            "institution_id": "00000000-0000-0000-0000-000000000001",
            "is_active": True,
            "is_verified": True,
            "created_at": "2026-01-01T00:00:00Z"
        },
        {
            "id": "s_1",
            "name": "Smit Kulkarni",
            "full_name": "Smit Kulkarni",
            "email": "smitkulkarni96@gmail.com",
            "password_hash": hash_password("Smit@12345"),
            "role": "STUDENT",
            "department": "Computer Science",
            "roll_no": "2016001",
            "institution_id": "00000000-0000-0000-0000-000000000001",
            "is_active": True,
            "is_verified": True,
            "created_at": "2026-01-01T00:00:00Z"
        },
        {
            "id": "s_student_1",
            "name": "Demo Student",
            "full_name": "Demo Student",
            "email": "student@attendora.edu",
            "password_hash": hash_password("Student@12345"),
            "role": "STUDENT",
            "department": "Computer Science",
            "roll_no": "2016002",
            "institution_id": "00000000-0000-0000-0000-000000000001",
            "is_active": True,
            "is_verified": True,
            "created_at": "2026-01-01T00:00:00Z"
        }
    ],
    "verification_codes": {}
}


def sanitize_user(user: dict) -> dict:
    """Removes sensitive credentials before returning to client."""
    if not user:
        return {}
    safe = dict(user)
    safe.pop("password_hash", None)
    safe.pop("password", None)
    return safe


def find_user_by_email(email: str) -> dict:
    """Finds user by normalized email (case-insensitive)."""
    if not email:
        return None
    target = email.strip().lower()
    for u in _IN_MEMORY_DB.get("users", []):
        if u.get("email", "").strip().lower() == target:
            return u
    return None


def find_user_by_id(user_id: str) -> dict:
    """Finds user by unique ID."""
    if not user_id:
        return None
    for u in _IN_MEMORY_DB.get("users", []):
        if str(u.get("id")) == str(user_id):
            return u
    return None


def create_user_record(user_data: dict) -> dict:
    """Creates and persists a new user record."""
    new_user = {
        "id": user_data.get("id") or f"usr_{uuid.uuid4().hex[:12]}",
        "name": user_data.get("name") or user_data.get("full_name", ""),
        "full_name": user_data.get("name") or user_data.get("full_name", ""),
        "email": user_data.get("email", "").strip().lower(),
        "password_hash": user_data.get("password_hash"),
        "role": user_data.get("role", "STUDENT").upper(),
        "department": user_data.get("department", "Computer Science"),
        "roll_no": user_data.get("roll_number") or user_data.get("roll_no", ""),
        "institution_id": user_data.get("institution_id", "00000000-0000-0000-0000-000000000001"),
        "is_active": True,
        "is_verified": user_data.get("is_verified", False),
        "created_at": user_data.get("created_at") or "2026-11-05T00:00:00Z"
    }
    _IN_MEMORY_DB.setdefault("users", []).append(new_user)
    return new_user


def set_verification_code(email: str, code: str, expires_in_seconds: int = 900) -> None:
    """Stores a 6-digit email verification code with expiration."""
    import time
    if not email or not code:
        return
    norm_email = email.strip().lower()
    _IN_MEMORY_DB.setdefault("verification_codes", {})[norm_email] = {
        "code": str(code).strip(),
        "expires_at": time.time() + expires_in_seconds
    }


def get_verification_code(email: str) -> dict:
    """Retrieves the active verification record for an email."""
    if not email:
        return None
    norm_email = email.strip().lower()
    return _IN_MEMORY_DB.get("verification_codes", {}).get(norm_email)


def verify_user_email(email: str, code: str) -> tuple:
    """
    Validates the verification code for an email and activates the user.
    Returns: (success: bool, message: str)
    """
    import time
    if not email or not code:
        return False, "Email and verification code are required"

    norm_email = email.strip().lower()
    rec = get_verification_code(norm_email)
    if not rec:
        return False, "No active verification code found for this email. Please request a new code."

    if time.time() > rec.get("expires_at", 0):
        return False, "Verification code has expired. Please request a new code."

    if str(rec.get("code")).strip() != str(code).strip():
        return False, "Invalid verification code. Please check your email and try again."

    # Mark user as verified
    user = find_user_by_email(norm_email)
    if user:
        user["is_verified"] = True

    # Clear used code
    _IN_MEMORY_DB.get("verification_codes", {}).pop(norm_email, None)
    return True, "Email successfully verified!"




def get_in_memory_db():
    return _IN_MEMORY_DB


@contextmanager
def get_db_cursor(tenant_id=None):
    """
    Context manager that yields a database cursor.
    Enforces PostgreSQL Row-Level Security by setting 'app.current_tenant'.
    Falls back gracefully if live PostgreSQL is not configured.
    """
    if DATABASE_URL:
        import psycopg2
        import psycopg2.extras
        conn = psycopg2.connect(DATABASE_URL)
        cur = conn.cursor(cursor_factory=psycopg2.extras.RealDictCursor)
        try:
            if tenant_id:
                cur.execute("SET LOCAL app.current_tenant = %s;", (str(tenant_id),))
            yield cur
            conn.commit()
        except Exception:
            conn.rollback()
            raise
        finally:
            cur.close()
            conn.close()
    else:
        # Development mode without PostgreSQL instance
        yield None

