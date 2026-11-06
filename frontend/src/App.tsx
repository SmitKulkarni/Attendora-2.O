import React, { useState, useEffect } from 'react';
import { UserRole, NavTab, Student, AttendanceStatus, ClassGroup, AttendanceNotice, AiRiskAssessment, AiInterventionPlan, EnterpriseCampusesSummary } from './types';
import { apiService, INITIAL_STUDENTS, INITIAL_CLASSES, INITIAL_NOTICES, INITIAL_DAY_LOGS, INITIAL_CAMPUSES } from './services/api';
import { AuthProvider, useAuth } from './context/AuthContext';
import { AuthModal } from './components/AuthModal';
import { ProtectedRoute } from './components/ProtectedRoute';
import { AttendoraSidebar } from './components/AttendoraSidebar';
import { AttendoraTopBar } from './components/AttendoraTopBar';
import { StudentDetailModal } from './components/StudentDetailModal';
import { AiRiskModal } from './components/AiRiskModal';
import { AiInterventionModal } from './components/AiInterventionModal';
import { DynamicQrModal } from './components/DynamicQrModal';
import { StudentQrScannerModal } from './components/StudentQrScannerModal';
import { AdminDashboardScreen } from './screens/AdminDashboardScreen';
import { AttendanceMarkingScreen } from './screens/AttendanceMarkingScreen';
import { StudentsManagementScreen } from './screens/StudentsManagementScreen';
import { ClassesManagementScreen } from './screens/ClassesManagementScreen';
import { ReportsScreen } from './screens/ReportsScreen';
import { StudentMobileScreen } from './screens/StudentMobileScreen';
import { SettingsScreen } from './screens/SettingsScreen';
import { AuthScreen } from './screens/AuthScreen';

const MainLayout: React.FC = () => {
  const { currentUser, isAuthenticated, isLoading } = useAuth();
  const [currentRole, setCurrentRole] = useState<UserRole>('ADMIN');
  const [currentTab, setCurrentTab] = useState<NavTab>('DASHBOARD');
  const [searchQuery, setSearchQuery] = useState('');
  const [students, setStudents] = useState<Student[]>(INITIAL_STUDENTS);
  const [classes, setClasses] = useState<ClassGroup[]>(INITIAL_CLASSES);
  const [notices, setNotices] = useState<AttendanceNotice[]>(INITIAL_NOTICES);
  const [campusesData, setCampusesData] = useState<EnterpriseCampusesSummary>(INITIAL_CAMPUSES);
  const [threshold, setThreshold] = useState(75);
  const [selectedClass, setSelectedClass] = useState('All');

  // Modals & Active State
  const [selectedStudentDetail, setSelectedStudentDetail] = useState<Student | null>(null);
  const [aiRiskAssessment, setAiRiskAssessment] = useState<AiRiskAssessment | null>(null);
  const [aiRiskStudent, setAiRiskStudent] = useState<Student | null>(null);
  const [activeIntervention, setActiveIntervention] = useState<AiInterventionPlan | null>(null);
  const [isQrModalOpen, setIsQrModalOpen] = useState(false);
  const [isScannerModalOpen, setIsScannerModalOpen] = useState(false);
  const [qrSeconds, setQrSeconds] = useState(10);
  const [qrVerifiedCount, setQrVerifiedCount] = useState(0);

  // Toast feedback
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3500);
  };

  // Sync role with authenticated user when logged in
  useEffect(() => {
    if (currentUser?.role) {
      setCurrentRole(currentUser.role);
    }
  }, [currentUser]);

  // Fetch initial data from backend if live
  useEffect(() => {
    apiService.checkHealth().then(h => {
      console.log('Backend Health:', h);
    });
    apiService.getStudents().then(res => {
      if (res && res.length > 0) setStudents(res);
    });
    apiService.getCampuses().then(res => {
      if (res) setCampusesData(res);
    });
  }, []);

  // Rolling TOTP QR countdown
  useEffect(() => {
    let timer: any;
    if (isQrModalOpen) {
      timer = setInterval(() => {
        setQrSeconds(prev => (prev <= 1 ? 10 : prev - 1));
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [isQrModalOpen]);

  // Attendance Toggle Logic
  const handleToggleStatus = (studentId: string) => {
    const timeStr = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    setStudents(prev =>
      prev.map(s => {
        if (s.id === studentId) {
          const nextStatus: AttendanceStatus =
            s.status === 'PRESENT' ? 'ABSENT' : s.status === 'ABSENT' ? 'LATE' : 'PRESENT';
          return {
            ...s,
            status: nextStatus,
            checkInTime: nextStatus === 'ABSENT' ? '-' : timeStr
          };
        }
        return s;
      })
    );
  };

  const handleSetStatus = (studentId: string, status: AttendanceStatus) => {
    const timeStr = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    setStudents(prev =>
      prev.map(s => (s.id === studentId ? { ...s, status, checkInTime: status === 'ABSENT' ? '-' : timeStr } : s))
    );
    showToast(`Updated attendance status to ${status}`);
  };

  const handleMarkAll = (status: AttendanceStatus) => {
    const timeStr = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    setStudents(prev =>
      prev.map(s => {
        if (selectedClass === 'All' || s.className === selectedClass) {
          return { ...s, status, checkInTime: status === 'ABSENT' ? '-' : timeStr };
        }
        return s;
      })
    );
    showToast(`All filtered students marked as ${status}`);
  };

  const handleSubmitSession = async () => {
    const records = students
      .filter(s => selectedClass === 'All' || s.className === selectedClass)
      .map(s => ({ id: s.id, status: s.status }));

    const ok = await apiService.submitAttendance(selectedClass, records);
    if (ok) {
      showToast(`Successfully saved and synced attendance session for ${records.length} students!`);
    } else {
      showToast('Offline mode: Saved locally in browser.');
    }
  };

  const handleExport = (format: 'CSV' | 'PDF' | 'EXCEL') => {
    if (format === 'PDF') {
      window.open(apiService.getTranscriptUrl(students[0]?.id || 's_1'), '_blank');
      showToast('Generating official Dean-certified PDF attendance transcript...');
      return;
    }
    const headers = 'ID,Name,RollNo,Class,Status,Percentage,CheckInTime\n';
    const rows = students
      .map(s => `${s.id},"${s.name}",${s.rollNo},"${s.className}",${s.status},${s.overallPercentage}%,${s.checkInTime}`)
      .join('\n');
    const blob = new Blob([headers + rows], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `Attendora_${selectedClass.replace(/\s+/g, '_')}_Attendance.csv`;
    a.click();
    showToast(`Exported ${format} audit report successfully!`);
  };

  const handleNotifyDefaulters = async () => {
    const res = await apiService.notifyDefaulters(threshold);
    showToast(res.message);
  };

  const handleAiEvaluate = async (student: Student) => {
    setAiRiskStudent(student);
    const assessment = await apiService.predictRisk(student.id);
    setAiRiskAssessment(assessment);
  };

  const handleGenerateIntervention = async (student: Student) => {
    const plan = await apiService.generateIntervention(student.id);
    setActiveIntervention(plan);
  };

  const handleStudentCheckInSuccess = () => {
    setIsScannerModalOpen(false);
    const timeStr = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const targetName = students[0]?.name || 'Student';
    setStudents(prev =>
      prev.map((s, idx) => (idx === 0 ? { ...s, status: 'PRESENT', checkInTime: timeStr } : s))
    );
    showToast(`Dynamic TOTP Attendance Verified for ${targetName}!`);
  };

  if (isLoading) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', width: '100vw', height: '100vh', backgroundColor: '#F8FAFC' }}>
        <div style={{ width: '48px', height: '48px', border: '4px solid rgba(0, 102, 115, 0.2)', borderTopColor: '#006673', borderRadius: '50%', animation: 'spin 1s linear infinite', marginBottom: '16px' }} />
        <h3 style={{ fontSize: '16px', fontWeight: '700', color: '#0F172A', margin: 0 }}>Attendora Intelligence Platform</h3>
        <p style={{ fontSize: '12px', color: '#64748B', marginTop: '6px' }}>Verifying authenticated session...</p>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <AuthScreen />;
  }

  return (
    <div style={{ display: 'flex', width: '100vw', height: '100vh', backgroundColor: '#EBF1F5', overflow: 'hidden' }}>
      {/* Toast Notification Banner */}
      {toastMessage && (
        <div
          style={{
            position: 'fixed',
            top: '20px',
            right: '24px',
            backgroundColor: '#006673',
            color: '#FFFFFF',
            padding: '12px 20px',
            borderRadius: '12px',
            fontSize: '13px',
            fontWeight: '600',
            boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
            zIndex: 9999,
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            animation: 'slide-in 0.3s ease'
          }}
        >
          <span>✓</span>
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Sidebar for Desktop Admin/Teacher */}
      {currentRole !== 'STUDENT' && (
        <AttendoraSidebar currentTab={currentTab} onTabChange={setCurrentTab} isCloudSynced={true} />
      )}

      {/* Main Container */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', height: '100vh', overflow: 'hidden' }}>
        {/* Top Bar */}
        <AttendoraTopBar
          searchQuery={searchQuery}
          onSearchChange={setSearchQuery}
          currentRole={currentRole}
          onRoleChange={setCurrentRole}
          notices={notices}
        />

        {/* Scrollable View Area */}
        <main style={{ flex: 1, overflowY: 'auto', padding: '24px', boxSizing: 'border-box' }}>
          {currentRole === 'STUDENT' ? (
            <StudentMobileScreen
              student={students[0]}
              dayLogs={INITIAL_DAY_LOGS}
              onScanQr={() => setIsScannerModalOpen(true)}
              onDownloadTranscript={() => handleExport('PDF')}
            />
          ) : (
            <>
              {currentTab === 'DASHBOARD' && (
                <AdminDashboardScreen
                  students={students}
                  onStudentClick={setSelectedStudentDetail}
                  onToggleStatus={handleToggleStatus}
                />
              )}

              {currentTab === 'ATTENDANCE' && (
                <AttendanceMarkingScreen
                  classes={classes}
                  students={students}
                  selectedClass={selectedClass}
                  onSelectClass={setSelectedClass}
                  onToggleStatus={handleToggleStatus}
                  onMarkAll={handleMarkAll}
                  onSubmitSession={handleSubmitSession}
                  onStartQrSession={() => {
                    setQrVerifiedCount(0);
                    setIsQrModalOpen(true);
                  }}
                />
              )}

              {currentTab === 'STUDENTS' && (
                <StudentsManagementScreen
                  students={students}
                  onStudentClick={setSelectedStudentDetail}
                  onAddNewStudent={newStu => {
                    const studentObj: Student = {
                      id: `s_${Date.now()}`,
                      ...newStu,
                      status: 'PRESENT',
                      checkInTime: '6:30 PM',
                      avatarBgColor: '#006673',
                      overallPercentage: 90.0,
                      subjects: [
                        { name: 'Core Foundations', code: 'CS-101', attended: 36, total: 40, percentage: 90.0 },
                        { name: 'Applied Mathematics', code: 'MA-101', attended: 38, total: 40, percentage: 95.0 }
                      ]
                    };
                    setStudents(prev => [studentObj, ...prev]);
                    showToast(`Enrolled ${studentObj.name} successfully!`);
                  }}
                  onAiEvaluate={handleAiEvaluate}
                  onGenerateIntervention={handleGenerateIntervention}
                />
              )}

              {currentTab === 'CLASSES' && (
                <ClassesManagementScreen
                  classes={classes}
                  onAddNewClass={newCls => {
                    const clsObj: ClassGroup = { id: `c_${Date.now()}`, ...newCls };
                    setClasses(prev => [...prev, clsObj]);
                    showToast(`Created class ${clsObj.name} (${clsObj.code})`);
                  }}
                />
              )}

              {currentTab === 'REPORTS' && (
                <ReportsScreen
                  students={students}
                  threshold={threshold}
                  onThresholdChange={setThreshold}
                  onExport={handleExport}
                  onNotifyDefaulters={handleNotifyDefaulters}
                  onAiEvaluateStudent={handleAiEvaluate}
                  campusesData={campusesData}
                />
              )}

              {currentTab === 'SETTINGS' && <SettingsScreen />}
            </>
          )}
        </main>
      </div>

      {/* Authentication Modal */}
      <AuthModal />

      {/* Existing Operational Modals */}
      <StudentDetailModal
        student={selectedStudentDetail}
        onClose={() => setSelectedStudentDetail(null)}
        onStatusChange={(id, st) => {
          handleSetStatus(id, st);
          setSelectedStudentDetail(null);
        }}
        onAiEvaluate={s => {
          setSelectedStudentDetail(null);
          handleAiEvaluate(s);
        }}
        onGenerateIntervention={s => {
          setSelectedStudentDetail(null);
          handleGenerateIntervention(s);
        }}
      />

      <AiRiskModal
        student={aiRiskStudent}
        riskAssessment={aiRiskAssessment}
        onClose={() => {
          setAiRiskAssessment(null);
          setAiRiskStudent(null);
        }}
        onDraftIntervention={() => {
          if (aiRiskStudent) handleGenerateIntervention(aiRiskStudent);
        }}
      />

      <AiInterventionModal
        intervention={activeIntervention}
        onClose={() => setActiveIntervention(null)}
        onDispatch={() => {
          handleNotifyDefaulters();
          showToast(`Official Intervention memo dispatched for ${activeIntervention?.student_name}!`);
        }}
      />

      {isQrModalOpen && (
        <DynamicQrModal
          className={selectedClass}
          qrToken={`att_qr_token_${Math.floor(Date.now() / 10000)}`}
          secondsRemaining={qrSeconds}
          verifiedCount={qrVerifiedCount}
          onClose={() => setIsQrModalOpen(false)}
        />
      )}

      {isScannerModalOpen && (
        <StudentQrScannerModal
          studentName={students[0].name}
          onCheckInSuccess={handleStudentCheckInSuccess}
          onClose={() => setIsScannerModalOpen(false)}
        />
      )}
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <MainLayout />
    </AuthProvider>
  );
};
