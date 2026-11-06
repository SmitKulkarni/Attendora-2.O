import React from 'react';
import { QrCode, CheckCircle2, XCircle, Send } from 'lucide-react';
import { Student, ClassGroup, AttendanceStatus } from '../types';
import { StatusPill } from '../components/StatusPill';

interface AttendanceMarkingScreenProps {
  classes: ClassGroup[];
  students: Student[];
  selectedClass: string;
  onSelectClass: (cls: string) => void;
  onToggleStatus: (studentId: string) => void;
  onMarkAll: (status: AttendanceStatus) => void;
  onSubmitSession: () => void;
  onStartQrSession: () => void;
}

export const AttendanceMarkingScreen: React.FC<AttendanceMarkingScreenProps> = ({
  classes,
  students,
  selectedClass,
  onSelectClass,
  onToggleStatus,
  onMarkAll,
  onSubmitSession,
  onStartQrSession
}) => {
  const filteredStudents = selectedClass === 'All'
    ? students
    : students.filter(s => s.className === selectedClass);

  const presentCount = filteredStudents.filter(s => s.status === 'PRESENT').length;
  const absentCount = filteredStudents.filter(s => s.status === 'ABSENT').length;
  const lateCount = filteredStudents.filter(s => s.status === 'LATE').length;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Top Controls Card */}
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '16px',
          padding: '20px',
          border: '1px solid #E2E8F0',
          boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
          display: 'flex',
          flexWrap: 'wrap',
          justifyContent: 'space-between',
          alignItems: 'center',
          gap: '16px'
        }}
      >
        <div>
          <h2 style={{ margin: 0, fontSize: '18px', fontWeight: '800', color: '#0F172A' }}>
            Classroom Attendance Marking
          </h2>
          <p style={{ margin: '4px 0 0', fontSize: '12px', color: '#64748B' }}>
            Single-tap roll call with 48-hour faculty modification window
          </p>
        </div>

        {/* Action Buttons */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' }}>
          {/* Class Filter */}
          <select
            value={selectedClass}
            onChange={e => onSelectClass(e.target.value)}
            style={{
              padding: '8px 14px',
              borderRadius: '10px',
              border: '1px solid #CBD5E1',
              backgroundColor: '#FFFFFF',
              color: '#0F172A',
              fontWeight: '600',
              fontSize: '13px',
              outline: 'none',
              cursor: 'pointer'
            }}
          >
            <option value="All">All Classes ({students.length})</option>
            {classes.map(c => (
              <option key={c.id} value={c.name}>
                {c.name} ({c.code})
              </option>
            ))}
          </select>

          {/* Smart QR Session */}
          <button
            onClick={onStartQrSession}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '8px 14px',
              borderRadius: '10px',
              border: '1px solid #006673',
              backgroundColor: '#E0F7FA',
              color: '#006673',
              fontWeight: '700',
              fontSize: '13px',
              cursor: 'pointer'
            }}
          >
            <QrCode size={16} />
            Smart QR Session
          </button>

          {/* Mark All Present */}
          <button
            onClick={() => onMarkAll('PRESENT')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '8px 14px',
              borderRadius: '10px',
              border: 'none',
              backgroundColor: '#10B981',
              color: '#FFFFFF',
              fontWeight: '600',
              fontSize: '13px',
              cursor: 'pointer'
            }}
          >
            <CheckCircle2 size={16} />
            All Present
          </button>

          {/* Mark All Absent */}
          <button
            onClick={() => onMarkAll('ABSENT')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '8px 14px',
              borderRadius: '10px',
              border: 'none',
              backgroundColor: '#EF4444',
              color: '#FFFFFF',
              fontWeight: '600',
              fontSize: '13px',
              cursor: 'pointer'
            }}
          >
            <XCircle size={16} />
            All Absent
          </button>

          {/* Submit Session */}
          <button
            onClick={onSubmitSession}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '8px 16px',
              borderRadius: '10px',
              border: 'none',
              backgroundColor: '#006673',
              color: '#FFFFFF',
              fontWeight: '700',
              fontSize: '13px',
              cursor: 'pointer'
            }}
          >
            <Send size={16} />
            Submit Session
          </button>
        </div>
      </div>

      {/* Summary Bar */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(4, 1fr)',
          gap: '12px'
        }}
      >
        <div style={{ backgroundColor: '#FFFFFF', padding: '12px 16px', borderRadius: '12px', border: '1px solid #E2E8F0' }}>
          <div style={{ fontSize: '11px', color: '#64748B' }}>Total in View</div>
          <div style={{ fontSize: '18px', fontWeight: '800', color: '#0F172A' }}>{filteredStudents.length}</div>
        </div>
        <div style={{ backgroundColor: '#F0FDF4', padding: '12px 16px', borderRadius: '12px', border: '1px solid #BBF7D0' }}>
          <div style={{ fontSize: '11px', color: '#16A34A' }}>Present Count</div>
          <div style={{ fontSize: '18px', fontWeight: '800', color: '#15803D' }}>{presentCount}</div>
        </div>
        <div style={{ backgroundColor: '#FEF2F2', padding: '12px 16px', borderRadius: '12px', border: '1px solid #FECACA' }}>
          <div style={{ fontSize: '11px', color: '#DC2626' }}>Absent Count</div>
          <div style={{ fontSize: '18px', fontWeight: '800', color: '#B91C1C' }}>{absentCount}</div>
        </div>
        <div style={{ backgroundColor: '#FFFBEB', padding: '12px 16px', borderRadius: '12px', border: '1px solid #FDE68A' }}>
          <div style={{ fontSize: '11px', color: '#D97706' }}>Late Entry</div>
          <div style={{ fontSize: '18px', fontWeight: '800', color: '#B45309' }}>{lateCount}</div>
        </div>
      </div>

      {/* Roster Cards Grid */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
          gap: '14px'
        }}
      >
        {filteredStudents.map(s => (
          <div
            key={s.id}
            onClick={() => onToggleStatus(s.id)}
            style={{
              backgroundColor: '#FFFFFF',
              borderRadius: '14px',
              padding: '16px',
              border: '1px solid #E2E8F0',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              cursor: 'pointer',
              transition: 'all 0.15s ease',
              boxShadow: '0 1px 2px rgba(0,0,0,0.03)'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <div
                style={{
                  width: '40px',
                  height: '40px',
                  borderRadius: '50%',
                  backgroundColor: s.avatarBgColor || '#3B82F6',
                  color: '#FFFFFF',
                  fontWeight: '700',
                  fontSize: '14px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }}
              >
                {s.name.split(' ').map(n => n[0]).slice(0, 2).join('')}
              </div>
              <div>
                <div style={{ fontSize: '14px', fontWeight: '700', color: '#0F172A' }}>{s.name}</div>
                <div style={{ fontSize: '12px', color: '#64748B' }}>Roll: {s.rollNo}</div>
                <div style={{ fontSize: '11px', color: '#94A3B8' }}>{s.checkInTime}</div>
              </div>
            </div>

            <div>
              <StatusPill status={s.status} />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

