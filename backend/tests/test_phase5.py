"""
Attendora SaaS - Phase 5 Automated Test Suite
Verifies Dynamic TOTP QR, GPS Geofencing, and Anti-Proxy Device Fingerprinting.
"""
import os
import sys
import time
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'src')))

import pytest
from app import app
from geofence import calculate_haversine_distance_meters, verify_classroom_geofence
from qr_engine import (
    generate_dynamic_qr_payload, verify_qr_token,
    verify_device_uniqueness, clear_device_registry
)


@pytest.fixture
def client():
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client


def test_haversine_distance_calculation():
    # Hall A-101 (Pune College of Engineering area: 18.52043, 73.85674)
    c_lat, c_lon = 18.52043, 73.85674

    # Point ~15 meters away
    s_lat, s_lon = 18.52055, 73.85674
    dist = calculate_haversine_distance_meters(c_lat, c_lon, s_lat, s_lon)
    assert 10.0 <= dist <= 20.0

    # Inside geofence test (50m radius)
    is_inside, dist = verify_classroom_geofence(c_lat, c_lon, s_lat, s_lon, allowed_radius_meters=50.0)
    assert is_inside is True

    # Point ~500 meters away (Outside campus)
    far_lat, far_lon = 18.52500, 73.85674
    is_inside_far, dist_far = verify_classroom_geofence(c_lat, c_lon, far_lat, far_lon, allowed_radius_meters=50.0)
    assert is_inside_far is False
    assert dist_far > 400.0


def test_dynamic_qr_payload_and_verification():
    res = generate_dynamic_qr_payload("Class 1", "sess_999", "inst_demo")
    assert "qr_token" in res
    assert res["expires_in_seconds"] > 0
    assert res["expires_in_seconds"] <= 10

    # Verify token
    payload = verify_qr_token(res["qr_token"], expected_class_id="Class 1")
    assert payload["cid"] == "Class 1"
    assert payload["sid"] == "sess_999"


def test_tampered_qr_token_rejection():
    res = generate_dynamic_qr_payload("Class 1", "sess_999", "inst_demo")
    token = res["qr_token"]

    # Tamper with the signature
    tampered_token = token[:-4] + "ABCD"
    with pytest.raises(ValueError, match="Signature mismatch"):
        verify_qr_token(tampered_token)


def test_anti_proxy_device_uniqueness():
    clear_device_registry()
    session_id = "sess_bio_101"
    device_id = "device_smit_pixel_9"

    # Student 1 check in with device -> OK
    assert verify_device_uniqueness(session_id, "s_1", device_id) is True

    # Same student re-scans with same device -> OK
    assert verify_device_uniqueness(session_id, "s_1", device_id) is True

    # Student 2 tries to use Student 1's device -> PROXY DETECTED & BLOCKED
    with pytest.raises(ValueError, match="Proxy Attendance Violation"):
        verify_device_uniqueness(session_id, "s_2", device_id)


def test_qr_generate_api_endpoint(client):
    headers = {'X-Dev-Role': 'TEACHER'}
    payload = {'class_id': 'Class 1', 'session_id': 'sess_api_test'}
    response = client.post('/api/v1/attendance/qr/generate', json=payload, headers=headers)
    assert response.status_code == 200
    data = response.get_json()
    assert data['success'] is True
    assert 'qr_token' in data
    assert 'classroom_location' in data
    assert data['classroom_location']['allowed_radius_meters'] == 50.0


def test_qr_checkin_success_api(client):
    clear_device_registry()
    # 1. Generate QR token
    t_headers = {'X-Dev-Role': 'TEACHER'}
    gen_resp = client.post('/api/v1/attendance/qr/generate', json={'class_id': 'Class 1', 'session_id': 'sess_chk_ok'}, headers=t_headers)
    token = gen_resp.get_json()['qr_token']

    # 2. Student Check-in within classroom (18.52043, 73.85674)
    s_headers = {'X-Dev-Role': 'STUDENT', 'X-Dev-User-ID': 's_2'}
    checkin_payload = {
        'qr_token': token,
        'student_id': 's_2',
        'latitude': 18.52044,
        'longitude': 73.85675,
        'device_id': 'device_aarav_s2'
    }
    response = client.post('/api/v1/attendance/qr/checkin', json=checkin_payload, headers=s_headers)
    assert response.status_code == 200
    data = response.get_json()
    assert data['success'] is True
    assert data['status'] == 'PRESENT'
    assert data['distance_meters'] <= 50.0


def test_qr_checkin_geofence_violation(client):
    # 1. Generate QR token
    t_headers = {'X-Dev-Role': 'TEACHER'}
    gen_resp = client.post('/api/v1/attendance/qr/generate', json={'class_id': 'Class 1', 'session_id': 'sess_chk_geo'}, headers=t_headers)
    token = gen_resp.get_json()['qr_token']

    # 2. Student attempts check-in from 3 kilometers away
    s_headers = {'X-Dev-Role': 'STUDENT', 'X-Dev-User-ID': 's_3'}
    checkin_payload = {
        'qr_token': token,
        'student_id': 's_3',
        'latitude': 18.55000,
        'longitude': 73.85674,
        'device_id': 'device_priya_s3'
    }
    response = client.post('/api/v1/attendance/qr/checkin', json=checkin_payload, headers=s_headers)
    assert response.status_code == 403
    data = response.get_json()
    assert 'Geofence Violation' in data['error']
    assert data['distance_meters'] > 50.0

