"""
Attendora SaaS - Automated Batch Email Dispatcher
"""
import os
import logging
from jinja2 import Environment, FileSystemLoader

logger = logging.getLogger(__name__)

TEMPLATES_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'templates')
jinja_env = Environment(loader=FileSystemLoader(TEMPLATES_DIR))

# In-memory sent spool for verification and testing
SENT_EMAILS_LOG = []


def render_defaulter_email(student_data, threshold=75.0, notice_id=None, portal_url="http://attendora.edu"):
    """
    Renders the responsive HTML email template for a defaulter student.
    """
    template = jinja_env.get_template('defaulter_alert.html')
    return template.render(
        student_name=student_data.get('name', 'Student'),
        roll_no=student_data.get('roll_no', 'N/A'),
        student_email=student_data.get('email', 'student@attendora.edu'),
        overall_percentage=student_data.get('overall_percentage', 0.0),
        threshold=threshold,
        subjects=student_data.get('subjects', []),
        notice_id=notice_id or f"notice_{student_data.get('id', '0')}",
        portal_url=portal_url
    )


def render_verification_email(user_name: str, verification_code: str) -> str:
    """
    Renders the responsive HTML email template for account email verification.
    """
    try:
        template = jinja_env.get_template('verification_email.html')
        return template.render(
            user_name=user_name,
            verification_code=verification_code
        )
    except Exception:
        return f"""
        <div style="font-family:sans-serif;padding:20px;max-width:500px;">
          <h2 style="color:#006673;">Attendora - Email Verification</h2>
          <p>Hello {user_name},</p>
          <p>Your 6-digit email verification code is:</p>
          <div style="font-size:32px;font-weight:bold;letter-spacing:6px;color:#006673;padding:12px;background:#e0f7fa;">{verification_code}</div>
          <p>This code is valid for 15 minutes.</p>
        </div>
        """


def send_verification_email(to_email: str, user_name: str, verification_code: str) -> bool:
    """
    Sends the 6-digit email verification code to the target user.
    """
    html = render_verification_email(user_name, verification_code)
    subject = f"Your Attendora Verification Code: {verification_code}"
    return send_email(to_email, subject, html)


def send_email(to_email, subject, html_content):
    """
    Dispatches email via configured transport (SendGrid/SMTP or local testing spool).
    """
    sendgrid_api_key = os.getenv('SENDGRID_API_KEY')
    
    if sendgrid_api_key:
        # Production SendGrid dispatch
        try:
            import requests
            headers = {
                "Authorization": f"Bearer {sendgrid_api_key}",
                "Content-Type": "application/json"
            }
            payload = {
                "personalizations": [{"to": [{"email": to_email}]}],
                "from": {"email": os.getenv("FROM_EMAIL", "notifications@attendora.edu"), "name": "Attendora Academic Office"},
                "subject": subject,
                "content": [{"type": "text/html", "value": html_content}]
            }
            resp = requests.post("https://api.sendgrid.com/v3/mail/send", json=payload, headers=headers, timeout=10)
            success = resp.status_code in [200, 202]
        except Exception as e:
            logger.error(f"Failed to send email via SendGrid: {e}")
            success = False
    else:
        # Development / Test mode: Record to in-memory spool
        success = True

    record = {
        "to": to_email,
        "subject": subject,
        "content_length": len(html_content),
        "status": "SENT" if success else "FAILED"
    }
    SENT_EMAILS_LOG.append(record)
    return success


def get_sent_emails():
    return SENT_EMAILS_LOG


def clear_sent_emails():
    SENT_EMAILS_LOG.clear()

