"""
Attendora SaaS - Request Input Validation Engine
Implements strict multi-tier validation for registration, login, and resource requests.
"""
import re
from typing import Dict, Any, Tuple, Optional

# Standard RFC 5322 compatible email regular expression
EMAIL_REGEX = re.compile(
    r"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$"
)

# Password requirement: >= 8 characters, >= 1 uppercase, >= 1 lowercase, >= 1 digit, >= 1 special character
PASSWORD_REGEX = re.compile(
    r"^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_+\-=\[\]{};':\"\\|,.<>\/?]).{8,}$"
)

VALID_ROLES = {"ADMIN", "TEACHER", "STUDENT"}


def validate_registration(data: Optional[Dict[str, Any]]) -> Tuple[bool, Dict[str, str], Dict[str, Any]]:
    """
    Validates user registration payload.
    Returns:
        (is_valid: bool, errors: dict, cleaned_data: dict)
    """
    errors: Dict[str, str] = {}
    cleaned: Dict[str, Any] = {}

    if not isinstance(data, dict):
        return False, {"payload": "Invalid JSON body provided"}, {}

    # 1. Full Name Validation
    name = data.get("name") or data.get("full_name")
    if not name or not isinstance(name, str) or not name.strip():
        errors["name"] = "Full name is required."
    else:
        name_clean = name.strip()
        if len(name_clean) < 2:
            errors["name"] = "Name must be at least 2 characters long."
        elif len(name_clean) > 100:
            errors["name"] = "Name cannot exceed 100 characters."
        else:
            cleaned["name"] = name_clean

    # 2. Email Validation & Normalization
    email = data.get("email")
    if not email or not isinstance(email, str) or not email.strip():
        errors["email"] = "Email address is required."
    else:
        email_clean = email.strip().lower()
        if not EMAIL_REGEX.match(email_clean) or len(email_clean) > 254:
            errors["email"] = "Invalid email address format."
        else:
            cleaned["email"] = email_clean

    # 3. Password Complexity Validation
    password = data.get("password")
    if not password or not isinstance(password, str):
        errors["password"] = "Password is required."
    else:
        if len(password) < 8:
            errors["password"] = "Password must be at least 8 characters long."
        elif not PASSWORD_REGEX.match(password):
            errors["password"] = (
                "Password must contain at least one uppercase letter, "
                "one lowercase letter, one number, and one special character."
            )
        else:
            cleaned["password"] = password

    # 4. Confirm Password Check (if present in payload)
    confirm_password = data.get("confirm_password") or data.get("confirmPassword")
    if confirm_password is not None:
        if confirm_password != password:
            errors["confirm_password"] = "Passwords do not match."

    # 5. Role Validation
    raw_role = data.get("role", "STUDENT")
    if isinstance(raw_role, str):
        role_clean = raw_role.strip().upper()
        if role_clean in VALID_ROLES:
            cleaned["role"] = role_clean
        else:
            errors["role"] = f"Invalid role. Must be one of: {', '.join(sorted(VALID_ROLES))}."
    else:
        cleaned["role"] = "STUDENT"

    # 6. Optional Department & Roll Number
    dept = data.get("department", "Computer Science")
    cleaned["department"] = str(dept).strip() if dept else "Computer Science"

    if "roll_number" in data and data["roll_number"]:
        cleaned["roll_number"] = str(data["roll_number"]).strip().upper()

    return (len(errors) == 0, errors, cleaned)


def validate_login(data: Optional[Dict[str, Any]]) -> Tuple[bool, Dict[str, str], Dict[str, Any]]:
    """
    Validates user login payload.
    Returns:
        (is_valid: bool, errors: dict, cleaned_data: dict)
    """
    errors: Dict[str, str] = {}
    cleaned: Dict[str, Any] = {}

    if not isinstance(data, dict):
        return False, {"payload": "Invalid JSON body provided"}, {}

    email = data.get("email")
    if not email or not isinstance(email, str) or not email.strip():
        errors["email"] = "Email address is required."
    else:
        email_clean = email.strip().lower()
        if not EMAIL_REGEX.match(email_clean):
            errors["email"] = "Invalid email address format."
        else:
            cleaned["email"] = email_clean

    password = data.get("password")
    if not password or not isinstance(password, str):
        errors["password"] = "Password is required."
    else:
        cleaned["password"] = password

    return (len(errors) == 0, errors, cleaned)

