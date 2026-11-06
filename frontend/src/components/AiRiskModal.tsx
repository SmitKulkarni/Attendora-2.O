import React from 'react';
import { X, Sparkles, AlertTriangle, ShieldCheck, FileText } from 'lucide-react';
import { AiRiskAssessment, Student } from '../types';

interface AiRiskModalProps {
  student: Student | null;
  riskAssessment: AiRiskAssessment | null;
  onClose: () => void;
  onDraftIntervention: () => void;
}

export const AiRiskModal: React.FC<AiRiskModalProps> = ({
  student,
  riskAssessment,
  onClose,
  onDraftIntervention
}) => {
  if (!riskAssessment || !student) return null;

  const getTierColor = (tier: string) => {
    switch (tier) {
      case 'CRITICAL': return { bg: '#FEE2E2', text: '#DC2626', border: '#EF4444' };
      case 'HIGH': return { bg: '#FFEDD5', text: '#EA580C', border: '#F97316' };
      case 'MODERATE': return { bg: '#FEF3C7', text: '#D97706', border: '#F59E0B' };
      default: return { bg: '#DCFCE7', text: '#16A34A', border: '#22C55E' };
    }
  };

  const tierColors = getTierColor(riskAssessment.risk_level);

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
          maxWidth: '540px',
          maxHeight: '90vh',
          overflowY: 'auto',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
          padding: '24px'
        }}
      >
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div
              style={{
                width: '36px',
                height: '36px',
                borderRadius: '50%',
                backgroundColor: '#E0F7FA',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#006673'
              }}
            >
              <Sparkles size={18} />
            </div>
            <div>
              <h3 style={{ margin: 0, fontSize: '17px', fontWeight: '800', color: '#0F172A' }}>
                Gemini Predictive AI
              </h3>
              <p style={{ margin: '1px 0 0', fontSize: '11px', color: '#64748B' }}>
                Attendance Anomaly & Debarment Risk
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#64748B', padding: '4px' }}
          >
            <X size={20} />
          </button>
        </div>

        {/* Student Identity Strip */}
        <div
          style={{
            marginTop: '16px',
            padding: '12px 16px',
            backgroundColor: '#F8FAFC',
            borderRadius: '12px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            border: '1px solid #E2E8F0'
          }}
        >
          <div>
            <div style={{ fontSize: '15px', fontWeight: '700', color: '#0F172A' }}>{student.name}</div>
            <div style={{ fontSize: '12px', color: '#64748B' }}>
              Roll: {student.rollNo} • {student.department}
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div
              style={{
                fontSize: '16px',
                fontWeight: '800',
                color: student.overallPercentage >= 75 ? '#10B981' : '#EF4444'
              }}
            >
              {student.overallPercentage}%
            </div>
            <div style={{ fontSize: '10px', color: '#94A3B8' }}>Current Standing</div>
          </div>
        </div>

        {/* Risk Tier Hero Card */}
        <div
          style={{
            marginTop: '16px',
            padding: '16px',
            borderRadius: '16px',
            backgroundColor: tierColors.bg,
            border: `1.5px solid ${tierColors.border}`
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span
              style={{
                padding: '4px 10px',
                borderRadius: '8px',
                backgroundColor: tierColors.text,
                color: '#FFFFFF',
                fontSize: '11px',
                fontWeight: '800'
              }}
            >
              {riskAssessment.risk_level} RISK
            </span>
            <span style={{ fontSize: '13px', fontWeight: '800', color: tierColors.text }}>
              Risk Index: {Math.round(riskAssessment.risk_score)}/100
            </span>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '14px' }}>
            <div>
              <div style={{ fontSize: '11px', color: '#64748B' }}>Projected Semester End</div>
              <div style={{ fontSize: '20px', fontWeight: '800', color: '#0F172A' }}>
                {riskAssessment.predicted_final_percentage}%
              </div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: '11px', color: '#64748B' }}>Model Confidence</div>
              <div style={{ fontSize: '20px', fontWeight: '800', color: '#006673' }}>
                {Math.round(riskAssessment.confidence_score * 100)}%
              </div>
            </div>
          </div>
        </div>

        {/* AI Summary */}
        <div style={{ marginTop: '16px' }}>
          <div style={{ fontSize: '12px', fontWeight: '700', color: '#475569', marginBottom: '4px' }}>
            Predictive Diagnostic
          </div>
          <p style={{ margin: 0, fontSize: '12px', color: '#1E293B', lineHeight: '18px' }}>
            {riskAssessment.ai_summary}
          </p>
        </div>

        {/* Risk Factors */}
        {riskAssessment.risk_factors && riskAssessment.risk_factors.length > 0 && (
          <div style={{ marginTop: '14px' }}>
            <div style={{ fontSize: '12px', fontWeight: '700', color: '#475569', marginBottom: '6px' }}>
              Observed Risk Factors
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              {riskAssessment.risk_factors.map((f, i) => (
                <div key={i} style={{ display: 'flex', alignItems: 'flex-start', gap: '6px', fontSize: '12px', color: '#334155' }}>
                  <AlertTriangle size={14} color={tierColors.text} style={{ marginTop: '2px', flexShrink: 0 }} />
                  <span>{f}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* AI Recommendations */}
        {riskAssessment.recommendations && riskAssessment.recommendations.length > 0 && (
          <div style={{ marginTop: '14px' }}>
            <div style={{ fontSize: '12px', fontWeight: '700', color: '#475569', marginBottom: '6px' }}>
              Advisory Interventions
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              {riskAssessment.recommendations.map((r, i) => (
                <div key={i} style={{ display: 'flex', alignItems: 'flex-start', gap: '6px', fontSize: '12px', color: '#334155' }}>
                  <ShieldCheck size={14} color="#006673" style={{ marginTop: '2px', flexShrink: 0 }} />
                  <span>{r}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Bottom Actions */}
        <div style={{ display: 'flex', gap: '10px', marginTop: '22px', borderTop: '1px solid #F1F5F9', paddingTop: '16px' }}>
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
              onClose();
              onDraftIntervention();
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
            <FileText size={16} />
            Draft Plan 📄
          </button>
        </div>
      </div>
    </div>
  );
};

