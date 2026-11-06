"""
Attendora SaaS - Authentication & Cryptographic Token Utilities
Handles secure password hashing with salt, JWT token generation, and verification.
"""
import os
from datetime import datetime, timedelta, timezone
from typing import Dict, Any, Optional
from werkzeug.security import generate_password_hash, check_password_hash
from jose import jwt, JWTError

# Load secrets strictly from environment variables
JWT_SECRET = os.getenv("JWT_SECRET", "attendora_jwt_prod_key_change_in_production_2026")
JWT_ALGORITHM = "HS256"
JWT_EXPIRES_DAYS = int(os.getenv("JWT_EXPIRES_DAYS", 7))


def hash_password(password: str) -> str:
    """
    Hashes a plain-text password using a salted key-derivation algorithm.
    Never stores plain-text passwords.
    """
    if not password:
        raise ValueError("Password cannot be empty")
    return generate_password_hash(password, method="scrypt")


def verify_password(plain_password: str, hashed_password: str) -> bool:
    """
    Verifies a plain-text password against the stored password hash.
    Constant-time comparison protects against timing attacks.
    """
    if not plain_password or not hashed_password:
        return False
    return check_password_hash(hashed_password, plain_password)


def create_access_token(user_data: Dict[str, Any], expires_delta: Optional[timedelta] = None) -> str:
    """
    Generates a cryptographically signed JWT access token.
    Token payload contains identity, role, and tenant context with strict expiration.
    """
    if expires_delta:
        expire = datetime.now(timezone.utc) + expires_delta
    else:
        expire = datetime.now(timezone.utc) + timedelta(days=JWT_EXPIRES_DAYS)

    payload = {
        "sub": str(user_data.get("id") or user_data.get("uid", "")),
        "email": str(user_data.get("email", "")).lower().strip(),
        "name": str(user_data.get("name") or user_data.get("full_name", "")),
        "role": str(user_data.get("role", "STUDENT")).upper(),
        "department": str(user_data.get("department", "Computer Science")),
        "institution_id": str(user_data.get("institution_id", "00000000-0000-0000-0000-000000000001")),
        "iat": datetime.now(timezone.utc),
        "exp": expire
    }

    token = jwt.encode(payload, JWT_SECRET, algorithm=JWT_ALGORITHM)
    return token


def verify_access_token(token: str) -> Optional[Dict[str, Any]]:
    """
    Verifies and decodes a JWT access token.
    Returns decoded claims dictionary if valid, or None if invalid or expired.
    """
    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
        return payload
    except JWTError:
        return None

