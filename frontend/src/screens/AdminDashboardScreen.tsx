import React from 'react';
import { MetricCards } from '../components/MetricCards';
import { AttendanceTrendsChart } from '../components/AttendanceTrendsChart';
import { WeeklyAttendanceChart } from '../components/WeeklyAttendanceChart';
import { StatusPill } from '../components/StatusPill';
import { Student } from '../types';

interface AdminDashboardScreenProps {
  students: Student[];
  onStudentClick: (student: Student) => void;
  onToggleStatus: (studentId: string) => void;
}

export const AdminDashboardScreen: React.FC<AdminDashboardScreenProps> = ({
  students,
  onStudentClick,
  onToggleStatus
}) => {
  const presentCount = students.filter(s => s.status === 'PRESENT').length;
  const lateCount = students.filter(s => s.status === 'LATE').length;
  const absentCount = students.filter(s => s.status === 'ABSENT').length;
  const todayPercent = students.length > 0 ? Math.round(((presentCount + lateCount) / students.length) * 100) : 94;
  const onTimePercent = students.length > 0 ? Math.round((presentCount / students.length) * 100) : 88;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* 4 Metric Cards */}
      <MetricCards
        totalStudents={120}
        todayPercent={todayPercent}
        onTimePercent={onTimePercent}
        absentCount={absentCount}
      />

      {/* 2 Canvas/SVG Charts */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '20px' }}>
        <AttendanceTrendsChart />
        <WeeklyAttendanceChart />
      </div>

      {/* Today's Roster Table */}
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '16px',
          padding: '20px',
          border: '1px solid #E2E8F0',
          boxShadow: '0 1px 3px rgba(0,0,0,0.05)'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <div>
            <h3 style={{ margin: 0, fontSize: '16px', fontWeight: '700', color: '#0F172A' }}>
              Real-Time Class Roster
            </h3>
            <p style={{ margin: '2px 0 0', fontSize: '12px', color: '#64748B' }}>
              Tap any status pill to toggle Present / Absent / Late
            </p>
          </div>
          <span style={{ fontSize: '12px', fontWeight: '600', color: '#006673' }}>
            {students.length} Students Active
          </span>
        </div>

        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '13px' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid #E2E8F0', color: '#64748B' }}>
                <th style={{ padding: '10px 14px', fontWeight: '600' }}>Student Name</th>
                <th style={{ padding: '10px 14px', fontWeight: '600' }}>Roll No</th>
                <th style={{ padding: '10px 14px', fontWeight: '600' }}>Class</th>
                <th style={{ padding: '10px 14px', fontWeight: '600' }}>Check-in</th>
                <th style={{ padding: '10px 14px', fontWeight: '600' }}>Standing</th>
                <th style={{ padding: '10px 14px', fontWeight: '600', textAlign: 'right' }}>Status</th>
              </tr>
            </thead>
            <tbody>
              {students.map(s => (
                <tr
                  key={s.id}
                  style={{
                    borderBottom: '1px solid #F1F5F9',
                    cursor: 'pointer',
                    transition: 'background 0.1s'
                  }}
                  onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#F8FAFC')}
                  onMouseLeave={e => (e.currentTarget.style.backgroundColor = 'transparent')}
                >
                  <td
                    onClick={() => onStudentClick(s)}
                    style={{ padding: '12px 14px', fontWeight: '600', color: '#0F172A' }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <div
                        style={{
                          width: '32px',
                          height: '32px',
                          borderRadius: '50%',
                          backgroundColor: s.avatarBgColor || '#3B82F6',
                          color: '#FFFFFF',
                          fontWeight: '700',
                          fontSize: '12px',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center'
                        }}
                      >
                        {s.name.split(' ').map(n => n[0]).slice(0, 2).join('')}
                      </div>
                      <div>
                        <div>{s.name}</div>
                        <div style={{ fontSize: '11px', color: '#94A3B8' }}>{s.department}</div>
                      </div>
                    </div>
                  </td>
                  <td onClick={() => onStudentClick(s)} style={{ padding: '12px 14px', color: '#64748B' }}>
                    {s.rollNo}
                  </td>
                  <td onClick={() => onStudentClick(s)} style={{ padding: '12px 14px', color: '#64748B' }}>
                    {s.className}
                  </td>
                  <td onClick={() => onStudentClick(s)} style={{ padding: '12px 14px', color: '#64748B' }}>
                    {s.checkInTime}
                  </td>
                  <td onClick={() => onStudentClick(s)} style={{ padding: '12px 14px' }}>
                    <span
                      style={{
                        fontWeight: '700',
                        color: s.overallPercentage >= 75 ? '#10B981' : '#EF4444'
                      }}
                    >
                      {s.overallPercentage}%
                    </span>
                  </td>
                  <td style={{ padding: '12px 14px', textAlign: 'right' }}>
                    <StatusPill status={s.status} onClick={() => onToggleStatus(s.id)} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

