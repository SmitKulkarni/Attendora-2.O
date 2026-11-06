"""
Attendora SaaS - Official Institutional Attendance Transcript PDF Generator
Generates high-fidelity PDF transcripts using ReportLab.
"""
import os
from datetime import datetime
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, HRFlowable
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch

TRANSCRIPTS_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'transcripts')
os.makedirs(TRANSCRIPTS_DIR, exist_ok=True)


def generate_attendance_transcript_pdf(student_data, output_path=None):
    """
    Generates a production-grade PDF attendance transcript for a student.
    """
    student_id = student_data.get('id', 'student')
    if not output_path:
        output_path = os.path.join(TRANSCRIPTS_DIR, f"{student_id}_transcript.pdf")

    doc = SimpleDocTemplate(
        output_path,
        pagesize=letter,
        rightMargin=36,
        leftMargin=36,
        topMargin=36,
        bottomMargin=36
    )

    styles = getSampleStyleSheet()

    # Custom styles
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Heading1'],
        fontName='Helvetica-Bold',
        fontSize=20,
        leading=24,
        textColor=colors.HexColor('#006673'),
        alignment=1 # Center
    )

    subtitle_style = ParagraphStyle(
        'DocSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=14,
        textColor=colors.HexColor('#475569'),
        alignment=1 # Center
    )

    label_style = ParagraphStyle(
        'MetaLabel',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=10,
        leading=13,
        textColor=colors.HexColor('#161C1F')
    )

    val_style = ParagraphStyle(
        'MetaValue',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=10,
        leading=13,
        textColor=colors.HexColor('#334155')
    )

    story = []

    # 1. Header Banner
    story.append(Paragraph("ATTENDORA EDTECH INSTITUTIONAL SYSTEM", title_style))
    story.append(Paragraph("OFFICIAL STUDENT ATTENDANCE TRANSCRIPT & ACADEMIC AUDIT", subtitle_style))
    story.append(Spacer(1, 10))
    story.append(HRFlowable(width="100%", thickness=2, color=colors.HexColor('#006673'), spaceAfter=14))

    # 2. Student Metadata Box
    overall_pct = float(student_data.get('overall_percentage', 0.0))
    is_defaulter = overall_pct < 75.0

    meta_data = [
        [
            Paragraph("<b>Student Name:</b>", label_style),
            Paragraph(student_data.get('name', 'N/A'), val_style),
            Paragraph("<b>Roll Number:</b>", label_style),
            Paragraph(student_data.get('roll_no', 'N/A'), val_style)
        ],
        [
            Paragraph("<b>Department:</b>", label_style),
            Paragraph(student_data.get('department', 'Computer Science'), val_style),
            Paragraph("<b>Academic Term:</b>", label_style),
            Paragraph("Fall 2026", val_style)
        ],
        [
            Paragraph("<b>Class Cohort:</b>", label_style),
            Paragraph(student_data.get('class_name', 'Class 1'), val_style),
            Paragraph("<b>Generated On:</b>", label_style),
            Paragraph(datetime.now().strftime("%B %d, %Y - %I:%M %p"), val_style)
        ]
    ]

    meta_table = Table(meta_data, colWidths=[1.4 * inch, 2.3 * inch, 1.4 * inch, 2.3 * inch])
    meta_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor('#F4FAFE')),
        ('BOX', (0, 0), (-1, -1), 1, colors.HexColor('#CBD5E1')),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#E2E8F0')),
        ('TOPPADDING', (0, 0), (-1, -1), 6),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
    ]))
    story.append(meta_table)
    story.append(Spacer(1, 14))

    # 3. Overall Standing Indicator Box
    standing_color = colors.HexColor('#DC2626') if is_defaulter else colors.HexColor('#16A34A')
    standing_text = "CRITICAL WARNING: DEFAULTER (BELOW 75%)" if is_defaulter else "ACADEMIC GOOD STANDING (ELIGIBLE FOR EXAMINATIONS)"

    standing_data = [
        [
            Paragraph(f"<b>Overall Attendance: {overall_pct}%</b>", ParagraphStyle('Pct', fontName='Helvetica-Bold', fontSize=14, textColor=standing_color, alignment=1)),
            Paragraph(f"<b>Status: {standing_text}</b>", ParagraphStyle('Std', fontName='Helvetica-Bold', fontSize=11, textColor=standing_color, alignment=1))
        ]
    ]
    standing_table = Table(standing_data, colWidths=[2.5 * inch, 4.9 * inch])
    standing_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor('#FEECEB') if is_defaulter else colors.HexColor('#E6F8F0')),
        ('BOX', (0, 0), (-1, -1), 1.5, standing_color),
        ('TOPPADDING', (0, 0), (-1, -1), 8),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 8),
        ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
    ]))
    story.append(standing_table)
    story.append(Spacer(1, 16))

    # 4. Subject Breakdown Table
    story.append(Paragraph("<b>Subject-Level Attendance Breakdown</b>", ParagraphStyle('SecH', fontName='Helvetica-Bold', fontSize=12, textColor=colors.HexColor('#006673'))))
    story.append(Spacer(1, 6))

    table_data = [
        [
            Paragraph("<b>Subject Name</b>", label_style),
            Paragraph("<b>Code</b>", label_style),
            Paragraph("<b>Attended</b>", label_style),
            Paragraph("<b>Total</b>", label_style),
            Paragraph("<b>Percentage</b>", label_style),
            Paragraph("<b>Compliance</b>", label_style)
        ]
    ]

    subjects = student_data.get('subjects', [])
    for subj in subjects:
        s_pct = float(subj.get('percentage', 0.0))
        s_status = "DEFICIENT" if s_pct < 75.0 else "QUALIFIED"
        status_color = colors.HexColor('#DC2626') if s_pct < 75.0 else colors.HexColor('#16A34A')

        table_data.append([
            Paragraph(subj.get('name', ''), val_style),
            Paragraph(subj.get('code', ''), val_style),
            Paragraph(str(subj.get('attended', 0)), val_style),
            Paragraph(str(subj.get('total', 0)), val_style),
            Paragraph(f"<b>{s_pct}%</b>", ParagraphStyle('SubjPct', fontName='Helvetica-Bold', fontSize=10, textColor=status_color)),
            Paragraph(f"<b>{s_status}</b>", ParagraphStyle('SubjStat', fontName='Helvetica-Bold', fontSize=9, textColor=status_color))
        ])

    subj_table = Table(table_data, colWidths=[2.4 * inch, 1.0 * inch, 1.0 * inch, 1.0 * inch, 1.0 * inch, 1.0 * inch])
    subj_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#E2E8F0')),
        ('BOX', (0, 0), (-1, -1), 1, colors.HexColor('#94A3B8')),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E1')),
        ('TOPPADDING', (0, 0), (-1, -1), 6),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
        ('ALIGN', (2, 0), (-1, -1), 'CENTER')
    ]))
    story.append(subj_table)
    story.append(Spacer(1, 24))

    # 5. Statutory Regulations Text
    notice_text = (
        "<b>Institutional Regulations Note:</b> Attendance records are maintained under the Academic Integrity Policy. "
        "A minimum mandatory requirement of 75% aggregate attendance is enforced. Sessions marked 'LATE' are recorded "
        "with verification timestamps and counted towards institutional compliance. For any discrepancy, contact "
        "the Office of the Academic Registrar within 48 hours of transcript generation."
    )
    story.append(Paragraph(notice_text, ParagraphStyle('Note', fontName='Helvetica-Oblique', fontSize=8, leading=11, textColor=colors.HexColor('#64748B'))))
    story.append(Spacer(1, 30))

    # 6. Signatures Footer Block
    sig_data = [
        [
            Paragraph("____________________________<br/><b>Prof. Priya Saxena</b><br/>Faculty In-Charge", label_style),
            Paragraph("____________________________<br/><b>Dr. Arvind Sharma</b><br/>Head of Department", label_style),
            Paragraph("____________________________<br/><b>Dr. K. R. Narayan</b><br/>Dean of Academic Affairs", label_style)
        ]
    ]
    sig_table = Table(sig_data, colWidths=[2.4 * inch, 2.5 * inch, 2.5 * inch])
    sig_table.setStyle(TableStyle([
        ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
    ]))
    story.append(sig_table)

    doc.build(story)
    return output_path

