import React from 'react';
import { FileText, Download, Mail, Sparkles, Building2 } from 'lucide-react';
import { Student, EnterpriseCampusesSummary } from '../types';

interface ReportsScreenProps {
  students: Student[];
  threshold: number;
  onThresholdChange: (val: number) => void;
  onExport: (format: 'PDF' | 'CSV') => void;
  onNotifyDefaulters: () => void;
  onAiEvaluateStudent: (student: Student) => void;
  campusesData: EnterpriseCampusesSummary;
}

export const ReportsScreen: React.FC<ReportsScreenProps> = ({
  students,
  threshold,
  onThresholdChange,
  onExport,
  onNotifyDefaulters,
  onAiEvaluateStudent,
  campusesData
}) => {
  const defaulters = students.filter(s => s.overallPercentage < threshold);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Threshold Config Card */}
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '16px',
          padding: '24px',
          border: '1px solid #E2E8F0',
          boxShadow: '0 1px 3px rgba(0,0,0,0.05)'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
          <div>
            <h2 style={{ margin: 0, fontSize: '18px', fontWeight: '800', color: '#0F172A' }}>
              Institutional Defaulter Analytics
            </h2>
            <p style={{ margin: '4px 0 0', fontSize: '12px', color: '#64748B' }}>
              Identify at-risk students below statutory attendance requirements
            </p>
          </div>

          <div
            style={{
              padding: '6px 14px',
              backgroundColor: '#FEE2E2',
              color: '#DC2626',
              borderRadius: '8px',
              fontSize: '13px',
              fontWeight: '800',
              border: '1px solid #FCA5A5'
            }}
          >
            Threshold: {threshold}%
          </div>
        </div>

        {/* Slider */}
        <div style={{ marginTop: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', fontWeight: '600', color: '#475569', marginBottom: '8px' }}>
            <span>Minimum Requirement Range</span>
            <span>{threshold}%</span>
          </div>
          <input
            type="range"
            min={50}
            max={90}
            step={5}
            value={threshold}
            onChange={e => onThresholdChange(Number(e.target.value))}
            style={{ width: '100%', accentColor: '#006673', cursor: 'pointer' }}
          />
        </div>

        {/* Action Buttons */}
        <div style={{ display: 'flex', gap: '10px', marginTop: '20px', flexWrap: 'wrap' }}>
          <button
            onClick={() => onExport('PDF')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '10px 16px',
              borderRadius: '10px',
              border: 'none',
              backgroundColor: '#006673',
              color: '#FFFFFF',
              fontWeight: '600',
              fontSize: '13px',
              cursor: 'pointer'
            }}
          >
            <Download size={16} />
            Export PDF
          </button>

          <button
            onClick={() => onExport('CSV')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '10px 16px',
              borderRadius: '10px',
              border: '1px solid #CBD5E1',
              backgroundColor: '#FFFFFF',
              color: '#334155',
              fontWeight: '600',
              fontSize: '13px',
              cursor: 'pointer'
            }}
          >
            <FileText size={16} />
            Export CSV
          </button>

          <button
            onClick={onNotifyDefaulters}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '10px 18px',
              borderRadius: '10px',
              border: 'none',
              backgroundColor: '#EF4444',
              color: '#FFFFFF',
              fontWeight: '700',
              fontSize: '13px',
              cursor: 'pointer'
            }}
          >
            <Mail size={16} />
            Notify Defaulters ({defaulters.length})
          </button>
        </div>
      </div>

      {/* Defaulter Roster Card */}
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '16px',
          padding: '24px',
          border: '1px solid #E2E8F0',
          boxShadow: '0 1px 3px rgba(0,0,0,0.05)'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h3 style={{ margin: 0, fontSize: '16px', fontWeight: '800', color: '#0F172A' }}>
            Defaulters Roster ({defaulters.length} Students)
          </h3>
          <span style={{ fontSize: '12px', color: '#64748B' }}>
            Falling below {threshold}% requirement
          </span>
        </div>

        {defaulters.length === 0 ? (
          <div style={{ padding: '32px', textAlign: 'center', color: '#10B981', fontWeight: '600', fontSize: '14px' }}>
            ✓ No students below {threshold}% attendance. Exemplary compliance!
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {defaulters.map(s => (
              <div
                key={s.id}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  padding: '12px 16px',
                  borderRadius: '12px',
                  backgroundColor: '#FFF1F2',
                  border: '1px solid #FECDD3'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                  <div
                    style={{
                      width: '36px',
                      height: '36px',
                      borderRadius: '50%',
                      backgroundColor: '#EF4444',
                      color: '#FFFFFF',
                      fontWeight: '700',
                      fontSize: '13px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center'
                    }}
                  >
                    {s.name.split(' ').map(n => n[0]).slice(0, 2).join('')}
                  </div>
                  <div>
                    <div style={{ fontSize: '14px', fontWeight: '700', color: '#0F172A' }}>{s.name}</div>
                    <div style={{ fontSize: '12px', color: '#64748B' }}>Roll: {s.rollNo} • {s.className} ({s.department})</div>
                  </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontSize: '16px', fontWeight: '800', color: '#E11D48' }}>
                      {s.overallPercentage}%
                    </div>
                    <div style={{ fontSize: '11px', color: '#9F1239' }}>
                      Shortage: {Math.round(threshold - s.overallPercentage)}%
                    </div>
                  </div>

                  <button
                    onClick={() => onAiEvaluateStudent(s)}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '4px',
                      padding: '8px 12px',
                      borderRadius: '8px',
                      border: 'none',
                      backgroundColor: '#006673',
                      color: '#FFFFFF',
                      fontWeight: '700',
                      fontSize: '12px',
                      cursor: 'pointer'
                    }}
                  >
                    <Sparkles size={14} />
                    AI Risk ✦
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Enterprise Multi-Campus Federation Card */}
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '16px',
          padding: '24px',
          border: '1px solid #E2E8F0',
          boxShadow: '0 1px 3px rgba(0,0,0,0.05)'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{ width: '36px', height: '36px', borderRadius: '10px', backgroundColor: '#E0F7FA', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#006673' }}>
              <Building2 size={20} />
            </div>
            <div>
              <h3 style={{ margin: 0, fontSize: '16px', fontWeight: '800', color: '#0F172A' }}>
                Enterprise Multi-Campus Federation
              </h3>
              <p style={{ margin: '1px 0 0', fontSize: '11px', color: '#64748B' }}>
                {campusesData.system_name}
              </p>
            </div>
          </div>

          <span style={{ padding: '4px 10px', backgroundColor: '#E0F7FA', color: '#006673', borderRadius: '8px', fontSize: '11px', fontWeight: '700' }}>
            {campusesData.total_campuses} Campuses Active
          </span>
        </div>

        {/* Aggregate Strip */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))',
            gap: '12px',
            backgroundColor: '#F8FAFC',
            padding: '14px',
            borderRadius: '12px',
            marginBottom: '16px'
          }}
        >
          <div>
            <div style={{ fontSize: '11px', color: '#64748B' }}>Total Enrolled</div>
            <div style={{ fontSize: '16px', fontWeight: '800', color: '#0F172A' }}>{campusesData.total_enrolled_students}</div>
          </div>
          <div>
            <div style={{ fontSize: '11px', color: '#64748B' }}>System Average</div>
            <div style={{ fontSize: '16px', fontWeight: '800', color: '#10B981' }}>{campusesData.system_average_attendance}%</div>
          </div>
          <div>
            <div style={{ fontSize: '11px', color: '#64748B' }}>Total Defaulters</div>
            <div style={{ fontSize: '16px', fontWeight: '800', color: '#EF4444' }}>{campusesData.total_defaulters} ({campusesData.system_defaulter_rate}%)</div>
          </div>
          <div>
            <div style={{ fontSize: '11px', color: '#64748B' }}>Top Campus</div>
            <div style={{ fontSize: '14px', fontWeight: '800', color: '#006673' }}>{campusesData.top_performing_campus}</div>
          </div>
        </div>

        {/* Campuses List */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          {campusesData.campuses.map(camp => (
            <div
              key={camp.id}
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '12px 14px',
                border: '1px solid #F1F5F9',
                borderRadius: '10px'
              }}
            >
              <div>
                <div style={{ fontSize: '14px', fontWeight: '700', color: '#0F172A' }}>{camp.name} ({camp.code})</div>
                <div style={{ fontSize: '11px', color: '#64748B' }}>{camp.city}, {camp.state} • Dean: {camp.dean}</div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: '15px', fontWeight: '800', color: camp.attendance_rate >= 82 ? '#10B981' : '#F59E0B' }}>
                  {camp.attendance_rate}%
                </div>
                <div style={{ fontSize: '11px', color: '#64748B' }}>{camp.defaulter_count} defaulters ({camp.defaulter_rate}%)</div>
              </div>
            </div>
          ))}
        </div>

        <div style={{ marginTop: '14px', padding: '12px', backgroundColor: '#F1F5F9', borderRadius: '10px', fontSize: '11px', color: '#475569', lineHeight: '16px' }}>
          <strong>AI Strategic Brief:</strong> {campusesData.ai_strategic_brief}
        </div>
      </div>
    </div>
  );
};

