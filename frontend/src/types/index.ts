export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE';

export type UserRole = 'ADMIN' | 'TEACHER' | 'STUDENT';

export type NavTab = 'DASHBOARD' | 'STUDENTS' | 'ATTENDANCE' | 'CLASSES' | 'REPORTS' | 'SETTINGS';

export interface SubjectAttendance {
  name: string;
  code: string;
  attended: number;
  total: number;
  percentage: number;
}

export interface Student {
  id: string;
  name: string;
  rollNo: string;
  className: string;
  status: AttendanceStatus;
  checkInTime: string;
  avatarBgColor: string;
  department: string;
  email: string;
  overallPercentage: number;
  subjects: SubjectAttendance[];
}

export interface ClassGroup {
  id: string;
  name: string;
  code: string;
  department: string;
  teacherName: string;
  enrolledCount: number;
  scheduleTime: string;
  courseName?: string;
  academicTerm?: string;
  roomLocation?: string;
}

export interface AttendanceNotice {
  id: string;
  title: string;
  message: string;
  timeAgo: string;
  isRead?: boolean;
  isWarning?: boolean;
}

export interface AttendanceDayLog {
  date: string;
  dayName: string;
  status: AttendanceStatus;
  checkInTime: string;
  subjectTitle: string;
  markedBy: string;
}

export type RiskLevel = 'LOW' | 'MODERATE' | 'HIGH' | 'CRITICAL';

export interface AiRiskAssessment {
  risk_level: RiskLevel;
  risk_score: number;
  predicted_final_percentage: number;
  confidence_score: number;
  risk_factors: string[];
  recommendations: string[];
  ai_summary: string;
  model_provider?: string;
  student_id?: string;
}

export interface AiInterventionPlan {
  student_id: string;
  student_name: string;
  roll_no: string;
  risk_level: RiskLevel;
  risk_score: number;
  current_attendance_percent: number;
  target_attendance_percent: number;
  classes_needed_for_eligibility: number;
  urgency: string;
  action_items: string[];
  counseling_letter: string;
  generated_at: string;
}

export interface CampusDepartment {
  name: string;
  students: number;
  attendance: number;
}

export interface CampusDetail {
  id: string;
  name: string;
  code: string;
  city: string;
  state: string;
  dean: string;
  enrolled_students: number;
  attendance_rate: number;
  defaulter_count: number;
  defaulter_rate: number;
  health_score: number;
  departments: CampusDepartment[];
}

export interface EnterpriseCampusesSummary {
  system_name: string;
  system_tenant_id?: string;
  total_campuses: number;
  total_enrolled_students: number;
  system_average_attendance: number;
  total_defaulters: number;
  system_defaulter_rate: number;
  top_performing_campus: string;
  campuses: CampusDetail[];
  ai_strategic_brief: string;
}

export interface AuthUser {
  id: string;
  uid?: string;
  name: string;
  full_name?: string;
  email: string;
  role: UserRole;
  department?: string;
  roll_no?: string;
  institution_id?: string;
  is_active?: boolean;
  created_at?: string;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData {
  name: string;
  email: string;
  password: string;
  confirm_password?: string;
  role?: UserRole;
  department?: string;
  roll_number?: string;
}

export interface AuthResponse {
  success: boolean;
  message?: string;
  user?: AuthUser;
  token?: string;
  access_token?: string;
  errors?: Record<string, string>;
  requires_verification?: boolean;
  dev_code?: string;
  email?: string;
}


