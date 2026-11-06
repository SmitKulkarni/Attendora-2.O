-- ============================================================================
-- ATTENDORA SAAS - MULTI-TENANT POSTGRESQL SCHEMA WITH ROW-LEVEL SECURITY (RLS)
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. INSTITUTIONS (Tenants)
CREATE TABLE IF NOT EXISTS institutions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    slug VARCHAR(64) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    subscription_plan VARCHAR(32) DEFAULT 'PRO',
    alert_threshold INT DEFAULT 75,
    strict_lock_hours INT DEFAULT 48,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. USERS (Faculty, Admins, Students)
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    institution_id UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    firebase_uid VARCHAR(128) UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL CHECK (role IN ('ADMIN', 'TEACHER', 'STUDENT')),
    department VARCHAR(128) DEFAULT 'Computer Science',
    roll_no VARCHAR(64),
    avatar_color BIGINT DEFAULT 4282155766,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_institution ON users(institution_id);
CREATE INDEX IF NOT EXISTS idx_users_firebase_uid ON users(firebase_uid);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- 3. CLASSES (Class Groups / Cohorts)
CREATE TABLE IF NOT EXISTS classes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    institution_id UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    teacher_id UUID REFERENCES users(id) ON DELETE SET NULL,
    name VARCHAR(128) NOT NULL,
    code VARCHAR(64) NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    department VARCHAR(128) NOT NULL,
    teacher_name VARCHAR(255) NOT NULL,
    academic_term VARCHAR(64) DEFAULT 'Fall 2026',
    schedule_time VARCHAR(64) DEFAULT '09:00 AM - 10:30 AM',
    room_location VARCHAR(128) DEFAULT 'Lecture Hall 101',
    max_capacity INT DEFAULT 60,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_classes_institution ON classes(institution_id);

-- 4. CLASS ENROLLMENTS
CREATE TABLE IF NOT EXISTS class_enrollments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    institution_id UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    class_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    enrolled_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (class_id, student_id)
);

CREATE INDEX IF NOT EXISTS idx_enrollments_institution ON class_enrollments(institution_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_student ON class_enrollments(student_id);

-- 5. ATTENDANCE SESSIONS (One Lecture / Session)
CREATE TABLE IF NOT EXISTS attendance_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    institution_id UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    class_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    teacher_id UUID REFERENCES users(id) ON DELETE SET NULL,
    class_name VARCHAR(128) NOT NULL,
    session_date DATE NOT NULL,
    session_time VARCHAR(32) NOT NULL,
    marked_by VARCHAR(255) NOT NULL,
    is_locked BOOLEAN DEFAULT FALSE,
    locked_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sessions_institution ON attendance_sessions(institution_id);
CREATE INDEX IF NOT EXISTS idx_sessions_class_date ON attendance_sessions(class_id, session_date);

-- 6. ATTENDANCE RECORDS (Individual Student Marks)
CREATE TABLE IF NOT EXISTS attendance_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    institution_id UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    session_id UUID NOT NULL REFERENCES attendance_sessions(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(16) NOT NULL CHECK (status IN ('PRESENT', 'ABSENT', 'LATE')),
    check_in_time VARCHAR(32) DEFAULT '-',
    marked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (session_id, student_id)
);

CREATE INDEX IF NOT EXISTS idx_records_institution ON attendance_records(institution_id);
CREATE INDEX IF NOT EXISTS idx_records_student ON attendance_records(student_id);
CREATE INDEX IF NOT EXISTS idx_records_session ON attendance_records(session_id);

-- 7. ATTENDANCE AUDITS (Immutable Log for Alterations)
CREATE TABLE IF NOT EXISTS attendance_audits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    institution_id UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    record_id UUID NOT NULL REFERENCES attendance_records(id) ON DELETE CASCADE,
    modified_by UUID REFERENCES users(id),
    old_status VARCHAR(16) NOT NULL,
    new_status VARCHAR(16) NOT NULL,
    change_reason TEXT,
    modified_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audits_institution ON attendance_audits(institution_id);

-- 8. NOTICES & ALERTS
CREATE TABLE IF NOT EXISTS notices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    institution_id UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_warning BOOLEAN DEFAULT FALSE,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notices_institution ON notices(institution_id);

-- ============================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- ============================================================================

-- Enable RLS on all tenant-scoped tables
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE classes ENABLE ROW LEVEL SECURITY;
ALTER TABLE class_enrollments ENABLE ROW LEVEL SECURITY;
ALTER TABLE attendance_sessions ENABLE ROW LEVEL SECURITY;
ALTER TABLE attendance_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE attendance_audits ENABLE ROW LEVEL SECURITY;
ALTER TABLE notices ENABLE ROW LEVEL SECURITY;

-- Dynamic Tenant Isolation Policy using session variable 'app.current_tenant'
CREATE POLICY tenant_isolation_users ON users
    USING (institution_id = NULLIF(current_setting('app.current_tenant', true), '')::uuid);

CREATE POLICY tenant_isolation_classes ON classes
    USING (institution_id = NULLIF(current_setting('app.current_tenant', true), '')::uuid);

CREATE POLICY tenant_isolation_enrollments ON class_enrollments
    USING (institution_id = NULLIF(current_setting('app.current_tenant', true), '')::uuid);

CREATE POLICY tenant_isolation_sessions ON attendance_sessions
    USING (institution_id = NULLIF(current_setting('app.current_tenant', true), '')::uuid);

CREATE POLICY tenant_isolation_records ON attendance_records
    USING (institution_id = NULLIF(current_setting('app.current_tenant', true), '')::uuid);

CREATE POLICY tenant_isolation_audits ON attendance_audits
    USING (institution_id = NULLIF(current_setting('app.current_tenant', true), '')::uuid);

CREATE POLICY tenant_isolation_notices ON notices
    USING (institution_id = NULLIF(current_setting('app.current_tenant', true), '')::uuid);

-- ============================================================================
-- 9. ENTERPRISE MULTI-CAMPUS FEDERATION & AI REMEDIATION AUDITS
-- ============================================================================

CREATE TABLE IF NOT EXISTS campuses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    institution_id UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    code VARCHAR(32) NOT NULL,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(128) NOT NULL,
    state VARCHAR(128) NOT NULL,
    dean_name VARCHAR(255) NOT NULL,
    enrolled_students INT DEFAULT 0,
    attendance_rate NUMERIC(5,2) DEFAULT 85.0,
    health_score NUMERIC(5,2) DEFAULT 90.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (institution_id, code)
);

CREATE INDEX IF NOT EXISTS idx_campuses_institution ON campuses(institution_id);

CREATE TABLE IF NOT EXISTS ai_intervention_audits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    institution_id UUID NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    risk_level VARCHAR(32) NOT NULL CHECK (risk_level IN ('LOW', 'MODERATE', 'HIGH', 'CRITICAL')),
    risk_score NUMERIC(5,2) NOT NULL,
    classes_needed INT DEFAULT 0,
    counseling_letter_md TEXT NOT NULL,
    issued_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ai_interventions_institution ON ai_intervention_audits(institution_id);
CREATE INDEX IF NOT EXISTS idx_ai_interventions_student ON ai_intervention_audits(student_id);

ALTER TABLE campuses ENABLE ROW LEVEL SECURITY;
ALTER TABLE ai_intervention_audits ENABLE ROW LEVEL SECURITY;

CREATE POLICY tenant_isolation_campuses ON campuses
    USING (institution_id = NULLIF(current_setting('app.current_tenant', true), '')::uuid);

CREATE POLICY tenant_isolation_ai_interventions ON ai_intervention_audits
    USING (institution_id = NULLIF(current_setting('app.current_tenant', true), '')::uuid);

-- ============================================================================
-- INITIAL SEED DATA FOR DEMO TENANT & MULTI-CAMPUS FEDERATION
-- ============================================================================

INSERT INTO institutions (id, slug, name, subscription_plan, alert_threshold)
VALUES ('00000000-0000-0000-0000-000000000001', 'demo-university', 'Attendora Modern University', 'ENTERPRISE', 75)
ON CONFLICT (id) DO NOTHING;

INSERT INTO campuses (id, institution_id, code, name, city, state, dean_name, enrolled_students, attendance_rate, health_score)
VALUES 
    ('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'PUNE-01', 'Pune Central Campus', 'Pune', 'Maharashtra', 'Dr. Rameshwar Joshi', 1420, 87.4, 92.5),
    ('10000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'MUM-02', 'Mumbai Metro Campus', 'Mumbai', 'Maharashtra', 'Dr. Sunita Kulkarni', 1180, 82.1, 84.0),
    ('10000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'NSH-03', 'Nashik Tech Campus', 'Nashik', 'Maharashtra', 'Dr. Anand Deshmukh', 850, 79.5, 78.2)
ON CONFLICT (institution_id, code) DO NOTHING;

