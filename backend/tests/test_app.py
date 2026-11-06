"""
Attendora SaaS Backend API - Unit Tests
"""
import os
import sys
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'src')))

import pytest
from app import app


@pytest.fixture
def client():
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client


def test_health_check(client):
    response = client.get('/health')
    assert response.status_code == 200
    data = response.get_json()
    assert data['status'] == 'healthy'
    assert data['version'] == '2.2.0'


def test_get_attendance_summary(client):
    headers = {'X-Dev-Role': 'ADMIN'}
    response = client.get('/api/v1/attendance/summary', headers=headers)
    assert response.status_code == 200
    data = response.get_json()
    assert 'total_students' in data
    assert 'today_attendance_percent' in data
    assert 'trends' in data
    assert 'weekly_data' in data
    assert len(data['trends']) == 3


def test_get_students(client):
    headers = {'X-Dev-Role': 'TEACHER'}
    response = client.get('/api/v1/students', headers=headers)
    assert response.status_code == 200
    data = response.get_json()
    assert 'students' in data
    assert data['total'] >= 1


def test_submit_attendance(client):
    headers = {'X-Dev-Role': 'TEACHER'}
    payload = {
        'class_id': 'Class 1',
        'records': [
            {'id': 's_1', 'status': 'PRESENT'},
            {'id': 's_2', 'status': 'ABSENT'}
        ]
    }
    response = client.post('/api/v1/attendance', json=payload, headers=headers)
    assert response.status_code == 201
    data = response.get_json()
    assert data['success'] is True
    assert data['records_processed'] == 2


def test_student_dashboard(client):
    headers = {'X-Dev-Role': 'STUDENT', 'X-Dev-User-ID': 's_1'}
    response = client.get('/api/v1/student/dashboard', headers=headers)
    assert response.status_code == 200
    data = response.get_json()
    assert data['name'] == 'Smit Kulkarni'
    assert 'overall_percentage' in data
    assert len(data['subjects']) >= 1


def test_defaulter_reporting(client):
    headers = {'X-Dev-Role': 'ADMIN'}
    response = client.get('/api/v1/reports/defaulters?threshold=75', headers=headers)
    assert response.status_code == 200
    data = response.get_json()
    assert 'defaulter_count' in data
    # Aarav Sharma has 61.8% and should be detected
    assert data['defaulter_count'] >= 1


def test_role_forbidden_check(client):
    # A student attempting to create a new class should be rejected with 403 Forbidden
    headers = {'X-Dev-Role': 'STUDENT'}
    payload = {'name': 'Unauthorized Class', 'code': 'CS-999'}
    response = client.post('/api/v1/classes', json=payload, headers=headers)
    assert response.status_code == 403
