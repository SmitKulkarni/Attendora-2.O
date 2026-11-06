import React from 'react';
import { X, QrCode, ShieldCheck, MapPin, Users } from 'lucide-react';

interface DynamicQrModalProps {
  className: string;
  qrToken: string;
  secondsRemaining: number;
  verifiedCount: number;
  onClose: () => void;
}

export const DynamicQrModal: React.FC<DynamicQrModalProps> = ({
  className,
  qrToken,
  secondsRemaining,
  verifiedCount,
  onClose
}) => {
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
          borderRadius: '24px',
          width: '100%',
          maxWidth: '440px',
          padding: '24px',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
          textAlign: 'center'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <div style={{ width: '32px', height: '32px', borderRadius: '8px', backgroundColor: '#E0F7FA', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#006673' }}>
              <QrCode size={18} />
            </div>
            <div style={{ textAlign: 'left' }}>
              <h3 style={{ margin: 0, fontSize: '16px', fontWeight: '800', color: '#0F172A' }}>Smart QR Session</h3>
              <p style={{ margin: 0, fontSize: '11px', color: '#64748B' }}>{className} • Rolling Dynamic TOTP</p>
            </div>
          </div>
          <button onClick={onClose} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#64748B' }}>
            <X size={20} />
          </button>
        </div>

        {/* QR Visual Box */}
        <div
          style={{
            margin: '20px auto 12px',
            width: '200px',
            height: '200px',
            backgroundColor: '#FFFFFF',
            borderRadius: '16px',
            border: '2px solid #006673',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '16px',
            position: 'relative'
          }}
        >
          {/* Simulated 2D Barcode Grid */}
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(7, 1fr)',
              gap: '4px',
              width: '140px',
              height: '140px'
            }}
          >
            {Array.from({ length: 49 }).map((_, i) => {
              const isFinder =
                (i <= 2 || (i >= 7 && i <= 9) || (i >= 14 && i <= 16)) || // top-left
                ((i >= 4 && i <= 6) || (i >= 11 && i <= 13) || (i >= 18 && i <= 20)) || // top-right
                ((i >= 28 && i <= 30) || (i >= 35 && i <= 37) || (i >= 42 && i <= 44)); // bottom-left
              const isRandomDark = isFinder || (i * 7 + secondsRemaining) % 3 === 0;

              return (
                <div
                  key={i}
                  style={{
                    backgroundColor: isRandomDark ? '#006673' : '#F1F5F9',
                    borderRadius: isFinder ? '2px' : '1px'
                  }}
                />
              );
            })}
          </div>
        </div>

        {/* Token Countdown Pill */}
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', padding: '6px 14px', backgroundColor: '#F8FAFC', borderRadius: '9999px', border: '1px solid #E2E8F0' }}>
          <div style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: secondsRemaining <= 3 ? '#EF4444' : '#10B981' }} />
          <span style={{ fontSize: '12px', fontWeight: '700', color: '#0F172A' }}>
            Refreshes in {secondsRemaining}s
          </span>
        </div>

        {/* Geofence & Verified Counts */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginTop: '16px' }}>
          <div style={{ padding: '10px', backgroundColor: '#F0FDF4', borderRadius: '10px', border: '1px solid #BBF7D0', textAlign: 'left' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '4px', fontSize: '11px', color: '#16A34A', fontWeight: '600' }}>
              <MapPin size={12} /> 50m Geofence
            </div>
            <div style={{ fontSize: '12px', fontWeight: '700', color: '#0F172A', marginTop: '2px' }}>Hall A-101</div>
          </div>
          <div style={{ padding: '10px', backgroundColor: '#F0FDFA', borderRadius: '10px', border: '1px solid #99F6E4', textAlign: 'left' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '4px', fontSize: '11px', color: '#0D9488', fontWeight: '600' }}>
              <Users size={12} /> Verified
            </div>
            <div style={{ fontSize: '12px', fontWeight: '700', color: '#0F172A', marginTop: '2px' }}>{verifiedCount} Students</div>
          </div>
        </div>

        <button
          onClick={onClose}
          style={{
            marginTop: '18px',
            width: '100%',
            padding: '10px',
            borderRadius: '10px',
            border: 'none',
            backgroundColor: '#006673',
            color: '#FFFFFF',
            fontWeight: '700',
            fontSize: '13px',
            cursor: 'pointer'
          }}
        >
          End Smart Session
        </button>
      </div>
    </div>
  );
};

