import React, { useState } from 'react';
import { UserPlus, Sparkles, FileText, Search } from 'lucide-react';
import { Student } from '../types';
import { StatusPill } from '../components/StatusPill';

interface StudentsManagementScreenProps {
  students: Student[];
  onStudentClick: (student: Student) => void;
  onAddNewStudent: (student: { name: string; rollNo: string; className: string; department: string; email: string }) => void;
  onAiEvaluate: (student: Student) => void;
  onGenerateIntervention: (student: Student) => void;
}

export const StudentsManagementScreen: React.FC<StudentsManagementScreenProps> = ({
  students,
  onStudentClick,
  onAddNewStudent,
  onAiEvaluate,
  onGenerateIntervention
}) => {
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [filterClass, setFilterClass] = useState('All');
  const [search, setSearch] = useState('');

  const [formData, setFormData] = useState({
    name: '',
    rollNo: '',
    className: 'Class 1',
    department: 'Computer Science',
    email: ''
  });

  const filtered = students.filter(s => {
    const matchesClass = filterClass === 'All' || s.className === filterClass;
    const matchesQuery = !search ||
      s.name.toLowerCase().includes(search.toLowerCase()) ||
      s.rollNo.toLowerCase().includes(search.toLowerCase()) ||
      s.department.toLowerCase().includes(search.toLowerCase());
    return matchesClass && matchesQuery;
  });

  const handleAddSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name) return;
    onAddNewStudent({
      ...formData,
      rollNo: formData.rollNo || `2026${Math.floor(100 + Math.random() * 900)}`,
      email: formData.email || `${formData.name.toLowerCase().replace(/\s+/g, '.')}@attendora.edu`
    });
    setFormData({ name: '', rollNo: '', className: 'Class 1', department: 'Computer Science', email: '' });
    setIsAddModalOpen(false);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header and Controls */}
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '16px',
          padding: '20px',
          border: '1px solid #E2E8F0',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '14px'
        }}
      >
        <div>
          <h2 style={{ margin: 0, fontSize: '18px', fontWeight: '800', color: '#0F172A' }}>
            Student Roster Management
          </h2>
          <p style={{ margin: '2px 0 0', fontSize: '12px', color: '#64748B' }}>
            Comprehensive directory, attendance standing & predictive profiles
          </p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          {/* Class Filter */}
          <select
            value={filterClass}
            onChange={e => setFilterClass(e.target.value)}
            style={{
              padding: '8px 12px',
              borderRadius: '10px',
              border: '1px solid #CBD5E1',
              fontSize: '13px',
              fontWeight: '600',
              outline: 'none',
              cursor: 'pointer'
            }}
          >
            <option value="All">All Cohorts ({students.length})</option>
            <option value="Class 1">Class 1</option>
            <option value="Class 2">Class 2</option>
          </select>

          {/* Add Student Button */}
          <button
            onClick={() => setIsAddModalOpen(true)}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '8px 16px',
              borderRadius: '10px',
              border: 'none',
              backgroundColor: '#006673',
              color: '#FFFFFF',
              fontWeight: '700',
              fontSize: '13px',
              cursor: 'pointer'
            }}
          >
            <UserPlus size={16} />
            Add Student
          </button>
        </div>
      </div>

      {/* Roster Table Card */}
      <div
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '16px',
          padding: '20px',
          border: '1px solid #E2E8F0',
          boxShadow: '0 1px 3px rgba(0,0,0,0.05)'
        }}
      >
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '13px' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid #E2E8F0', color: '#64748B' }}>
                <th style={{ padding: '10px 14px', fontWeight: '600' }}>Student</th>
                <th style={{ padding: '10px 14px', fontWeight: '600' }}>Roll No</th>
                <th style={{ padding: '10px 14px', fontWeight: '600' }}>Department</th>
                <th style={{ padding: '10px 14px', fontWeight: '600' }}>Email</th>
                <th style={{ padding: '10px 14px', fontWeight: '600' }}>Attendance</th>
                <th style={{ padding: '10px 14px', fontWeight: '600' }}>Status</th>
                <th style={{ padding: '10px 14px', fontWeight: '600', textAlign: 'right' }}>AI Analysis</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(s => (
                <tr
                  key={s.id}
                  style={{ borderBottom: '1px solid #F1F5F9', transition: 'background 0.1s' }}
                  onMouseEnter={e => (e.currentTarget.style.backgroundColor = '#F8FAFC')}
                  onMouseLeave={e => (e.currentTarget.style.backgroundColor = 'transparent')}
                >
                  <td
                    onClick={() => onStudentClick(s)}
                    style={{ padding: '12px 14px', fontWeight: '600', color: '#0F172A', cursor: 'pointer' }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <div
                        style={{
                          width: '32px',
                          height: '32px',
                          borderRadius: '50%',
                          backgroundColor: s.avatarBgColor || '#3B82F6',
                          color: '#FFFFFF',
                          fontWeight: '700',
                          fontSize: '12px',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center'
                        }}
                      >
                        {s.name.split(' ').map(n => n[0]).slice(0, 2).join('')}
                      </div>
                      <span>{s.name}</span>
                    </div>
                  </td>
                  <td style={{ padding: '12px 14px', color: '#64748B' }}>{s.rollNo}</td>
                  <td style={{ padding: '12px 14px', color: '#64748B' }}>{s.department}</td>
                  <td style={{ padding: '12px 14px', color: '#64748B' }}>{s.email}</td>
                  <td style={{ padding: '12px 14px' }}>
                    <span
                      style={{
                        fontWeight: '700',
                        color: s.overallPercentage >= 75 ? '#10B981' : '#EF4444'
                      }}
                    >
                      {s.overallPercentage}%
                    </span>
                  </td>
                  <td style={{ padding: '12px 14px' }}>
                    <StatusPill status={s.status} />
                  </td>
                  <td style={{ padding: '12px 14px', textAlign: 'right' }}>
                    <div style={{ display: 'inline-flex', gap: '6px' }}>
                      <button
                        onClick={() => onAiEvaluate(s)}
                        title="Evaluate Gemini AI Risk"
                        style={{
                          padding: '6px 10px',
                          borderRadius: '8px',
                          border: 'none',
                          backgroundColor: '#E0F7FA',
                          color: '#006673',
                          fontWeight: '700',
                          fontSize: '11px',
                          cursor: 'pointer',
                          display: 'inline-flex',
                          alignItems: 'center',
                          gap: '4px'
                        }}
                      >
                        <Sparkles size={12} />
                        Risk ✦
                      </button>
                      <button
                        onClick={() => onGenerateIntervention(s)}
                        title="Generate Remediation Plan"
                        style={{
                          padding: '6px 10px',
                          borderRadius: '8px',
                          border: '1px solid #CBD5E1',
                          backgroundColor: '#FFFFFF',
                          color: '#334155',
                          fontWeight: '600',
                          fontSize: '11px',
                          cursor: 'pointer',
                          display: 'inline-flex',
                          alignItems: 'center',
                          gap: '4px'
                        }}
                      >
                        <FileText size={12} />
                        Plan 📄
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Student Modal */}
      {isAddModalOpen && (
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
              borderRadius: '20px',
              width: '100%',
              maxWidth: '440px',
              padding: '24px',
              boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)'
            }}
          >
            <h3 style={{ margin: '0 0 16px', fontSize: '18px', fontWeight: '800', color: '#0F172A' }}>
              Add New Student
            </h3>
            <form onSubmit={handleAddSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div>
                <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748B' }}>Full Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Maya Deshpande"
                  value={formData.name}
                  onChange={e => setFormData({ ...formData, name: e.target.value })}
                  style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #CBD5E1', marginTop: '4px', boxSizing: 'border-box' }}
                />
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748B' }}>Class Cohort</label>
                <select
                  value={formData.className}
                  onChange={e => setFormData({ ...formData, className: e.target.value })}
                  style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #CBD5E1', marginTop: '4px', boxSizing: 'border-box' }}
                >
                  <option value="Class 1">Class 1</option>
                  <option value="Class 2">Class 2</option>
                </select>
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748B' }}>Department</label>
                <input
                  type="text"
                  value={formData.department}
                  onChange={e => setFormData({ ...formData, department: e.target.value })}
                  style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #CBD5E1', marginTop: '4px', boxSizing: 'border-box' }}
                />
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748B' }}>Institutional Email</label>
                <input
                  type="email"
                  placeholder="e.g. maya.d@attendora.edu"
                  value={formData.email}
                  onChange={e => setFormData({ ...formData, email: e.target.value })}
                  style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #CBD5E1', marginTop: '4px', boxSizing: 'border-box' }}
                />
              </div>

              <div style={{ display: 'flex', gap: '10px', marginTop: '16px' }}>
                <button
                  type="button"
                  onClick={() => setIsAddModalOpen(false)}
                  style={{ flex: 1, padding: '10px', borderRadius: '8px', border: '1px solid #CBD5E1', background: '#FFFFFF', cursor: 'pointer' }}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  style={{ flex: 1.5, padding: '10px', borderRadius: '8px', border: 'none', background: '#006673', color: '#FFFFFF', fontWeight: '700', cursor: 'pointer' }}
                >
                  Enroll Student
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

