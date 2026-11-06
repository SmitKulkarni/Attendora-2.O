"""
Attendora SaaS - Security, Rate Limiting, and HTTP Hardening
Provides rate-limiting against brute force, security headers, and cookie handling.
"""
import os
import time
from typing import Dict, List, Tuple
from collections import defaultdict
from flask import request, jsonify, Response

# Sliding window rate limiter configuration for sensitive endpoints
RATE_LIMIT_WINDOW_SECONDS = int(os.getenv("RATE_LIMIT_WINDOW_SECONDS", 300))  # 5 minutes
MAX_REQUESTS_PER_WINDOW = int(os.getenv("MAX_AUTH_REQUESTS_PER_WINDOW", 15))  # max 15 requests / 5 mins

_RATE_LIMIT_CACHE: Dict[str, List[float]] = defaultdict(list)


def check_rate_limit(key_prefix: str = "auth") -> Tuple[bool, int]:
    """
    Sliding window rate limit checker per client IP.
    Returns:
        (is_allowed: bool, retry_after_seconds: int)
    """
    # In tests or if disabled, allow
    if os.getenv("TESTING") == "true" or os.getenv("DISABLE_RATE_LIMIT") == "true":
        return True, 0

    client_ip = request.headers.get("X-Forwarded-For", request.remote_addr) or "unknown"
    if "," in client_ip:
        client_ip = client_ip.split(",")[0].strip()

    cache_key = f"{key_prefix}:{client_ip}"
    current_time = time.time()
    cutoff_time = current_time - RATE_LIMIT_WINDOW_SECONDS

    # Prune expired timestamps
    valid_timestamps = [t for t in _RATE_LIMIT_CACHE[cache_key] if t > cutoff_time]
    _RATE_LIMIT_CACHE[cache_key] = valid_timestamps

    if len(valid_timestamps) >= MAX_REQUESTS_PER_WINDOW:
        oldest = valid_timestamps[0]
        retry_after = int(RATE_LIMIT_WINDOW_SECONDS - (current_time - oldest))
        return False, max(retry_after, 1)

    _RATE_LIMIT_CACHE[cache_key].append(current_time)
    return True, 0


def rate_limit_exceeded_response(retry_after: int) -> Response:
    """Returns standardized 429 response"""
    resp = jsonify({
        "success": False,
        "message": f"Too many requests. Rate limit exceeded. Try again in {retry_after} seconds."
    })
    resp.status_code = 429
    resp.headers["Retry-After"] = str(retry_after)
    return resp


def add_security_headers(response: Response) -> Response:
    """
    Injects OWASP-recommended security headers into every outgoing HTTP response.
    """
    response.headers["X-Content-Type-Options"] = "nosniff"
    response.headers["X-Frame-Options"] = "DENY"
    response.headers["X-XSS-Protection"] = "1; mode=block"
    response.headers["Referrer-Policy"] = "strict-origin-when-cross-origin"
    
    # HSTS if in production or HTTPS
    if os.getenv("FLASK_ENV") == "production" or request.is_secure:
        response.headers["Strict-Transport-Security"] = "max-age=31536000; includeSubDomains"
        
    return response


def set_auth_cookie(response: Response, token: str, days: int = 7) -> Response:
    """
    Sets a secure, HttpOnly authentication cookie.
    Prevents client-side XSS attacks from accessing the JWT.
    """
    is_secure = os.getenv("COOKIE_SECURE", "false").lower() == "true" or request.is_secure
    response.set_cookie(
        key="attendora_token",
        value=token,
        max_age=days * 24 * 60 * 60,
        httponly=True,
        samesite="Lax",
        secure=is_secure,
        path="/"
    )
    return response


def clear_auth_cookie(response: Response) -> Response:
    """
    Clears the HttpOnly authentication cookie on logout.
    """
    response.set_cookie(
        key="attendora_token",
        value="",
        max_age=0,
        expires=0,
        httponly=True,
        samesite="Lax",
        path="/"
    )
    return response

