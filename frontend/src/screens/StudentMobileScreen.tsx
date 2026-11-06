import React from 'react';
import { QrCode, Download, Calendar, CheckCircle2, AlertCircle } from 'lucide-react';
import { Student, AttendanceDayLog } from '../types';

interface StudentMobileScreenProps {
  student: Student;
  dayLogs: AttendanceDayLog[];
  onScanQr: () => void;
  onDownloadTranscript: () => void;
}

export const StudentMobileScreen: React.FC<StudentMobileScreenProps> = ({
  student,
  dayLogs,
  onScanQr,
  onDownloadTranscript
}) => {
  const isCompliant = student.overallPercentage >= 75;

  return (
    <div style={{ maxWidth: '640px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Student Profile & Percentage Card */}
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '20px',
          padding: '24px',
          border: '1px solid #E2E8F0',
          boxShadow: '0 2px 4px rgba(0,0,0,0.04)',
          textAlign: 'center'
        }}
      >
        <div
          style={{
            width: '64px',
            height: '64px',
            borderRadius: '50%',
            backgroundColor: student.avatarBgColor || '#3B82F6',
            color: '#FFFFFF',
            fontSize: '24px',
            fontWeight: '800',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 12px'
          }}
        >
          {student.name.split(' ').map(n => n[0]).slice(0, 2).join('')}
        </div>

        <h2 style={{ margin: 0, fontSize: '20px', fontWeight: '800', color: '#0F172A' }}>
          {student.name}
        </h2>
        <p style={{ margin: '4px 0 16px', fontSize: '13px', color: '#64748B' }}>
          Roll: {student.rollNo} • {student.className} • {student.department}
        </p>

        {/* Circular Metric Ring Representation */}
        <div
          style={{
            display: 'inline-flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            width: '140px',
            height: '140px',
            borderRadius: '50%',
            backgroundColor: isCompliant ? '#F0FDF4' : '#FEF2F2',
            border: `6px solid ${isCompliant ? '#10B981' : '#EF4444'}`,
            margin: '0 auto'
          }}
        >
          <span style={{ fontSize: '32px', fontWeight: '800', color: isCompliant ? '#15803D' : '#B91C1C' }}>
            {student.overallPercentage}%
          </span>
          <span style={{ fontSize: '11px', color: '#64748B', fontWeight: '600' }}>Overall Standing</span>
        </div>

        {/* Action Buttons */}
        <div style={{ display: 'flex', gap: '12px', marginTop: '24px' }}>
          <button
            onClick={onScanQr}
            style={{
              flex: 1,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '8px',
              padding: '12px',
              borderRadius: '12px',
              border: 'none',
              backgroundColor: '#006673',
              color: '#FFFFFF',
              fontWeight: '700',
              fontSize: '14px',
              cursor: 'pointer'
            }}
          >
            <QrCode size={18} />
            Scan Classroom QR
          </button>

          <button
            onClick={onDownloadTranscript}
            style={{
              flex: 1,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '8px',
              padding: '12px',
              borderRadius: '12px',
              border: '1px solid #CBD5E1',
              backgroundColor: '#FFFFFF',
              color: '#334155',
              fontWeight: '600',
              fontSize: '14px',
              cursor: 'pointer'
            }}
          >
            <Download size={18} />
            Transcript PDF
          </button>
        </div>
      </div>

      {/* Subject-Wise Breakdown Cards */}
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '20px',
          padding: '24px',
          border: '1px solid #E2E8F0',
          boxShadow: '0 2px 4px rgba(0,0,0,0.04)'
        }}
      >
        <h3 style={{ margin: '0 0 16px', fontSize: '16px', fontWeight: '800', color: '#0F172A' }}>
          Course Breakdown
        </h3>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {student.subjects.map((sub, i) => {
            const ok = sub.percentage >= 75;
            return (
              <div
                key={i}
                style={{
                  padding: '14px',
                  borderRadius: '12px',
                  backgroundColor: '#F8FAFC',
                  border: '1px solid #E2E8F0'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                  <div>
                    <div style={{ fontSize: '14px', fontWeight: '700', color: '#0F172A' }}>{sub.name}</div>
                    <div style={{ fontSize: '11px', color: '#64748B' }}>Code: {sub.code}</div>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontSize: '15px', fontWeight: '800', color: ok ? '#10B981' : '#EF4444' }}>
                      {sub.percentage}%
                    </div>
                    <div style={{ fontSize: '11px', color: '#64748B' }}>{sub.attended}/{sub.total} sessions</div>
                  </div>
                </div>
                <div style={{ height: '6px', backgroundColor: '#E2E8F0', borderRadius: '3px', overflow: 'hidden' }}>
                  <div
                    style={{
                      height: '100%',
                      width: `${Math.min(100, Math.max(0, sub.percentage))}%`,
                      backgroundColor: ok ? '#006673' : '#EF4444',
                      borderRadius: '3px'
                    }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Recent Lecture Attendance Timeline */}
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '20px',
          padding: '24px',
          border: '1px solid #E2E8F0',
          boxShadow: '0 2px 4px rgba(0,0,0,0.04)'
        }}
      >
        <h3 style={{ margin: '0 0 16px', fontSize: '16px', fontWeight: '800', color: '#0F172A' }}>
          Recent Class Activity
        </h3>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {dayLogs.map((log, i) => (
            <div
              key={i}
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '10px 12px',
                borderBottom: i < dayLogs.length - 1 ? '1px solid #F1F5F9' : 'none'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                <Calendar size={18} color="#006673" />
                <div>
                  <div style={{ fontSize: '13px', fontWeight: '700', color: '#0F172A' }}>{log.subjectTitle}</div>
                  <div style={{ fontSize: '11px', color: '#64748B' }}>{log.date} ({log.dayName}) • {log.markedBy}</div>
                </div>
              </div>
              <span
                style={{
                  fontSize: '11px',
                  fontWeight: '700',
                  padding: '4px 8px',
                  borderRadius: '6px',
                  backgroundColor: log.status === 'PRESENT' ? '#DCFCE7' : log.status === 'LATE' ? '#FEF3C7' : '#FEE2E2',
                  color: log.status === 'PRESENT' ? '#16A34A' : log.status === 'LATE' ? '#D97706' : '#DC2626'
                }}
              >
                {log.status} {log.checkInTime !== '-' ? `(${log.checkInTime})` : ''}
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

