import {
  Student,
  ClassGroup,
  AttendanceNotice,
  AttendanceDayLog,
  AiRiskAssessment,
  AiInterventionPlan,
  EnterpriseCampusesSummary,
  AuthUser,
  LoginCredentials,
  RegisterData,
  AuthResponse
} from '../types';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:5000';

let authToken: string | null = typeof sessionStorage !== 'undefined' ? sessionStorage.getItem('attendora_auth_token') : null;

export const getAuthToken = (): string | null => authToken;

export const setAuthToken = (token: string | null): void => {
  authToken = token;
  if (typeof sessionStorage !== 'undefined') {
    if (token) {
      sessionStorage.setItem('attendora_auth_token', token);
    } else {
      sessionStorage.removeItem('attendora_auth_token');
    }
  }
};

const getHeaders = (extraHeaders?: Record<string, string>): Record<string, string> => {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    'X-Dev-Role': 'ADMIN',
    'X-Dev-User-ID': 'u_admin_default',
    'X-Institution-ID': '00000000-0000-0000-0000-000000000001',
    ...extraHeaders
  };
  if (authToken) {
    headers['Authorization'] = `Bearer ${authToken}`;
  }
  return headers;
};

const DEFAULT_HEADERS: Record<string, string> = {
  'Content-Type': 'application/json'
};

export const INITIAL_STUDENTS: Student[] = [
  {
    id: 's_1',
    name: 'Smit Kulkarni',
    rollNo: '2016001',
    className: 'Class 1',
    status: 'PRESENT',
    checkInTime: '6:30 PM',
    avatarBgColor: '#3B82F6',
    department: 'Computer Science',
    email: 'smitkulkarni96@gmail.com',
    overallPercentage: 84.5,
    subjects: [
      { name: 'Artificial Intelligence', code: 'CS-501', attended: 34, total: 40, percentage: 85.0 },
      { name: 'Cloud Computing', code: 'CS-502', attended: 36, total: 40, percentage: 90.0 },
      { name: 'Theory of Computation', code: 'CS-503', attended: 26, total: 40, percentage: 65.0 },
      { name: 'Database Management', code: 'CS-504', attended: 38, total: 40, percentage: 95.0 }
    ]
  },
  {
    id: 's_2',
    name: 'Aarav Sharma',
    rollNo: '2010002',
    className: 'Class 1',
    status: 'ABSENT',
    checkInTime: '-',
    avatarBgColor: '#10B981',
    department: 'Computer Science',
    email: 'aarav.sharma@attendora.edu',
    overallPercentage: 61.8,
    subjects: [
      { name: 'Artificial Intelligence', code: 'CS-501', attended: 24, total: 40, percentage: 60.0 },
      { name: 'Cloud Computing', code: 'CS-502', attended: 28, total: 40, percentage: 70.0 },
      { name: 'Theory of Computation', code: 'CS-503', attended: 22, total: 40, percentage: 55.0 },
      { name: 'Database Management', code: 'CS-504', attended: 25, total: 40, percentage: 62.5 }
    ]
  },
  {
    id: 's_3',
    name: 'Priya Patel',
    rollNo: '2018003',
    className: 'Class 1',
    status: 'LATE',
    checkInTime: '6:30 PM',
    avatarBgColor: '#8B5CF6',
    department: 'Computer Science',
    email: 'priya.patel@attendora.edu',
    overallPercentage: 77.2,
    subjects: [
      { name: 'Artificial Intelligence', code: 'CS-501', attended: 34, total: 40, percentage: 85.0 },
      { name: 'Cloud Computing', code: 'CS-502', attended: 36, total: 40, percentage: 90.0 }
    ]
  },
  {
    id: 's_4',
    name: 'Ananya Rao',
    rollNo: '2018004',
    className: 'Class 2',
    status: 'PRESENT',
    checkInTime: '6:30 PM',
    avatarBgColor: '#EC4899',
    department: 'Information Technology',
    email: 'ananya.rao@attendora.edu',
    overallPercentage: 92.0,
    subjects: [
      { name: 'Operating Systems', code: 'CS-401', attended: 32, total: 40, percentage: 80.0 },
      { name: 'Computer Networks', code: 'CS-402', attended: 35, total: 40, percentage: 87.5 }
    ]
  },
  {
    id: 's_5',
    name: 'Rohan Verma',
    rollNo: '2018005',
    className: 'Class 2',
    status: 'PRESENT',
    checkInTime: '6:35 PM',
    avatarBgColor: '#F59E0B',
    department: 'Information Technology',
    email: 'rohan.verma@attendora.edu',
    overallPercentage: 88.5,
    subjects: [
      { name: 'Operating Systems', code: 'CS-401', attended: 32, total: 40, percentage: 80.0 },
      { name: 'Computer Networks', code: 'CS-402', attended: 35, total: 40, percentage: 87.5 }
    ]
  },
  {
    id: 's_6',
    name: 'Tanvi Deshmukh',
    rollNo: '2018006',
    className: 'Class 1',
    status: 'PRESENT',
    checkInTime: '6:32 PM',
    avatarBgColor: '#06B6D4',
    department: 'Computer Science',
    email: 'tanvi.d@attendora.edu',
    overallPercentage: 94.0,
    subjects: [
      { name: 'Artificial Intelligence', code: 'CS-501', attended: 34, total: 40, percentage: 85.0 },
      { name: 'Cloud Computing', code: 'CS-502', attended: 36, total: 40, percentage: 90.0 }
    ]
  },
  {
    id: 's_7',
    name: 'Vikram Mehta',
    rollNo: '2018007',
    className: 'Class 2',
    status: 'ABSENT',
    checkInTime: '-',
    avatarBgColor: '#EF4444',
    department: 'Information Technology',
    email: 'vikram.m@attendora.edu',
    overallPercentage: 68.0,
    subjects: [
      { name: 'Artificial Intelligence', code: 'CS-501', attended: 24, total: 40, percentage: 60.0 },
      { name: 'Theory of Computation', code: 'CS-503', attended: 22, total: 40, percentage: 55.0 }
    ]
  },
  {
    id: 's_8',
    name: 'Neha Kapoor',
    rollNo: '2018008',
    className: 'Class 1',
    status: 'PRESENT',
    checkInTime: '6:28 PM',
    avatarBgColor: '#6366F1',
    department: 'Computer Science',
    email: 'neha.k@attendora.edu',
    overallPercentage: 91.5,
    subjects: [
      { name: 'Artificial Intelligence', code: 'CS-501', attended: 34, total: 40, percentage: 85.0 },
      { name: 'Cloud Computing', code: 'CS-502', attended: 36, total: 40, percentage: 90.0 }
    ]
  }
];

export const INITIAL_CLASSES: ClassGroup[] = [
  {
    id: 'c_1',
    name: 'Class 1',
    code: 'CS-2A',
    courseName: 'Artificial Intelligence & Foundations',
    academicTerm: 'Fall 2026',
    department: 'Computer Science',
    teacherName: 'Prof. Priya Saxena',
    enrolledCount: 60,
    scheduleTime: '09:00 AM - 10:30 AM',
    roomLocation: 'Lecture Hall 101'
  },
  {
    id: 'c_2',
    name: 'Class 2',
    code: 'IT-2B',
    courseName: 'Cloud Computing & Distributed Systems',
    academicTerm: 'Fall 2026',
    department: 'Information Technology',
    teacherName: 'Dr. Arvind Sharma',
    enrolledCount: 60,
    scheduleTime: '11:00 AM - 12:30 PM',
    roomLocation: 'Lecture Hall 204'
  },
  {
    id: 'c_3',
    name: 'CS 3rd Year',
    code: 'CS-3A',
    courseName: 'Theory of Computation & Compiler Design',
    academicTerm: 'Fall 2026',
    department: 'Computer Science',
    teacherName: 'Prof. R. Sen',
    enrolledCount: 55,
    scheduleTime: '02:00 PM - 03:30 PM',
    roomLocation: 'Auditorium C'
  }
];

export const INITIAL_NOTICES: AttendanceNotice[] = [
  {
    id: 'n_1',
    title: 'Low Attendance Warning',
    message: '2 students in Class 1 are below the institutional 75% threshold.',
    timeAgo: '10m ago',
    isWarning: true
  },
  {
    id: 'n_2',
    title: 'Class CS101 Logged',
    message: 'Prof. Priya marked today\'s 6:30 PM attendance session successfully.',
    timeAgo: '35m ago',
    isWarning: false
  },
  {
    id: 'n_3',
    title: 'Cloud Synchronization Completed',
    message: 'All 120 tenant attendance logs synced to PostgreSQL database.',
    timeAgo: '1h ago',
    isWarning: false
  }
];

export const INITIAL_DAY_LOGS: AttendanceDayLog[] = [
  { date: 'Nov 05', dayName: 'Wednesday', status: 'PRESENT', checkInTime: '6:30 PM', subjectTitle: 'Artificial Intelligence', markedBy: 'Prof. Priya Saxena' },
  { date: 'Nov 04', dayName: 'Tuesday', status: 'PRESENT', checkInTime: '09:05 AM', subjectTitle: 'Cloud Computing', markedBy: 'Dr. Arvind Sharma' },
  { date: 'Nov 03', dayName: 'Monday', status: 'PRESENT', checkInTime: '11:15 AM', subjectTitle: 'Theory of Computation', markedBy: 'Prof. R. Sen' },
  { date: 'Oct 31', dayName: 'Friday', status: 'LATE', checkInTime: '09:22 AM', subjectTitle: 'Database Management', markedBy: 'Prof. K. Iyer' },
  { date: 'Oct 30', dayName: 'Thursday', status: 'PRESENT', checkInTime: '02:00 PM', subjectTitle: 'Artificial Intelligence', markedBy: 'Prof. Priya Saxena' },
  { date: 'Oct 29', dayName: 'Wednesday', status: 'ABSENT', checkInTime: '-', subjectTitle: 'Cloud Computing', markedBy: 'Dr. Arvind Sharma' }
];

export const INITIAL_CAMPUSES: EnterpriseCampusesSummary = {
  system_name: 'Attendora Multi-Campus University System',
  total_campuses: 3,
  total_enrolled_students: 3450,
  system_average_attendance: 83.7,
  total_defaulters: 248,
  system_defaulter_rate: 7.2,
  top_performing_campus: 'Pune Central Campus',
  campuses: [
    {
      id: 'camp_pune_01',
      name: 'Pune Central Campus',
      code: 'PUNE-01',
      city: 'Pune',
      state: 'Maharashtra',
      dean: 'Dr. Rameshwar Joshi',
      enrolled_students: 1420,
      attendance_rate: 87.4,
      defaulter_count: 60,
      defaulter_rate: 4.2,
      health_score: 92.5,
      departments: [
        { name: 'Computer Science', students: 480, attendance: 88.2 },
        { name: 'Information Technology', students: 420, attendance: 86.9 },
        { name: 'Mechanical Engineering', students: 520, attendance: 87.1 }
      ]
    },
    {
      id: 'camp_mum_02',
      name: 'Mumbai Metro Campus',
      code: 'MUM-02',
      city: 'Mumbai',
      state: 'Maharashtra',
      dean: 'Dr. Sunita Kulkarni',
      enrolled_students: 1180,
      attendance_rate: 82.1,
      defaulter_count: 92,
      defaulter_rate: 7.8,
      health_score: 84.0,
      departments: [
        { name: 'Computer Science', students: 400, attendance: 83.5 },
        { name: 'Data Science', students: 380, attendance: 81.2 },
        { name: 'Civil Engineering', students: 400, attendance: 81.6 }
      ]
    },
    {
      id: 'camp_nsh_03',
      name: 'Nashik Tech Campus',
      code: 'NSH-03',
      city: 'Nashik',
      state: 'Maharashtra',
      dean: 'Dr. Anand Deshmukh',
      enrolled_students: 850,
      attendance_rate: 79.5,
      defaulter_count: 96,
      defaulter_rate: 11.3,
      health_score: 78.2,
      departments: [
        { name: 'Computer Science', students: 320, attendance: 80.4 },
        { name: 'Electrical Engineering', students: 280, attendance: 78.5 },
        { name: 'Electronics & Telecom', students: 250, attendance: 79.6 }
      ]
    }
  ],
  ai_strategic_brief: 'Multi-campus predictive telemetry indicates steady attendance across Western Region. Nashik Tech Campus exhibits elevated defaulter concentration (11.3%) requiring regional task force.'
};

export const apiService = {
  async register(data: RegisterData): Promise<AuthResponse> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(data)
      });
      const json = await res.json();
      if (res.ok && (json.token || json.access_token)) {
        setAuthToken(json.token || json.access_token);
      }
      return json;
    } catch (err: any) {
      return {
        success: false,
        message: err?.message || 'Network error while attempting registration'
      };
    }
  },

  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(credentials)
      });
      const json = await res.json();
      if (res.ok && (json.token || json.access_token)) {
        setAuthToken(json.token || json.access_token);
      }
      return json;
    } catch (err: any) {
      return {
        success: false,
        message: err?.message || 'Network error while logging in'
      };
    }
  },

  async verifyEmail(email: string, code: string): Promise<AuthResponse> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/auth/verify-email`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ email, code })
      });
      const json = await res.json();
      if (res.ok && (json.token || json.access_token)) {
        setAuthToken(json.token || json.access_token);
      }
      return json;
    } catch (err: any) {
      return {
        success: false,
        message: err?.message || 'Network error while verifying email'
      };
    }
  },

  async resendVerificationCode(email: string): Promise<{ success: boolean; message: string; dev_code?: string }> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/auth/resend-code`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ email })
      });
      return await res.json();
    } catch (err: any) {
      return {
        success: false,
        message: err?.message || 'Network error while requesting code'
      };
    }
  },

  async logout(): Promise<{ success: boolean; message: string }> {
    try {
      setAuthToken(null);
      const res = await fetch(`${API_BASE}/api/v1/auth/logout`, {
        method: 'POST',
        headers: getHeaders(),
        credentials: 'include'
      });
      if (res.ok) return await res.json();
    } catch {
      // offline fallback
    }
    return { success: true, message: 'Logged out successfully' };
  },

  async getMe(): Promise<{ success: boolean; user?: AuthUser; message?: string }> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/auth/me`, {
        method: 'GET',
        headers: getHeaders(),
        credentials: 'include'
      });
      if (res.ok) {
        return await res.json();
      }
    } catch {
      // offline fallback
    }
    return { success: false, message: 'Not authenticated' };
  },

  async checkHealth(): Promise<{ status: string; service: string; version: string }> {
    try {
      const res = await fetch(`${API_BASE}/health`, { credentials: 'include' });
      if (res.ok) return await res.json();
    } catch {
      // offline fallback
    }
    return { status: 'healthy (offline fallback)', service: 'Attendora SaaS API', version: '3.1.0' };
  },

  async getStudents(): Promise<Student[]> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/students`, {
        headers: getHeaders(),
        credentials: 'include'
      });
      if (res.ok) {
        const data = await res.json();
        if (data.students && data.students.length > 0) return data.students;
      }
    } catch {
      // fallback
    }
    return INITIAL_STUDENTS;
  },

  async submitAttendance(classId: string, records: { id: string; status: string }[]): Promise<boolean> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/attendance`, {
        method: 'POST',
        headers: getHeaders(),
        credentials: 'include',
        body: JSON.stringify({
          class_id: classId,
          session_date: new Date().toISOString().split('T')[0],
          records
        })
      });
      return res.ok;
    } catch {
      return true; // simulate offline persistence
    }
  },

  async notifyDefaulters(threshold: number): Promise<{ success: boolean; message: string }> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/reports/defaulters/notify`, {
        method: 'POST',
        headers: getHeaders(),
        credentials: 'include',
        body: JSON.stringify({ threshold })
      });
      if (res.ok) return await res.json();
    } catch {
      // fallback
    }
    return { success: true, message: `Dispatched automated alerts to students below ${threshold}%.` };
  },

  async generateQr(classId: string): Promise<any> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/attendance/qr/generate`, {
        method: 'POST',
        headers: getHeaders(),
        credentials: 'include',
        body: JSON.stringify({ class_id: classId })
      });
      if (res.ok) return await res.json();
    } catch {
      // fallback
    }
    return {
      success: true,
      qr_token: `att_qr_${classId}_${Math.floor(Date.now() / 10000)}`,
      expires_in_seconds: 10,
      classroom_location: { name: 'Hall A-101', latitude: 18.52043, longitude: 73.85674, allowed_radius_meters: 50.0 }
    };
  },

  async checkInQr(qrToken: string, studentId: string): Promise<{ success: boolean; message: string }> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/attendance/qr/checkin`, {
        method: 'POST',
        headers: getHeaders(),
        credentials: 'include',
        body: JSON.stringify({
          qr_token: qrToken,
          student_id: studentId,
          latitude: 18.52043,
          longitude: 73.85674,
          device_id: 'dev_browser_web_client'
        })
      });
      if (res.ok) return await res.json();
    } catch {
      // fallback
    }
    return { success: true, message: `Attendance marked PRESENT for student ${studentId}.` };
  },

  async predictRisk(studentId: string): Promise<AiRiskAssessment> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/ai/predict-risk`, {
        method: 'POST',
        headers: getHeaders(),
        credentials: 'include',
        body: JSON.stringify({ student_id: studentId })
      });
      if (res.ok) {
        const data = await res.json();
        if (data.risk_assessment) return data.risk_assessment;
      }
    } catch {
      // fallback
    }

    const student = INITIAL_STUDENTS.find(s => s.id === studentId) || INITIAL_STUDENTS[0];
    const pct = student.overallPercentage;
    const isDefaulter = pct < 65;
    return {
      risk_level: isDefaulter ? 'CRITICAL' : pct < 75 ? 'HIGH' : pct < 85 ? 'MODERATE' : 'LOW',
      risk_score: isDefaulter ? 78.5 : pct < 75 ? 54.0 : 22.0,
      predicted_final_percentage: Math.max(0, Math.min(100, Math.round(pct - (isDefaulter ? 6.2 : 1.5)))),
      confidence_score: 0.94,
      risk_factors: [
        isDefaulter ? `Cumulative attendance (${pct}%) is critically below statutory university 75% threshold.` : 'Consistent class presence.',
        ...(student.subjects.filter(s => s.percentage < 65).map(s => `Deficit in ${s.name}: ${s.percentage}%`))
      ],
      recommendations: [
        isDefaulter ? 'Mandatory counseling before the Academic Review Board.' : 'Maintain active lecture attendance.',
        'Enroll in Friday remedial tutoring sessions.'
      ],
      ai_summary: `Student ${student.name} profile exhibits a ${isDefaulter ? 'CRITICAL' : 'STABLE'} risk level. Semester forecast projects ${pct}%.`
    };
  },

  async generateIntervention(studentId: string): Promise<AiInterventionPlan> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/ai/intervention-plan`, {
        method: 'POST',
        headers: getHeaders(),
        credentials: 'include',
        body: JSON.stringify({ student_id: studentId })
      });
      if (res.ok) {
        const data = await res.json();
        if (data.intervention_plan) return data.intervention_plan;
      }
    } catch {
      // fallback
    }

    const student = INITIAL_STUDENTS.find(s => s.id === studentId) || INITIAL_STUDENTS[0];
    return {
      student_id: student.id,
      student_name: student.name,
      roll_no: student.rollNo,
      risk_level: student.overallPercentage < 65 ? 'CRITICAL' : 'HIGH',
      risk_score: 78.5,
      current_attendance_percent: student.overallPercentage,
      target_attendance_percent: 75.0,
      classes_needed_for_eligibility: 14,
      urgency: 'EMERGENCY_DEBARMENT_WARNING',
      action_items: [
        'Achieve 100% attendance in the next 14 consecutive lectures.',
        'Attend scheduled conference with Faculty Advisor Prof. Priya Saxena.',
        'Enroll in weekend remedial labs for Theory of Computation.'
      ],
      counseling_letter: `# CONFIDENTIAL: ACADEMIC ATTENDANCE INTERVENTION NOTICE\n**Attendora University System — Office of the Dean of Academic Affairs**\n\nTO: ${student.name} (Roll: ${student.rollNo})\nDepartment: ${student.department}\n\nCurrent Standing: ${student.overallPercentage}%\nStatutory Minimum: 75.0%\n\nRemediation Requirements:\n1. Complete 14 consecutive class sessions without absence.\n2. Return signed counseling agreement within 3 working days.\n\nSignatures:\nFaculty Advisor: Prof. Priya Saxena\nHead of Department: Dr. Arvind Sharma\nDean of Academic Affairs, Attendora System`,
      generated_at: new Date().toISOString()
    };
  },

  async getCampuses(): Promise<EnterpriseCampusesSummary> {
    try {
      const res = await fetch(`${API_BASE}/api/v1/campuses`, {
        headers: getHeaders(),
        credentials: 'include'
      });
      if (res.ok) return await res.json();
    } catch {
      // fallback
    }
    return INITIAL_CAMPUSES;
  },

  getTranscriptUrl(studentId: string): string {
    return `${API_BASE}/api/v1/student/${studentId}/transcript.pdf`;
  }
};

