import React from 'react';
import { AttendanceStatus } from '../types';

interface StatusPillProps {
  status: AttendanceStatus;
  onClick?: () => void;
}

export const StatusPill: React.FC<StatusPillProps> = ({ status, onClick }) => {
  const getStyles = () => {
    switch (status) {
      case 'PRESENT':
        return {
          bg: '#DCFCE7',
          text: '#15803D',
          border: '#86EFAC',
          label: 'Present'
        };
      case 'ABSENT':
        return {
          bg: '#FFE4E6',
          text: '#BE123C',
          border: '#FDA4AF',
          label: 'Absent'
        };
      case 'LATE':
        return {
          bg: '#FEF3C7',
          text: '#B45309',
          border: '#FCD34D',
          label: 'Late'
        };
    }
  };

  const s = getStyles();

  return (
    <span
      onClick={onClick}
      style={{
        backgroundColor: s.bg,
        color: s.text,
        border: `1px solid ${s.border}`,
        padding: '4px 10px',
        borderRadius: '9999px',
        fontSize: '12px',
        fontWeight: '600',
        cursor: onClick ? 'pointer' : 'default',
        display: 'inline-flex',
        alignItems: 'center',
        gap: '4px',
        transition: 'all 0.15s ease'
      }}
    >
      <span style={{ width: '6px', height: '6px', borderRadius: '50%', backgroundColor: s.text }} />
      {s.label}
    </span>
  );
};

