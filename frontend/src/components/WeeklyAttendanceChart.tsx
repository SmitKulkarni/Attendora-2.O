import React from 'react';

export const WeeklyAttendanceChart: React.FC = () => {
  const days = [
    { day: 'Mon', present: 78, absent: 52 },
    { day: 'Tue', present: 68, absent: 38 },
    { day: 'Wed', present: 88, absent: 60 },
    { day: 'Thu', present: 82, absent: 22 },
    { day: 'Fri', present: 80, absent: 38 }
  ];

  const maxVal = 100;

  return (
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
          <h3 style={{ margin: 0, fontSize: '16px', fontWeight: '700', color: '#0F172A' }}>Weekly Attendance</h3>
          <p style={{ margin: '2px 0 0', fontSize: '12px', color: '#64748B' }}>Daily breakdown: Present vs Absent</p>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
            <span style={{ width: '8px', height: '8px', borderRadius: '2px', backgroundColor: '#006673' }} />
            <span style={{ fontSize: '11px', color: '#64748B' }}>Present</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
            <span style={{ width: '8px', height: '8px', borderRadius: '2px', backgroundColor: '#EF4444' }} />
            <span style={{ fontSize: '11px', color: '#64748B' }}>Absent</span>
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-around', alignItems: 'flex-end', height: '140px', paddingTop: '10px' }}>
        {days.map((d, i) => {
          const presentHeight = (d.present / maxVal) * 110;
          const absentHeight = (d.absent / maxVal) * 110;

          return (
            <div key={i} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
              <div style={{ display: 'flex', alignItems: 'flex-end', gap: '6px', height: '110px' }}>
                <div
                  title={`Present: ${d.present}`}
                  style={{
                    width: '14px',
                    height: `${presentHeight}px`,
                    backgroundColor: '#006673',
                    borderRadius: '4px 4px 0 0',
                    transition: 'height 0.3s ease'
                  }}
                />
                <div
                  title={`Absent: ${d.absent}`}
                  style={{
                    width: '14px',
                    height: `${absentHeight}px`,
                    backgroundColor: '#EF4444',
                    borderRadius: '4px 4px 0 0',
                    transition: 'height 0.3s ease'
                  }}
                />
              </div>
              <span style={{ fontSize: '11px', fontWeight: '600', color: '#64748B' }}>{d.day}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
};

