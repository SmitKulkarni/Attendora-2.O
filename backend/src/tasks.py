"""
Attendora SaaS - Background Worker Tasks
Handles asynchronous bulk email alerts, PDF transcript generation, and nightly defaulter audits.
"""
import os
import time
import uuid
import logging
from concurrent.futures import ThreadPoolExecutor
from celery_app import celery_app, CELERY_AVAILABLE
from db import get_in_memory_db
from emailer import render_defaulter_email, send_email
from pdf_generator import generate_attendance_transcript_pdf

logger = logging.getLogger(__name__)

# Local asynchronous thread pool for development & automated tests
_local_executor = ThreadPoolExecutor(max_workers=4)

# In-memory status map for tracking async jobs
TASK_STATUS_MAP = {}


def register_task(task_id, name):
    TASK_STATUS_MAP[task_id] = {
        "task_id": task_id,
        "name": name,
        "status": "PENDING",
        "progress": 0,
        "result": None,
        "created_at": time.time()
    }


def update_task(task_id, status, progress=100, result=None):
    if task_id in TASK_STATUS_MAP:
        TASK_STATUS_MAP[task_id].update({
            "status": status,
            "progress": progress,
            "result": result,
            "completed_at": time.time()
        })


def get_task_status(task_id):
    return TASK_STATUS_MAP.get(task_id)


def audit_and_generate_defaulters(threshold=75.0, institution_id="00000000-0000-0000-0000-000000000001"):
    """
    Audits all students, calculates defaulters below threshold, and queues notifications.
    """
    mem_db = get_in_memory_db()
    students = mem_db.get("students", [])
    defaulters = [
        s for s in students
        if float(s.get("overall_percentage", 100)) < threshold
    ]

    sent_count = 0
    for student in defaulters:
        html = render_defaulter_email(student, threshold=threshold)
        subject = f"CRITICAL: Attendance Defaulter Warning (< {threshold}%)"
        success = send_email(student.get("email"), subject, html)
        if success:
            sent_count += 1

    return {
        "audited_total": len(students),
        "defaulters_found": len(defaulters),
        "alerts_dispatched": sent_count,
        "threshold": threshold
    }


def generate_transcript_task(student_id):
    """
    Generates official PDF transcript asynchronously.
    """
    mem_db = get_in_memory_db()
    student = next((s for s in mem_db.get("students", []) if s["id"] == student_id), None)
    if not student:
        raise ValueError(f"Student {student_id} not found")

    pdf_path = generate_attendance_transcript_pdf(student)
    return {
        "student_id": student_id,
        "pdf_path": pdf_path,
        "file_size": os.path.getsize(pdf_path) if os.path.exists(pdf_path) else 0
    }


# Wrap in Celery task decorators if Celery is available
if CELERY_AVAILABLE and celery_app:
    audit_and_generate_defaulters_task = celery_app.task(audit_and_generate_defaulters)
    generate_transcript_celery_task = celery_app.task(generate_transcript_task)


def enqueue_task(task_func, *args, **kwargs):
    """
    Enqueues an asynchronous task to Celery if available, or to local thread pool.
    Returns task_id string.
    """
    task_id = f"task_{uuid.uuid4().hex[:12]}"
    task_name = task_func.__name__
    register_task(task_id, task_name)

    def _worker():
        try:
            update_task(task_id, "RUNNING", progress=25)
            res = task_func(*args, **kwargs)
            update_task(task_id, "SUCCESS", progress=100, result=res)
        except Exception as e:
            logger.error(f"Task {task_id} failed: {e}")
            update_task(task_id, "FAILURE", progress=100, result=str(e))

    _local_executor.submit(_worker)
    return task_id
