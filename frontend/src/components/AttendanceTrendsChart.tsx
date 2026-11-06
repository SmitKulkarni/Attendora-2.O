import React from 'react';

export const AttendanceTrendsChart: React.FC = () => {
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
          <h3 style={{ margin: 0, fontSize: '16px', fontWeight: '700', color: '#0F172A' }}>Attendance Trends</h3>
          <p style={{ margin: '2px 0 0', fontSize: '12px', color: '#64748B' }}>Semester 3-Month Trajectory</p>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#006673' }} />
          <span style={{ fontSize: '12px', color: '#64748B', fontWeight: '500' }}>Overall %</span>
        </div>
      </div>

      <div style={{ position: 'relative', height: '160px', width: '100%' }}>
        <svg viewBox="0 0 400 160" style={{ width: '100%', height: '100%', overflow: 'visible' }}>
          <defs>
            <linearGradient id="trendGradient" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" stopColor="#006673" stopOpacity="0.25" />
              <stop offset="100%" stopColor="#006673" stopOpacity="0.0" />
            </linearGradient>
          </defs>

          {/* Grid lines */}
          <line x1="0" y1="40" x2="400" y2="40" stroke="#F1F5F9" strokeWidth="1" strokeDasharray="4 4" />
          <line x1="0" y1="80" x2="400" y2="80" stroke="#F1F5F9" strokeWidth="1" strokeDasharray="4 4" />
          <line x1="0" y1="120" x2="400" y2="120" stroke="#F1F5F9" strokeWidth="1" strokeDasharray="4 4" />

          {/* Area under curve */}
          <path
            d="M 40,140 Q 150,110 200,60 T 360,20 L 360,150 L 40,150 Z"
            fill="url(#trendGradient)"
          />

          {/* Spline Curve */}
          <path
            d="M 40,140 Q 150,110 200,60 T 360,20"
            fill="none"
            stroke="#006673"
            strokeWidth="3.5"
            strokeLinecap="round"
          />

          {/* Data Points */}
          <circle cx="40" cy="140" r="5" fill="#FFFFFF" stroke="#006673" strokeWidth="3" />
          <text x="40" y="158" textAnchor="middle" fontSize="11" fill="#64748B" fontWeight="600">Sep (12%)</text>

          <circle cx="200" cy="60" r="5" fill="#FFFFFF" stroke="#006673" strokeWidth="3" />
          <text x="200" y="78" textAnchor="middle" fontSize="11" fill="#64748B" fontWeight="600">Oct (58%)</text>

          <circle cx="360" cy="20" r="5" fill="#FFFFFF" stroke="#006673" strokeWidth="3" />
          <text x="360" y="38" textAnchor="middle" fontSize="11" fill="#006673" fontWeight="700">Nov (98%)</text>
        </svg>
      </div>
    </div>
  );
};

