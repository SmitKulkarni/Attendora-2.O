import React, { useState } from 'react';
import { Lock, Mail, Shield, Database, RefreshCw, CheckCircle2 } from 'lucide-react';

export const SettingsScreen: React.FC = () => {
  const [strictLockEnabled, setStrictLockEnabled] = useState(true);
  const [autoEmailEnabled, setAutoEmailEnabled] = useState(true);
  const [geoFenceStrict, setGeoFenceStrict] = useState(true);
  const [isSyncing, setIsSyncing] = useState(false);
  const [syncedSuccess, setSyncedSuccess] = useState(false);

  const handleSyncNow = () => {
    setIsSyncing(true);
    setSyncedSuccess(false);
    setTimeout(() => {
      setIsSyncing(false);
      setSyncedSuccess(true);
      setTimeout(() => setSyncedSuccess(false), 3000);
    }, 1000);
  };

  return (
    <div style={{ maxWidth: '640px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '16px',
          padding: '24px',
          border: '1px solid #E2E8F0',
          boxShadow: '0 1px 3px rgba(0,0,0,0.05)'
        }}
      >
        <h2 style={{ margin: '0 0 4px', fontSize: '18px', fontWeight: '800', color: '#0F172A' }}>
          Tenant Configuration & Security
        </h2>
        <p style={{ margin: '0 0 20px', fontSize: '12px', color: '#64748B' }}>
          Policies for attendance locking, automated alerts, and database synchronization
        </p>

        {/* 48-Hour Lock Toggle */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '14px 0', borderBottom: '1px solid #F1F5F9' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <div style={{ width: '36px', height: '36px', borderRadius: '10px', backgroundColor: '#E0F7FA', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#006673' }}>
              <Lock size={18} />
            </div>
            <div>
              <div style={{ fontSize: '14px', fontWeight: '700', color: '#0F172A' }}>48-Hour Edit Window Lock</div>
              <div style={{ fontSize: '11px', color: '#64748B' }}>Automatically locks attendance sessions after 48 hours to prevent retroactive tampering</div>
            </div>
          </div>
          <input
            type="checkbox"
            checked={strictLockEnabled}
            onChange={e => setStrictLockEnabled(e.target.checked)}
            style={{ width: '18px', height: '18px', accentColor: '#006673', cursor: 'pointer' }}
          />
        </div>

        {/* Automated Email Notifications */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '14px 0', borderBottom: '1px solid #F1F5F9' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <div style={{ width: '36px', height: '36px', borderRadius: '10px', backgroundColor: '#FEF2F2', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#DC2626' }}>
              <Mail size={18} />
            </div>
            <div>
              <div style={{ fontSize: '14px', fontWeight: '700', color: '#0F172A' }}>Automated Low-Attendance Warnings</div>
              <div style={{ fontSize: '11px', color: '#64748B' }}>Nightly Celery audit dispatches notices to defaulters and guardians</div>
            </div>
          </div>
          <input
            type="checkbox"
            checked={autoEmailEnabled}
            onChange={e => setAutoEmailEnabled(e.target.checked)}
            style={{ width: '18px', height: '18px', accentColor: '#006673', cursor: 'pointer' }}
          />
        </div>

        {/* 50m GPS Geofence */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '14px 0', borderBottom: '1px solid #F1F5F9' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <div style={{ width: '36px', height: '36px', borderRadius: '10px', backgroundColor: '#F0FDF4', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#16A34A' }}>
              <Shield size={18} />
            </div>
            <div>
              <div style={{ fontSize: '14px', fontWeight: '700', color: '#0F172A' }}>Strict Classroom GPS Geofencing</div>
              <div style={{ fontSize: '11px', color: '#64748B' }}>50m Haversine radius validation with hardware anti-proxy device binding</div>
            </div>
          </div>
          <input
            type="checkbox"
            checked={geoFenceStrict}
            onChange={e => setGeoFenceStrict(e.target.checked)}
            style={{ width: '18px', height: '18px', accentColor: '#006673', cursor: 'pointer' }}
          />
        </div>

        {/* Cloud Sync Flush Action */}
        <div style={{ marginTop: '20px', padding: '16px', backgroundColor: '#F8FAFC', borderRadius: '12px', border: '1px solid #E2E8F0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <div style={{ fontSize: '13px', fontWeight: '700', color: '#0F172A' }}>Cloud Sync Engine</div>
            <div style={{ fontSize: '11px', color: '#64748B' }}>PostgreSQL RLS tenant connection with local offline cache</div>
          </div>

          <button
            onClick={handleSyncNow}
            disabled={isSyncing}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '8px 14px',
              borderRadius: '8px',
              border: 'none',
              backgroundColor: syncedSuccess ? '#10B981' : '#006673',
              color: '#FFFFFF',
              fontWeight: '600',
              fontSize: '12px',
              cursor: isSyncing ? 'default' : 'pointer'
            }}
          >
            {syncedSuccess ? <CheckCircle2 size={14} /> : <RefreshCw size={14} className={isSyncing ? 'spin' : ''} />}
            {syncedSuccess ? 'Synchronized!' : isSyncing ? 'Syncing...' : 'Sync Cloud Now'}
          </button>
        </div>
      </div>
    </div>
  );
};

