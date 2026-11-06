import React, { useState } from 'react';
import { Search, Bell, ChevronDown, Check, LogIn, LogOut, User, Shield } from 'lucide-react';
import { UserRole, AttendanceNotice } from '../types';
import { useAuth } from '../context/AuthContext';

interface AttendoraTopBarProps {
  searchQuery: string;
  onSearchChange: (q: string) => void;
  currentRole: UserRole;
  onRoleChange: (r: UserRole) => void;
  notices: AttendanceNotice[];
}

export const AttendoraTopBar: React.FC<AttendoraTopBarProps> = ({
  searchQuery,
  onSearchChange,
  currentRole,
  onRoleChange,
  notices
}) => {
  const { currentUser, isAuthenticated, logout, openAuthModal } = useAuth();
  const [isRoleDropdownOpen, setIsRoleDropdownOpen] = useState(false);
  const [isNoticeDropdownOpen, setIsNoticeDropdownOpen] = useState(false);
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);

  const roles: { role: UserRole; label: string }[] = [
    { role: 'ADMIN', label: 'Administrator / Principal' },
    { role: 'TEACHER', label: 'Faculty / Professor' },
    { role: 'STUDENT', label: 'Student Portal' }
  ];

  const getInitials = (name?: string) => {
    if (!name) return 'AT';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.slice(0, 2).toUpperCase();
  };

  return (
    <header
      style={{
        height: '68px',
        backgroundColor: '#FFFFFF',
        borderBottom: '1px solid #E2E8F0',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0 24px',
        boxSizing: 'border-box',
        position: 'relative',
        zIndex: 20
      }}
    >
      {/* Search Input */}
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '10px',
          backgroundColor: '#F1F5F9',
          padding: '8px 14px',
          borderRadius: '12px',
          width: '320px',
          maxWidth: '100%'
        }}
      >
        <Search size={16} color="#94A3B8" />
        <input
          type="text"
          placeholder="Search students, roll no, classes..."
          value={searchQuery}
          onChange={e => onSearchChange(e.target.value)}
          style={{
            border: 'none',
            background: 'none',
            outline: 'none',
            fontSize: '13px',
            color: '#0F172A',
            width: '100%',
            fontFamily: 'inherit'
          }}
        />
      </div>

      {/* Right Controls: Role Switcher & Notifications & Auth Avatar */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
        {/* Role Switcher Pill */}
        <div style={{ position: 'relative' }}>
          <button
            onClick={() => setIsRoleDropdownOpen(!isRoleDropdownOpen)}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              padding: '6px 14px',
              borderRadius: '9999px',
              backgroundColor: '#E0F7FA',
              border: '1px solid #B2EBF2',
              color: '#006673',
              fontSize: '12px',
              fontWeight: '700',
              cursor: 'pointer'
            }}
          >
            <span>Role: {currentRole}</span>
            <ChevronDown size={14} />
          </button>

          {isRoleDropdownOpen && (
            <div
              style={{
                position: 'absolute',
                top: '40px',
                right: 0,
                backgroundColor: '#FFFFFF',
                borderRadius: '12px',
                boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
                border: '1px solid #E2E8F0',
                width: '210px',
                padding: '6px',
                zIndex: 30
              }}
            >
              {roles.map(r => (
                <button
                  key={r.role}
                  onClick={() => {
                    onRoleChange(r.role);
                    setIsRoleDropdownOpen(false);
                  }}
                  style={{
                    width: '100%',
                    padding: '8px 12px',
                    borderRadius: '8px',
                    border: 'none',
                    backgroundColor: currentRole === r.role ? '#F1F5F9' : 'transparent',
                    color: currentRole === r.role ? '#006673' : '#334155',
                    fontSize: '12px',
                    fontWeight: currentRole === r.role ? '700' : '500',
                    textAlign: 'left',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between'
                  }}
                >
                  <span>{r.label}</span>
                  {currentRole === r.role && <Check size={14} color="#006673" />}
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Notifications */}
        <div style={{ position: 'relative' }}>
          <button
            onClick={() => setIsNoticeDropdownOpen(!isNoticeDropdownOpen)}
            style={{
              position: 'relative',
              background: '#F8FAFC',
              border: '1px solid #E2E8F0',
              width: '38px',
              height: '38px',
              borderRadius: '10px',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#64748B'
            }}
          >
            <Bell size={18} />
            {notices.length > 0 && (
              <span
                style={{
                  position: 'absolute',
                  top: '6px',
                  right: '6px',
                  width: '8px',
                  height: '8px',
                  borderRadius: '50%',
                  backgroundColor: '#EF4444'
                }}
              />
            )}
          </button>

          {isNoticeDropdownOpen && (
            <div
              style={{
                position: 'absolute',
                top: '46px',
                right: 0,
                backgroundColor: '#FFFFFF',
                borderRadius: '16px',
                boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.1)',
                border: '1px solid #E2E8F0',
                width: '320px',
                padding: '16px',
                zIndex: 30
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                <span style={{ fontSize: '14px', fontWeight: '700', color: '#0F172A' }}>Notifications</span>
                <span style={{ fontSize: '11px', color: '#006673', fontWeight: '600' }}>{notices.length} unread</span>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', maxHeight: '240px', overflowY: 'auto' }}>
                {notices.map(n => (
                  <div
                    key={n.id}
                    style={{
                      padding: '10px',
                      borderRadius: '10px',
                      backgroundColor: n.isWarning ? '#FEF2F2' : '#F8FAFC',
                      border: `1px solid ${n.isWarning ? '#FCA5A5' : '#E2E8F0'}`
                    }}
                  >
                    <div style={{ fontSize: '12px', fontWeight: '700', color: n.isWarning ? '#DC2626' : '#0F172A' }}>
                      {n.title}
                    </div>
                    <div style={{ fontSize: '11px', color: '#475569', marginTop: '2px' }}>{n.message}</div>
                    <div style={{ fontSize: '10px', color: '#94A3B8', marginTop: '4px' }}>{n.timeAgo}</div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* User Account / Profile Section */}
        {isAuthenticated && currentUser ? (
          <div style={{ position: 'relative' }}>
            <button
              onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                padding: '4px 8px 4px 4px',
                background: '#F8FAFC',
                border: '1px solid #E2E8F0',
                borderRadius: '12px',
                cursor: 'pointer'
              }}
            >
              <div
                style={{
                  width: '32px',
                  height: '32px',
                  borderRadius: '8px',
                  backgroundColor: '#006673',
                  color: '#FFFFFF',
                  fontWeight: '700',
                  fontSize: '12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }}
              >
                {getInitials(currentUser.name)}
              </div>
              <div style={{ textAlign: 'left', display: 'flex', flexDirection: 'column' }}>
                <span style={{ fontSize: '12px', fontWeight: '700', color: '#0F172A', lineHeight: 1.2 }}>
                  {currentUser.name}
                </span>
                <span style={{ fontSize: '10px', color: '#64748B' }}>
                  {currentUser.role}
                </span>
              </div>
              <ChevronDown size={14} color="#64748B" />
            </button>

            {isUserMenuOpen && (
              <div
                style={{
                  position: 'absolute',
                  top: '46px',
                  right: 0,
                  backgroundColor: '#FFFFFF',
                  borderRadius: '16px',
                  boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.1)',
                  border: '1px solid #E2E8F0',
                  width: '240px',
                  padding: '12px',
                  zIndex: 30
                }}
              >
                <div style={{ padding: '4px 8px 10px 8px', borderBottom: '1px solid #F1F5F9', marginBottom: '8px' }}>
                  <div style={{ fontSize: '13px', fontWeight: '700', color: '#0F172A' }}>{currentUser.name}</div>
                  <div style={{ fontSize: '11px', color: '#64748B' }}>{currentUser.email}</div>
                  <div style={{ marginTop: '4px', display: 'inline-flex', alignItems: 'center', gap: '4px', padding: '2px 8px', borderRadius: '9999px', backgroundColor: '#F0FDF4', color: '#166534', fontSize: '10px', fontWeight: '700' }}>
                    <Shield size={10} /> {currentUser.role}
                  </div>
                </div>

                <button
                  onClick={() => {
                    setIsUserMenuOpen(false);
                    logout();
                  }}
                  style={{
                    width: '100%',
                    padding: '8px 12px',
                    borderRadius: '8px',
                    border: 'none',
                    backgroundColor: '#FEF2F2',
                    color: '#DC2626',
                    fontSize: '12px',
                    fontWeight: '600',
                    textAlign: 'left',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '8px'
                  }}
                >
                  <LogOut size={14} />
                  <span>Sign Out</span>
                </button>
              </div>
            )}
          </div>
        ) : (
          <button
            onClick={() => openAuthModal('login')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '8px 16px',
              borderRadius: '12px',
              backgroundColor: '#006673',
              border: 'none',
              color: '#FFFFFF',
              fontSize: '12px',
              fontWeight: '700',
              cursor: 'pointer',
              boxShadow: '0 2px 4px rgba(0, 102, 115, 0.2)'
            }}
          >
            <LogIn size={15} />
            <span>Sign In</span>
          </button>
        )}
      </div>
    </header>
  );
};
