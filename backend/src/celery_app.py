"""
Attendora SaaS - Celery Distributed Task Queue Configuration
"""
import os

CELERY_AVAILABLE = False
celery_app = None

try:
    from celery import Celery
    redis_url = os.getenv('REDIS_URL', 'redis://localhost:6379/0')
    celery_app = Celery(
        'attendora_tasks',
        broker=redis_url,
        backend=redis_url
    )
    celery_app.conf.update(
        task_serializer='json',
        accept_content=['json'],
        result_serializer='json',
        timezone='UTC',
        enable_utc=True,
        beat_schedule={
            'nightly-defaulter-audit': {
                'task': 'tasks.audit_and_generate_defaulters',
                'schedule': 86400.0, # Every 24 hours
                'args': (75.0,)
            }
        }
    )
    CELERY_AVAILABLE = True
except Exception:
    CELERY_AVAILABLE = False
    celery_app = None

