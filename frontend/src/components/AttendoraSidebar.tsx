import React from 'react';
import { LayoutDashboard, Users, CalendarCheck, BookOpen, BarChart3, Settings } from 'lucide-react';
import { NavTab } from '../types';

interface AttendoraSidebarProps {
  currentTab: NavTab;
  onTabChange: (tab: NavTab) => void;
  isCloudSynced?: boolean;
}

export const AttendoraSidebar: React.FC<AttendoraSidebarProps> = ({
  currentTab,
  onTabChange,
  isCloudSynced = true
}) => {
  const navItems = [
    { tab: 'DASHBOARD' as NavTab, label: 'Dashboard', icon: LayoutDashboard },
    { tab: 'STUDENTS' as NavTab, label: 'Students', icon: Users },
    { tab: 'ATTENDANCE' as NavTab, label: 'Attendance', icon: CalendarCheck },
    { tab: 'CLASSES' as NavTab, label: 'Classes', icon: BookOpen },
    { tab: 'REPORTS' as NavTab, label: 'Reports', icon: BarChart3 },
    { tab: 'SETTINGS' as NavTab, label: 'Settings', icon: Settings }
  ];

  return (
    <aside
      style={{
        width: '240px',
        backgroundColor: '#FFFFFF',
        borderRight: '1px solid #E2E8F0',
        display: 'flex',
        flexDirection: 'column',
        height: '100vh',
        boxSizing: 'border-box',
        padding: '24px 16px',
        flexShrink: 0
      }}
    >
      {/* Brand */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '10px', paddingLeft: '8px', marginBottom: '32px' }}>
        <div
          style={{
            width: '36px',
            height: '36px',
            borderRadius: '10px',
            backgroundColor: '#006673',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#FFFFFF',
            fontWeight: '800',
            fontSize: '18px'
          }}
        >
          A
        </div>
        <div>
          <h2 style={{ margin: 0, fontSize: '18px', fontWeight: '800', color: '#0F172A', letterSpacing: '-0.3px' }}>
            Attendora
          </h2>
          <span style={{ fontSize: '10px', color: '#64748B', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
            Enterprise SaaS
          </span>
        </div>
      </div>

      {/* Nav List */}
      <nav style={{ display: 'flex', flexDirection: 'column', gap: '6px', flex: 1 }}>
        {navItems.map(item => {
          const isSelected = currentTab === item.tab;
          const Icon = item.icon;

          return (
            <button
              key={item.tab}
              onClick={() => onTabChange(item.tab)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
                padding: '10px 14px',
                borderRadius: '12px',
                border: 'none',
                backgroundColor: isSelected ? '#E0F7FA' : 'transparent',
                color: isSelected ? '#006673' : '#64748B',
                fontWeight: isSelected ? '700' : '500',
                fontSize: '14px',
                cursor: 'pointer',
                textAlign: 'left',
                transition: 'all 0.15s ease'
              }}
            >
              <Icon size={18} color={isSelected ? '#006673' : '#64748B'} />
              <span>{item.label}</span>
            </button>
          );
        })}
      </nav>

      {/* Cloud Sync Status */}
      <div
        style={{
          padding: '12px 14px',
          backgroundColor: '#F8FAFC',
          borderRadius: '12px',
          border: '1px solid #E2E8F0',
          display: 'flex',
          alignItems: 'center',
          gap: '10px'
        }}
      >
        <span
          style={{
            width: '8px',
            height: '8px',
            borderRadius: '50%',
            backgroundColor: isCloudSynced ? '#10B981' : '#F59E0B'
          }}
        />
        <div style={{ flex: 1 }}>
          <div style={{ fontSize: '11px', fontWeight: '700', color: '#0F172A' }}>
            {isCloudSynced ? 'Cloud Synced' : 'Sync Pending'}
          </div>
          <div style={{ fontSize: '10px', color: '#94A3B8' }}>PostgreSQL RLS Active</div>
        </div>
      </div>
    </aside>
  );
};
