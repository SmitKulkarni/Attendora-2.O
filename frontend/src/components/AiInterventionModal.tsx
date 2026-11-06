import React from 'react';
import { X, Send, FileCheck2 } from 'lucide-react';
import { AiInterventionPlan } from '../types';

interface AiInterventionModalProps {
  intervention: AiInterventionPlan | null;
  onClose: () => void;
  onDispatch: () => void;
}

export const AiInterventionModal: React.FC<AiInterventionModalProps> = ({
  intervention,
  onClose,
  onDispatch
}) => {
  if (!intervention) return null;

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(15, 23, 42, 0.6)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 60,
        padding: '16px'
      }}
    >
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '24px',
          width: '100%',
          maxWidth: '560px',
          maxHeight: '90vh',
          overflowY: 'auto',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
          padding: '24px'
        }}
      >
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h3 style={{ margin: 0, fontSize: '18px', fontWeight: '800', color: '#0F172A' }}>
              Academic Intervention Plan
            </h3>
            <p style={{ margin: '2px 0 0', fontSize: '12px', color: '#64748B' }}>
              Statutory Remediation & Counseling Contract
            </p>
          </div>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#64748B', padding: '4px' }}
          >
            <X size={20} />
          </button>
        </div>

        {/* Shortfall Target Card */}
        <div
          style={{
            marginTop: '16px',
            padding: '14px',
            borderRadius: '12px',
            backgroundColor: intervention.risk_level === 'CRITICAL' ? '#FEF2F2' : '#F0FDF4',
            border: `1px solid ${intervention.risk_level === 'CRITICAL' ? '#FCA5A5' : '#86EFAC'}`,
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}
        >
          <div>
            <div style={{ fontSize: '11px', color: '#64748B' }}>Recovery Goal</div>
            <div
              style={{
                fontSize: '15px',
                fontWeight: '800',
                color: intervention.risk_level === 'CRITICAL' ? '#DC2626' : '#16A34A'
              }}
            >
              {intervention.current_attendance_percent}% → {intervention.target_attendance_percent}%
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: '11px', color: '#64748B' }}>Required Consecutive Sessions</div>
            <div style={{ fontSize: '15px', fontWeight: '800', color: '#0F172A' }}>
              {intervention.classes_needed_for_eligibility} lectures
            </div>
          </div>
        </div>

        {/* Action Steps */}
        <div style={{ marginTop: '16px' }}>
          <div style={{ fontSize: '13px', fontWeight: '700', color: '#334155', marginBottom: '8px' }}>
            Remediation Action Items
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
            {intervention.action_items.map((item, i) => (
              <div key={i} style={{ display: 'flex', alignItems: 'flex-start', gap: '8px', fontSize: '12px', color: '#334155' }}>
                <span
                  style={{
                    width: '18px',
                    height: '18px',
                    borderRadius: '50%',
                    backgroundColor: '#006673',
                    color: '#FFFFFF',
                    fontSize: '10px',
                    fontWeight: '700',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    flexShrink: 0
                  }}
                >
                  {i + 1}
                </span>
                <span>{item}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Counseling Letter Memo Preview */}
        <div style={{ marginTop: '16px' }}>
          <div style={{ fontSize: '13px', fontWeight: '700', color: '#334155', marginBottom: '6px' }}>
            Official Notice Letter Preview
          </div>
          <div
            style={{
              padding: '12px',
              backgroundColor: '#F8FAFC',
              borderRadius: '10px',
              border: '1px solid #E2E8F0',
              fontFamily: "'JetBrains Mono', monospace",
              fontSize: '11px',
              color: '#334155',
              whiteSpace: 'pre-wrap',
              maxHeight: '180px',
              overflowY: 'auto',
              lineHeight: '16px'
            }}
          >
            {intervention.counseling_letter}
          </div>
        </div>

        {/* Bottom Actions */}
        <div style={{ display: 'flex', gap: '10px', marginTop: '20px', borderTop: '1px solid #F1F5F9', paddingTop: '16px' }}>
          <button
            onClick={onClose}
            style={{
              flex: 1,
              padding: '10px',
              borderRadius: '10px',
              border: '1px solid #CBD5E1',
              backgroundColor: '#FFFFFF',
              color: '#475569',
              fontWeight: '600',
              fontSize: '13px',
              cursor: 'pointer'
            }}
          >
            Close
          </button>
          <button
            onClick={() => {
              onDispatch();
              onClose();
            }}
            style={{
              flex: 1.5,
              padding: '10px',
              borderRadius: '10px',
              border: 'none',
              backgroundColor: '#006673',
              color: '#FFFFFF',
              fontWeight: '700',
              fontSize: '13px',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '6px'
            }}
          >
            <Send size={15} />
            Issue Official Notice
          </button>
        </div>
      </div>
    </div>
  );
};

