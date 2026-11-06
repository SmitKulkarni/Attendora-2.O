import React, { useState } from 'react';
import { BookOpen, Users, Clock, MapPin, Plus } from 'lucide-react';
import { ClassGroup } from '../types';

interface ClassesManagementScreenProps {
  classes: ClassGroup[];
  onAddNewClass: (cls: Omit<ClassGroup, 'id'>) => void;
}

export const ClassesManagementScreen: React.FC<ClassesManagementScreenProps> = ({
  classes,
  onAddNewClass
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    code: '',
    department: 'Computer Science',
    teacherName: '',
    enrolledCount: 60,
    scheduleTime: '09:00 AM - 10:30 AM',
    roomLocation: 'Lecture Hall 101'
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name) return;
    onAddNewClass({
      ...formData,
      code: formData.code || `CS-${Math.floor(100 + Math.random() * 800)}`,
      teacherName: formData.teacherName || 'Prof. Faculty Member'
    });
    setFormData({ name: '', code: '', department: 'Computer Science', teacherName: '', enrolledCount: 60, scheduleTime: '09:00 AM - 10:30 AM', roomLocation: 'Lecture Hall 101' });
    setIsModalOpen(false);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header */}
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
            Academic Class Cohorts
          </h2>
          <p style={{ margin: '2px 0 0', fontSize: '12px', color: '#64748B' }}>
            Manage lecture halls, professor assignments, and room schedules
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
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
          <Plus size={16} />
          Create Class
        </button>
      </div>

      {/* Class Cards Grid */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))',
          gap: '16px'
        }}
      >
        {classes.map(c => (
          <div
            key={c.id}
            style={{
              backgroundColor: '#FFFFFF',
              borderRadius: '16px',
              padding: '20px',
              border: '1px solid #E2E8F0',
              boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between'
            }}
          >
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <span
                  style={{
                    padding: '4px 10px',
                    borderRadius: '8px',
                    backgroundColor: '#E0F7FA',
                    color: '#006673',
                    fontSize: '11px',
                    fontWeight: '800'
                  }}
                >
                  {c.code}
                </span>
                <span style={{ fontSize: '12px', color: '#64748B' }}>{c.academicTerm || 'Fall 2026'}</span>
              </div>

              <h3 style={{ margin: '12px 0 4px', fontSize: '16px', fontWeight: '700', color: '#0F172A' }}>
                {c.name}
              </h3>
              <p style={{ margin: '0 0 14px', fontSize: '12px', color: '#64748B' }}>
                {c.courseName || c.name}
              </p>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '12px', color: '#475569' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Users size={15} color="#006673" />
                  <span>Enrolled: <strong>{c.enrolledCount} Students</strong></span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Clock size={15} color="#006673" />
                  <span>{c.scheduleTime}</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <MapPin size={15} color="#006673" />
                  <span>{c.roomLocation || 'Hall A-101'}</span>
                </div>
              </div>
            </div>

            <div style={{ marginTop: '16px', borderTop: '1px solid #F1F5F9', paddingTop: '12px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ fontSize: '12px', color: '#64748B' }}>Faculty: <strong>{c.teacherName}</strong></span>
              <span style={{ fontSize: '11px', color: '#006673', fontWeight: '700' }}>Active Cohort</span>
            </div>
          </div>
        ))}
      </div>

      {/* Create Class Modal */}
      {isModalOpen && (
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
              maxWidth: '460px',
              padding: '24px',
              boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)'
            }}
          >
            <h3 style={{ margin: '0 0 16px', fontSize: '18px', fontWeight: '800', color: '#0F172A' }}>
              Create Class Cohort
            </h3>
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div>
                <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748B' }}>Class Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. IT 3rd Year"
                  value={formData.name}
                  onChange={e => setFormData({ ...formData, name: e.target.value })}
                  style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #CBD5E1', marginTop: '4px', boxSizing: 'border-box' }}
                />
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748B' }}>Course Code</label>
                <input
                  type="text"
                  placeholder="e.g. IT-3A"
                  value={formData.code}
                  onChange={e => setFormData({ ...formData, code: e.target.value })}
                  style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #CBD5E1', marginTop: '4px', boxSizing: 'border-box' }}
                />
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748B' }}>Faculty Teacher</label>
                <input
                  type="text"
                  placeholder="e.g. Dr. Ramesh Joshi"
                  value={formData.teacherName}
                  onChange={e => setFormData({ ...formData, teacherName: e.target.value })}
                  style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #CBD5E1', marginTop: '4px', boxSizing: 'border-box' }}
                />
              </div>

              <div style={{ display: 'flex', gap: '10px' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748B' }}>Schedule Time</label>
                  <input
                    type="text"
                    value={formData.scheduleTime}
                    onChange={e => setFormData({ ...formData, scheduleTime: e.target.value })}
                    style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #CBD5E1', marginTop: '4px', boxSizing: 'border-box' }}
                  />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ fontSize: '12px', fontWeight: '600', color: '#64748B' }}>Room Location</label>
                  <input
                    type="text"
                    value={formData.roomLocation}
                    onChange={e => setFormData({ ...formData, roomLocation: e.target.value })}
                    style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #CBD5E1', marginTop: '4px', boxSizing: 'border-box' }}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', gap: '10px', marginTop: '16px' }}>
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  style={{ flex: 1, padding: '10px', borderRadius: '8px', border: '1px solid #CBD5E1', background: '#FFFFFF', cursor: 'pointer' }}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  style={{ flex: 1.5, padding: '10px', borderRadius: '8px', border: 'none', background: '#006673', color: '#FFFFFF', fontWeight: '700', cursor: 'pointer' }}
                >
                  Save Class
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

