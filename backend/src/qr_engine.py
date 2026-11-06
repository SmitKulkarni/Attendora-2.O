"""
Attendora SaaS - Dynamic TOTP QR & Anti-Proxy Device Verification Engine
"""
import time
import json
import base64
import hmac
import hashlib
import os

SECRET_KEY = os.getenv("ATTENDORA_QR_SECRET", "attendora_secret_lecture_salt_2026")
QR_STEP_INTERVAL = 10  # Seconds per TOTP window

# In-memory session device registry: (session_id, device_id) -> student_id
SESSION_DEVICE_REGISTRY = {}


def generate_dynamic_qr_payload(class_id, session_id, institution_id, interval=QR_STEP_INTERVAL):
    """
    Generates a time-bound, HMAC-signed dynamic QR token string.
    Payload rotates every `interval` seconds (default 10s).
    """
    current_time = time.time()
    time_step = int(current_time // interval)
    seconds_remaining = interval - int(current_time % interval)

    payload = {
        "cid": class_id,
        "sid": session_id,
        "iid": institution_id,
        "ts": int(current_time),
        "step": time_step
    }

    payload_json = json.dumps(payload, separators=(',', ':'))
    payload_b64 = base64.urlsafe_b64encode(payload_json.encode()).decode()

    # Create HMAC-SHA256 signature truncated to 16 characters for compact QR representation
    signature = hmac.new(
        SECRET_KEY.encode(),
        payload_json.encode(),
        hashlib.sha256
    ).hexdigest()[:16]

    token_string = f"{payload_b64}.{signature}"

    return {
        "qr_token": token_string,
        "expires_in_seconds": seconds_remaining,
        "step": time_step,
        "interval_seconds": interval
    }


def verify_qr_token(token_string, expected_class_id=None, interval=QR_STEP_INTERVAL, max_drift_steps=1):
    """
    Validates token integrity, HMAC signature, and timestamp expiration.
    Returns: payload dictionary if valid, or raises ValueError.
    """
    if not token_string or '.' not in token_string:
        raise ValueError("Malformed QR token format")

    parts = token_string.split('.')
    if len(parts) != 2:
        raise ValueError("Invalid QR token segment count")

    payload_b64, signature = parts

    try:
        payload_json = base64.urlsafe_b64decode(payload_b64.encode()).decode()
        payload = json.loads(payload_json)
    except Exception as e:
        raise ValueError(f"Corrupt QR payload encoding: {e}")

    # 1. Verify HMAC Signature
    expected_sig = hmac.new(
        SECRET_KEY.encode(),
        payload_json.encode(),
        hashlib.sha256
    ).hexdigest()[:16]

    if not hmac.compare_digest(signature, expected_sig):
        raise ValueError("Tampered QR token: Signature mismatch")

    # 2. Verify Time Drift (TOTP Window)
    token_step = payload.get("step", 0)
    current_step = int(time.time() // interval)

    if abs(current_step - token_step) > max_drift_steps:
        raise ValueError("QR token expired. Please scan the newly refreshed QR code.")

    # 3. Verify Class ID if supplied
    if expected_class_id and payload.get("cid") != expected_class_id:
        raise ValueError(f"QR token is for class '{payload.get('cid')}', not '{expected_class_id}'")

    return payload


def verify_device_uniqueness(session_id, student_id, device_id):
    """
    Anti-Proxy Verification:
    Ensures a single physical device cannot be used to mark attendance
    for multiple student IDs within the same lecture session.
    """
    key = (session_id, device_id)
    registered_student = SESSION_DEVICE_REGISTRY.get(key)

    if registered_student and registered_student != student_id:
        raise ValueError(
            f"Proxy Attendance Violation: Device '{device_id}' has already checked in student '{registered_student}' for this session."
        )

    # Register this device for the student
    SESSION_DEVICE_REGISTRY[key] = student_id
    return True


def clear_device_registry():
    SESSION_DEVICE_REGISTRY.clear()

