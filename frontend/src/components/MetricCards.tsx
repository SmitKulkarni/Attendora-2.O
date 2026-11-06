import React from 'react';
import { Users, CheckCircle2, Clock, UserX } from 'lucide-react';

interface MetricCardsProps {
  totalStudents?: number;
  todayPercent?: number;
  onTimePercent?: number;
  absentCount?: number;
}

export const MetricCards: React.FC<MetricCardsProps> = ({
  totalStudents = 120,
  todayPercent = 94,
  onTimePercent = 88,
  absentCount = 7
}) => {
  const cards = [
    {
      title: 'Total Enrolled',
      value: totalStudents.toString(),
      subtext: 'Across all active class cohorts',
      icon: Users,
      color: '#006673',
      bgColor: '#E0F7FA'
    },
    {
      title: "Today's Attendance",
      value: `${todayPercent}%`,
      subtext: '+2.4% vs last week average',
      icon: CheckCircle2,
      color: '#10B981',
      bgColor: '#DCFCE7'
    },
    {
      title: 'On-Time Rate',
      value: `${onTimePercent}%`,
      subtext: 'Class entry before 09:15 AM',
      icon: Clock,
      color: '#3B82F6',
      bgColor: '#DBEAFE'
    },
    {
      title: 'Absent Defaulters',
      value: absentCount.toString(),
      subtext: 'Requires counseling alert',
      icon: UserX,
      color: '#EF4444',
      bgColor: '#FEE2E2'
    }
  ];

  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
      {cards.map((c, i) => {
        const IconComponent = c.icon;
        return (
          <div
            key={i}
            style={{
              backgroundColor: '#FFFFFF',
              borderRadius: '16px',
              padding: '20px',
              border: '1px solid #E2E8F0',
              display: 'flex',
              flexDirection: 'column',
              boxShadow: '0 1px 3px rgba(0,0,0,0.05)'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ fontSize: '13px', fontWeight: '600', color: '#64748B' }}>{c.title}</span>
              <div
                style={{
                  width: '36px',
                  height: '36px',
                  borderRadius: '10px',
                  backgroundColor: c.bgColor,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  color: c.color
                }}
              >
                <IconComponent size={20} />
              </div>
            </div>
            <div style={{ fontSize: '28px', fontWeight: '800', color: '#0F172A', marginTop: '12px' }}>
              {c.value}
            </div>
            <div style={{ fontSize: '12px', color: '#94A3B8', marginTop: '4px' }}>
              {c.subtext}
            </div>
          </div>
        );
      })}
    </div>
  );
};

