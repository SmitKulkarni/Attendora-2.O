import React from 'react';
import { X, Sparkles, FileText, CheckCircle2 } from 'lucide-react';
import { Student, AttendanceStatus } from '../types';

interface StudentDetailModalProps {
  student: Student | null;
  onClose: () => void;
  onStatusChange: (studentId: string, status: AttendanceStatus) => void;
  onAiEvaluate: (student: Student) => void;
  onGenerateIntervention: (student: Student) => void;
}

export const StudentDetailModal: React.FC<StudentDetailModalProps> = ({
  student,
  onClose,
  onStatusChange,
  onAiEvaluate,
  onGenerateIntervention
}) => {
  if (!student) return null;

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(15, 23, 42, 0.6)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 50,
        padding: '16px'
      }}
    >
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '20px',
          width: '100%',
          maxWidth: '520px',
          maxHeight: '90vh',
          overflowY: 'auto',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
          padding: '24px'
        }}
      >
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 style={{ margin: 0, fontSize: '18px', fontWeight: '700', color: '#0F172A' }}>
            Student Profile & Roster
          </h3>
          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              cursor: 'pointer',
              color: '#64748B',
              padding: '4px',
              borderRadius: '8px'
            }}
          >
            <X size={20} />
          </button>
        </div>

        {/* Profile Card */}
        <div
          style={{
            backgroundColor: '#F8FAFC',
            borderRadius: '12px',
            padding: '16px',
            marginTop: '16px',
            display: 'flex',
            alignItems: 'center',
            gap: '14px',
            border: '1px solid #E2E8F0'
          }}
        >
          <div
            style={{
              width: '48px',
              height: '48px',
              borderRadius: '50%',
              backgroundColor: student.avatarBgColor || '#3B82F6',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#FFFFFF',
              fontWeight: '700',
              fontSize: '16px'
            }}
          >
            {student.name.split(' ').map(n => n[0]).slice(0, 2).join('')}
          </div>
          <div style={{ flex: 1 }}>
            <div style={{ fontSize: '16px', fontWeight: '700', color: '#0F172A' }}>{student.name}</div>
            <div style={{ fontSize: '12px', color: '#64748B' }}>
              Roll: {student.rollNo} • {student.className}
            </div>
            <div style={{ fontSize: '11px', color: '#006673', fontWeight: '600' }}>{student.department}</div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div
              style={{
                fontSize: '18px',
                fontWeight: '800',
                color: student.overallPercentage >= 75 ? '#10B981' : '#EF4444'
              }}
            >
              {student.overallPercentage}%
            </div>
            <div style={{ fontSize: '10px', color: '#94A3B8' }}>Cumulative</div>
          </div>
        </div>

        {/* Subject-Wise Attendance */}
        <div style={{ marginTop: '20px' }}>
          <h4 style={{ margin: '0 0 10px', fontSize: '14px', fontWeight: '700', color: '#0F172A' }}>
            Subject-Wise Attendance
          </h4>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {student.subjects && student.subjects.length > 0 ? (
              student.subjects.map((sub, i) => (
                <div key={i}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', marginBottom: '4px' }}>
                    <span style={{ fontWeight: '500', color: '#334155' }}>{sub.name} ({sub.code})</span>
                    <span
                      style={{
                        fontWeight: '600',
                        color: sub.percentage >= 75 ? '#10B981' : '#EF4444'
                      }}
                    >
                      {sub.attended}/{sub.total} ({sub.percentage}%)
                    </span>
                  </div>
                  <div style={{ height: '6px', backgroundColor: '#E2E8F0', borderRadius: '3px', overflow: 'hidden' }}>
                    <div
                      style={{
                        height: '100%',
                        width: `${Math.min(100, Math.max(0, sub.percentage))}%`,
                        backgroundColor: sub.percentage >= 75 ? '#006673' : '#EF4444',
                        borderRadius: '3px'
                      }}
                    />
                  </div>
                </div>
              ))
            ) : (
              <p style={{ fontSize: '12px', color: '#94A3B8' }}>No individual subject breakdown recorded.</p>
            )}
          </div>
        </div>

        {/* Gemini AI Predictive Intelligence */}
        <div style={{ marginTop: '20px', padding: '14px', backgroundColor: '#F0FDFA', borderRadius: '12px', border: '1px solid #CCFBF1' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#006673', fontWeight: '700', fontSize: '13px' }}>
            <Sparkles size={16} />
            Gemini AI Predictive Intelligence
          </div>
          <p style={{ margin: '4px 0 12px', fontSize: '11px', color: '#0D9488' }}>
            Analyze multi-course attendance vectors and evaluate course completion probability.
          </p>
          <div style={{ display: 'flex', gap: '10px' }}>
            <button
              onClick={() => onAiEvaluate(student)}
              style={{
                flex: 1,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '6px',
                padding: '8px 12px',
                borderRadius: '8px',
                backgroundColor: '#006673',
                color: '#FFFFFF',
                border: 'none',
                fontWeight: '600',
                fontSize: '12px',
                cursor: 'pointer'
              }}
            >
              <Sparkles size={14} />
              AI Risk Forecast
            </button>
            <button
              onClick={() => onGenerateIntervention(student)}
              style={{
                flex: 1,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '6px',
                padding: '8px 12px',
                borderRadius: '8px',
                backgroundColor: '#FFFFFF',
                color: '#006673',
                border: '1px solid #006673',
                fontWeight: '600',
                fontSize: '12px',
                cursor: 'pointer'
              }}
            >
              <FileText size={14} />
              Remediation Plan
            </button>
          </div>
        </div>

        {/* Quick Status Override */}
        <div style={{ marginTop: '20px' }}>
          <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748B', display: 'block', marginBottom: '8px' }}>
            Update Today's Status
          </label>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '8px' }}>
            {(['PRESENT', 'ABSENT', 'LATE'] as AttendanceStatus[]).map(status => {
              const isSelected = student.status === status;
              return (
                <button
                  key={status}
                  onClick={() => onStatusChange(student.id, status)}
                  style={{
                    padding: '8px',
                    borderRadius: '8px',
                    border: isSelected ? '2px solid #006673' : '1px solid #CBD5E1',
                    backgroundColor: isSelected ? '#E0F7FA' : '#FFFFFF',
                    color: isSelected ? '#006673' : '#334155',
                    fontWeight: isSelected ? '700' : '500',
                    fontSize: '12px',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '4px'
                  }}
                >
                  {isSelected && <CheckCircle2 size={14} />}
                  {status}
                </button>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};

