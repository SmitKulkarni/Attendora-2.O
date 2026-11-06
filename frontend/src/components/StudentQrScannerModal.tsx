import React, { useState } from 'react';
import { X, Camera, MapPin, Smartphone, CheckCircle2 } from 'lucide-react';

interface StudentQrScannerModalProps {
  studentName: string;
  onCheckInSuccess: () => void;
  onClose: () => void;
}

export const StudentQrScannerModal: React.FC<StudentQrScannerModalProps> = ({
  studentName,
  onCheckInSuccess,
  onClose
}) => {
  const [isScanning, setIsScanning] = useState(true);
  const [isSuccess, setIsSuccess] = useState(false);

  const handleSimulateScan = () => {
    setIsScanning(false);
    setIsSuccess(true);
    setTimeout(() => {
      onCheckInSuccess();
      onClose();
    }, 1200);
  };

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(15, 23, 42, 0.75)',
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
          maxWidth: '420px',
          padding: '24px',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
          textAlign: 'center'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h3 style={{ margin: 0, fontSize: '17px', fontWeight: '800', color: '#0F172A' }}>Classroom QR Scanner</h3>
            <p style={{ margin: '2px 0 0', fontSize: '11px', color: '#64748B' }}>Self-Check-In Viewfinder</p>
          </div>
          <button onClick={onClose} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#64748B' }}>
            <X size={20} />
          </button>
        </div>

        {/* Viewfinder simulation */}
        <div
          style={{
            margin: '20px auto',
            width: '240px',
            height: '240px',
            backgroundColor: '#0F172A',
            borderRadius: '20px',
            position: 'relative',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            overflow: 'hidden'
          }}
        >
          {isSuccess ? (
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', color: '#10B981' }}>
              <CheckCircle2 size={56} />
              <div style={{ marginTop: '8px', fontSize: '15px', fontWeight: '700', color: '#FFFFFF' }}>
                Attendance Verified!
              </div>
              <div style={{ fontSize: '12px', color: '#A7F3D0' }}>Marked PRESENT (Hall A-101)</div>
            </div>
          ) : (
            <>
              {/* Corner crosshairs */}
              <div style={{ position: 'absolute', top: '16px', left: '16px', width: '24px', height: '24px', borderTop: '3px solid #006673', borderLeft: '3px solid #006673' }} />
              <div style={{ position: 'absolute', top: '16px', right: '16px', width: '24px', height: '24px', borderTop: '3px solid #006673', borderRight: '3px solid #006673' }} />
              <div style={{ position: 'absolute', bottom: '16px', left: '16px', width: '24px', height: '24px', borderBottom: '3px solid #006673', borderLeft: '3px solid #006673' }} />
              <div style={{ position: 'absolute', bottom: '16px', right: '16px', width: '24px', height: '24px', borderBottom: '3px solid #006673', borderRight: '3px solid #006673' }} />

              {/* Animated laser line */}
              <div
                style={{
                  position: 'absolute',
                  left: '20px',
                  right: '20px',
                  height: '2px',
                  backgroundColor: '#00E5FF',
                  boxShadow: '0 0 10px #00E5FF, 0 0 20px #00E5FF'
                }}
              />

              <div style={{ color: '#94A3B8', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '6px' }}>
                <Camera size={32} />
                <span style={{ fontSize: '11px' }}>Align classroom QR inside frame</span>
              </div>
            </>
          )}
        </div>

        {/* GPS and Device verification badges */}
        <div style={{ display: 'flex', justifyContent: 'center', gap: '8px', marginBottom: '16px' }}>
          <span style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', padding: '4px 8px', backgroundColor: '#F0FDF4', borderRadius: '6px', fontSize: '11px', color: '#16A34A', fontWeight: '600' }}>
            <MapPin size={12} /> GPS In-Range (14m)
          </span>
          <span style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', padding: '4px 8px', backgroundColor: '#F8FAFC', borderRadius: '6px', fontSize: '11px', color: '#475569', fontWeight: '600' }}>
            <Smartphone size={12} /> Bound Device
          </span>
        </div>

        <button
          onClick={handleSimulateScan}
          disabled={!isScanning}
          style={{
            width: '100%',
            padding: '12px',
            borderRadius: '12px',
            border: 'none',
            backgroundColor: '#006673',
            color: '#FFFFFF',
            fontWeight: '700',
            fontSize: '14px',
            cursor: isScanning ? 'pointer' : 'default',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '8px'
          }}
        >
          <Camera size={16} />
          {isScanning ? 'Scan & Check-In Now' : 'Verified!'}
        </button>
      </div>
    </div>
  );
};

