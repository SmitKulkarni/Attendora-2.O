# Attendora (Attendora-2.O) - Long-Term Project Memory

This document maintains the persistent project memory and technical reference manual for **Attendora**. It serves as an authoritative knowledge base for engineering context, application architecture, feature status, database schemas, API contracts, and roadmap milestones.

---

## 1. Project Overview

**Attendora** is a modern, enterprise-grade EdTech SaaS attendance intelligence and class management platform. Designed specifically for colleges, universities, and multi-campus educational institutions, it eliminates paper-based attendance registers and disconnected spreadsheets by providing a unified, multi-tenant digital ecosystem.

### Key Objectives
- **Automated Defaulter Detection**: Real-time identification of students falling below statutory institutional attendance thresholds (e.g., 75%).
- **Multi-Role User Experience**: Tailored interfaces for Institutional Administrators, Faculty/Professors, and Students within a single unified client.
- **Fast Synchronous Marking**: Rapid single-tap classroom attendance marking supporting 60+ students per class in under 60 seconds with bulk actions.
- **Academic Integrity**: 48-hour edit window lock for faculty with immutable audit trails to prevent retroactive grade and attendance tampering.
- **Responsive Dual-Form Factor**: Native adaptive layout that renders a desktop/tablet dashboard with a persistent sidebar on wide screens (>= 800dp) and a mobile portal on compact screens (< 800dp).

---

## 2. Tech Stack

### Client-Side (Android Native)
- **Language**: Kotlin 2.0+
- **Minimum SDK**: 24 (Android 7.0 Nougat)
- **Target / Compile SDK**: 36 (Android 15 / 16)
- **UI Toolkit**: Jetpack Compose with Material 3 Design System
- **Asynchronous & Concurrency**: Kotlin Coroutines & StateFlow / SharedFlow
- **Architecture Pattern**: MVVM / MVI (Model-View-ViewModel with Unidirectional Data Flow)
- **Local Persistence & Caching**: Android Jetpack Room DB (KSP compiler enabled)
- **Serialization**: Moshi Kotlin Codegen
- **Image Loading**: Coil Compose
- **Networking**: Retrofit 2 & OkHttp 3 with Logging Interceptor
- **AI Integration**: Google Firebase AI & Server-Side Gemini API integration

### Backend & Cloud Infrastructure (SaaS Architecture Specification)
- **API Runtime**: Python (Flask / FastAPI) with Gunicorn WSGI
- **Database Engine**: PostgreSQL 15+ with Row-Level Security (RLS)
- **Caching & Broker**: Redis 7.x
- **Task Queue / Background Jobs**: Celery workers for async batch email dispatch and PDF generation
- **Authentication**: Firebase Authentication with JWT Custom Claims (`institution_id`, `role`, `user_id`)
- **Object Storage**: AWS S3 / Google Cloud Storage for exported PDF transcripts and institutional reports
- **Deployment Platform**: Docker containerized on AWS App Runner / Render / Google Cloud Run

---

## 3. Features Completed vs. Pending

### Status Matrix

| Module / Feature | Description | Status | Layer |
| :--- | :--- | :--- | :--- |
| **Dual-Layout Adaptive Scaffold** | Dynamic switching between wide-screen sidebar and mobile drawer/bottom nav | Completed | UI (`AttendoraApp.kt`) |
| **Role Simulation Engine** | Seamless switching between Administrator, Faculty, and Student views | Completed | ViewModel (`AttendoraViewModel.kt`) |
| **Admin Dashboard Overview** | 4 Top metric cards (Total, Today %, On-time %, Absent) | Completed | UI (`AdminDashboardScreen.kt`) |
| **Attendance Trends Chart** | Custom Canvas spline curve visualization with gradient fill | Completed | UI (`AttendanceTrendsChart.kt`) |
| **Weekly Attendance Chart** | Custom Canvas grouped bar chart (Mon-Fri Present vs Absent) | Completed | UI (`WeeklyAttendanceChart.kt`) |
| **Student Roster Management** | Search by name/roll/department, class dropdown filtering | Completed | UI (`StudentsManagementScreen.kt`) |
| **Add New Student** | Interactive dialog adding student with dynamic avatar color & subjects | Completed | UI / ViewModel |
| **Student Detail Dialog** | Modal showing overall %, subject-level breakdown bars, quick status edit | Completed | UI (`StudentDetailDialog.kt`) |
| **Attendance Marking Screen** | Single-tap status toggle (Present -> Absent -> Late), class selector | Completed | UI (`AttendanceMarkingScreen.kt`) |
| **Bulk Attendance Marking** | Instant "Mark All Present" and "Mark All Absent" action buttons | Completed | UI / ViewModel |
| **Session Submission & Cloud Sync**| Submits session, locks state, and triggers toast/notice event | Completed | UI / ViewModel |
| **Classes Management** | Class cards showing enrolled counts, schedules, teachers, and rooms | Completed | UI (`ClassesManagementScreen.kt`) |
| **Create Class Suite** | Comprehensive creation flow (course, code, dept, term, capacity slider) | Completed | UI (`CreateClassScreen.kt`) |
| **Defaulter Analytics & Reports** | Dynamic threshold slider (50%-90%), at-risk badges, export buttons | Completed | UI (`ReportsScreen.kt`) |
| **Automated Defaulter Alerts** | Batch dispatch simulation of email alerts to at-risk students | Completed | UI / ViewModel |
| **Student Mobile Portal** | Dedicated mobile view with percentage donut, subject cards, day timeline | Completed | UI (`StudentMobileScreen.kt`) |
| **Tenant Settings Screen** | Toggles for 48h strict lock, automated email alerts, and sync status | Completed | UI (`SettingsScreen.kt`) |
| **Room DB Offline Persistence** | Android Jetpack Room SQLite, entities, DAOs & TypeConverters | Completed | Data / DB (`db`) |
| **Attendance Repository Layer** | Reactive single source of truth with optimistic local writes | Completed | Repository (`data.repository`) |
| **Offline Sync Queue** | Sync queue tracking dirty records (`sync_queue`) with sync manager | Completed | Sync (`data.sync`) |
| **SaaS Python/Flask Backend API** | Containerized REST service with health, attendance, students & class endpoints | Completed | Backend (`backend/app.py`) |
| **PostgreSQL Multi-Tenant RLS** | Database schema with Row-Level Security policies on `app.current_tenant` | Completed | Backend / DB (`schema.sql`) |
| **Auth & Tenant Middleware** | JWT verification, Custom Claims extraction (`institution_id`, `role`), RBAC | Completed | Backend (`middleware.py`) |
| **Retrofit Cloud API Integration** | Live REST networking with Bearer JWT token authentication & DTOs | Completed | Network / Client (`network`) |
| **Defaulter Automation Engine** | Asynchronous task queue, scheduled nightly audit & SendGrid batcher | Completed | Worker (`tasks.py`, `emailer.py`) |
| **PDF Attendance Transcript Engine**| ReportLab institutional transcript generator & streaming endpoint | Completed | Reporting (`pdf_generator.py`) |
| **Dynamic TOTP QR & Geofencing** | Rotating 10s QR barcode, 50m Haversine GPS geofence & anti-proxy guard | Completed | Smart Attendance (`qr_engine.py`, `geofence.py`) |
| **Compose QR Code Visualizer** | Pure Canvas 2D barcode matrix renderer with finder patterns | Completed | UI (`QrCodeCanvas.kt`) |
| **Faculty QR & Student Scanner Modals**| `DynamicQrDialog` and `StudentQrScannerDialog` with animated scanning laser | Completed | UI Components |
| **Full-Stack Project Restructuring** | Decoupled root repository into clean `frontend/` and `backend/` sub-architectures | Completed | Architecture / Root |
| **Web Frontend (React 18 + TS + Vite)**| Enterprise dashboard with high-fidelity Attendora visual system | Completed | Frontend (`frontend/src`) |
| **JWT & Cookie Authentication** | Salted scrypt/bcrypt password hashing, HS256 tokens, HttpOnly cookie support | Completed | Backend / IAM (`auth.py`, `auth_routes.py`) |
| **Multi-Tier Request Validation** | RFC 5322 regex, password complexity, duplicate rejection, and client instant validation | Completed | Security & Validation (`validators.py`) |
| **Role-Based Authorization (RBAC)** | Strict `@require_auth()` and `@require_roles()` middleware enforcing 401/403 | Completed | Security (`middleware.py`) |
| **Rate Limiting & Security Headers**| Sliding window IP rate limiter (HTTP 429), OWASP security headers injection | Completed | Security (`security.py`) |
| **Frontend Auth Context & Modals** | React `AuthProvider`, `useAuth()`, `AuthModal.tsx` with presets, and `ProtectedRoute.tsx` | Completed | Frontend UI (`AuthContext.tsx`, `AuthModal.tsx`) |
| **Predictive Gemini AI Analytics** | Multi-semester dropout risk modeling, 4 risk tiers, and heuristic fallback | Completed | AI Engine (`backend/ai_engine.py`) |
| **Academic Intervention Generator** | Automated consecutive class calculator & formal counseling letter drafts | Completed | AI Engine (`backend/ai_engine.py`) |
| **Enterprise Multi-Campus Federation** | Cross-campus hierarchy (`System -> Campuses -> Depts`) & aggregate telemetry | Completed | Backend & UI (`ReportsScreen.kt`) |

---

## 4. API Endpoints Specification

Below is the production REST API specification consumed by the Attendora clients.

### Authentication & Tenant Context
All protected endpoints require the header:
```http
Authorization: Bearer <FIREBASE_ID_TOKEN>
```
The backend middleware unpacks `decoded_token["institution_id"]` and enforces tenant scoping.

---

### Authentication & Identity Endpoints

#### `POST /api/v1/auth/register`
- **Access**: Public (Rate-limited: 15 req/5 min)
- **Description**: Registers a new user, hashes password with salted scrypt, sets HttpOnly cookie, and returns JWT.
- **Request Body**:
```json
{
  "name": "Jane Doe",
  "email": "jane.doe@attendora.edu",
  "password": "StrongPassword!123",
  "confirm_password": "StrongPassword!123",
  "role": "TEACHER",
  "department": "Computer Science"
}
```
- **Response (201 Created)**:
```json
{
  "success": true,
  "message": "Account created successfully.",
  "user": { "id": "usr_9b1a8...", "email": "jane.doe@attendora.edu", "name": "Jane Doe", "role": "TEACHER" },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### `POST /api/v1/auth/verify-email`
- **Access**: Public
- **Description**: Verifies a 6-digit numeric verification code sent to the user's email, activates account, and issues active JWT session.
- **Request Body**:
```json
{
  "email": "jane.doe@attendora.edu",
  "code": "849201"
}
```
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Email successfully verified! Welcome to Attendora.",
  "user": { "id": "usr_9b1a8...", "email": "jane.doe@attendora.edu", "name": "Jane Doe", "role": "TEACHER" },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### `POST /api/v1/auth/resend-code`
- **Access**: Public (Rate-limited: 15 req/5 min)
- **Description**: Dispatches a fresh 6-digit verification code with 15-minute expiration to the user's email.
- **Request Body**:
```json
{
  "email": "jane.doe@attendora.edu"
}
```
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "A fresh 6-digit verification code has been sent to your email."
}
```

#### `POST /api/v1/auth/login`
- **Access**: Public (Rate-limited: 15 req/5 min)
- **Description**: Verifies credentials using constant-time hash verification. Sets `attendora_token` HttpOnly cookie.
- **Request Body**:
```json
{
  "email": "admin@attendora.edu",
  "password": "Admin@12345"
}
```
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Login successful",
  "user": { "id": "u_admin_1", "email": "admin@attendora.edu", "name": "Institutional Admin", "role": "ADMIN" },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### `POST /api/v1/auth/logout`
- **Access**: Public / Authenticated
- **Description**: Clears the `attendora_token` HttpOnly cookie (`max_age=0`).
- **Response (200 OK)**:
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

#### `GET /api/v1/auth/me`
- **Role Required**: `ADMIN`, `TEACHER`, `STUDENT`
- **Description**: Returns the sanitized profile for the authenticated session (via cookie or Bearer header).
- **Response (200 OK)**:
```json
{
  "success": true,
  "user": {
    "id": "u_admin_1",
    "email": "admin@attendora.edu",
    "name": "Institutional Admin",
    "role": "ADMIN",
    "department": "Computer Science",
    "institution_id": "00000000-0000-0000-0000-000000000001"
  }
}
```

---

### 1. Attendance Marking & Sessions

#### `POST /api/v1/attendance`
- **Role Required**: `TEACHER`, `ADMIN`
- **Description**: Submits or bulk-updates attendance records for a specific class session.
- **Request Body**:
```json
{
  "class_id": "c_1",
  "academic_term": "Fall 2026",
  "session_date": "2026-11-05",
  "session_time": "09:00 AM",
  "records": [
    { "student_id": "s_1", "status": "PRESENT" },
    { "student_id": "s_2", "status": "ABSENT" },
    { "student_id": "s_3", "status": "LATE" }
  ]
}
```
- **Response (201 Created)**:
```json
{
  "session_id": "att_sess_98234",
  "status": "LOCKED_FOR_FACULTY_IN_48H",
  "records_processed": 3,
  "timestamp": "2026-11-05T09:45:00Z"
}
```

#### `GET /api/v1/attendance/summary`
- **Role Required**: `TEACHER`, `ADMIN`
- **Query Params**: `class_id` (optional), `date_from`, `date_to`
- **Response (200 OK)**:
```json
{
  "total_students": 120,
  "today_attendance_percent": 94,
  "on_time_percent": 88,
  "absent_count": 7,
  "trends": [
    { "month": "Sep", "value": 12.0 },
    { "month": "Oct", "value": 58.0 },
    { "month": "Nov", "value": 98.0 }
  ]
}
```

---

### 2. Student Portal & Rosters

#### `GET /api/v1/student/dashboard`
- **Role Required**: `STUDENT`
- **Description**: Returns individual attendance analytics, subject breakdown, and recent history for the authenticated student.
- **Response (200 OK)**:
```json
{
  "student_id": "s_1",
  "name": "Smit Kulkarni",
  "roll_no": "2016001",
  "department": "Computer Science",
  "overall_percentage": 84.5,
  "status": "GOOD_STANDING",
  "subjects": [
    {
      "name": "Artificial Intelligence",
      "code": "CS-501",
      "attended": 34,
      "total": 40,
      "percentage": 85.0
    },
    {
      "name": "Theory of Computation",
      "code": "CS-503",
      "attended": 26,
      "total": 40,
      "percentage": 65.0
    }
  ]
}
```

#### `GET /api/v1/students`
- **Role Required**: `TEACHER`, `ADMIN`
- **Query Params**: `class_id`, `search`, `limit`, `offset`
- **Response (200 OK)**: List of students matching query with subject summaries.

#### `POST /api/v1/students`
- **Role Required**: `ADMIN`
- **Request Body**:
```json
{
  "name": "Aarav Sharma",
  "roll_no": "2010002",
  "class_id": "c_1",
  "department": "Computer Science",
  "email": "aarav.sharma@attendora.edu"
}
```
- **Response (201 Created)**: Created student record with unique ID.

---

### 3. Classes & Academic Core

#### `GET /api/v1/classes`
- **Role Required**: `TEACHER`, `ADMIN`, `STUDENT`
- **Response (200 OK)**: Array of active classes, schedules, assigned teachers, and enrollments.

#### `POST /api/v1/classes`
- **Role Required**: `ADMIN`
- **Request Body**:
```json
{
  "name": "Class 1",
  "code": "CS-2A",
  "course_name": "Artificial Intelligence & Foundations",
  "academic_term": "Fall 2026",
  "department": "Computer Science",
  "teacher_name": "Prof. Priya Saxena",
  "enrolled_count": 60,
  "schedule_time": "09:00 AM - 10:30 AM",
  "room_location": "Lecture Hall 101"
}
```

---

### 4. Defaulters & Reporting

#### `GET /api/v1/reports/defaulters`
- **Role Required**: `ADMIN`, `TEACHER`
- **Query Params**: `threshold` (default: 75), `department`, `class_id`
- **Response (200 OK)**: List of at-risk students below threshold percentage.

#### `POST /api/v1/reports/defaulters/notify`
- **Role Required**: `ADMIN`
- **Description**: Triggers asynchronous background job to email/SMS warning notices to defaulters and guardians.
- **Response (202 Accepted)**:
```json
{
  "task_id": "celery_task_9812739",
  "status": "QUEUED",
  "recipients_count": 2,
  "message": "Defaulter notices dispatched to Celery background queue."
}
```

---

### 5. Smart Attendance (Dynamic QR & Geofencing)

#### `POST /api/v1/attendance/qr/generate`
- **Role Required**: `TEACHER`, `ADMIN`
- **Description**: Generates a 10-second rolling dynamic QR token signed with HMAC-SHA256 along with classroom GPS coordinates.

#### `POST /api/v1/attendance/qr/checkin`
- **Role Required**: `STUDENT`, `TEACHER`, `ADMIN`
- **Description**: Verifies TOTP timestamp freshness, 50m Haversine classroom geofence, and anti-proxy device binding before recording `PRESENT`.

---

### 6. Gemini AI Predictive Intelligence & Enterprise Multi-Campus

#### `POST /api/v1/ai/predict-risk`
- **Role Required**: `STUDENT`, `TEACHER`, `ADMIN`
- **Description**: Sanitizes student PII, evaluates attendance vectors, and outputs risk tier (`LOW`, `MODERATE`, `HIGH`, `CRITICAL`), risk score, projected completion rate, and diagnostic factors.
- **Request Body**: `{"student_id": "s_2"}`

#### `POST /api/v1/ai/intervention-plan`
- **Role Required**: `TEACHER`, `ADMIN`
- **Description**: Calculates consecutive lectures required to regain statutory 75% standing, generates tailored remediation steps, and formats an official counseling notice letter.
- **Request Body**: `{"student_id": "s_2"}`

#### `GET /api/v1/ai/insights`
- **Role Required**: `TEACHER`, `ADMIN`
- **Description**: Aggregates tenant-wide attendance health index, risk distribution, and executive strategic summary.

#### `GET /api/v1/campuses`
- **Role Required**: `STUDENT`, `TEACHER`, `ADMIN`
- **Description**: Enterprise multi-campus federation endpoint returning system aggregate attendance, individual campus performance, and regional AI strategic briefs.

---

## 5. Database Schema Summary

Attendora utilizes a multi-tenant relational schema in PostgreSQL. Every table contains `institution_id` for Row-Level Security.

```
+-------------------------------------------------------------+
|                        institutions                         |
+-------------------------------------------------------------+
| id (UUID, PK)                                               |
| name (VARCHAR)                                              |
| slug (VARCHAR, UNIQUE)                                      |
| subscription_plan (VARCHAR)                                 |
| alert_threshold (INT DEFAULT 75)                            |
| strict_lock_hours (INT DEFAULT 48)                          |
| created_at (TIMESTAMP)                                      |
+-------------------------------------------------------------+
                              | 1
                              |
                              | N
+-------------------------------------------------------------+
|                           users                             |
+-------------------------------------------------------------+
| id (UUID, PK)                                               |
| institution_id (UUID, FK -> institutions.id)                |
| firebase_uid (VARCHAR, UNIQUE)                              |
| email (VARCHAR)                                             |
| full_name (VARCHAR)                                         |
| role (ENUM: ADMIN, TEACHER, STUDENT)                        |
| avatar_color (BIGINT)                                       |
| created_at (TIMESTAMP)                                      |
+-------------------------------------------------------------+
                              | 1
                              |
                              | N
+-------------------------------------------------------------+
|                          classes                            |
+-------------------------------------------------------------+
| id (UUID, PK)                                               |
| institution_id (UUID, FK -> institutions.id)                |
| teacher_id (UUID, FK -> users.id)                           |
| name (VARCHAR)                                              |
| code (VARCHAR)                                              |
| department (VARCHAR)                                        |
| academic_term (VARCHAR)                                     |
| schedule_time (VARCHAR)                                     |
| room_location (VARCHAR)                                     |
| max_capacity (INT)                                          |
+-------------------------------------------------------------+
         | 1                                       | 1
         |                                         |
         | N                                       | N
+-----------------------+           +-------------------------+
|   class_enrollments   |           |   attendance_sessions   |
+-----------------------+           +-------------------------+
| id (UUID, PK)         |           | id (UUID, PK)           |
| institution_id (UUID) |           | institution_id (UUID)   |
| class_id (UUID, FK)   |           | class_id (UUID, FK)     |
| student_id (UUID, FK) |           | teacher_id (UUID, FK)   |
| roll_no (VARCHAR)     |           | session_date (DATE)     |
| enrolled_at (TS)      |           | session_time (VARCHAR)  |
+-----------------------+           | is_locked (BOOLEAN)     |
                                    | locked_at (TIMESTAMP)   |
                                    +-------------------------+
                                                 | 1
                                                 |
                                                 | N
                                    +-------------------------+
                                    |   attendance_records    |
                                    +-------------------------+
                                    | id (UUID, PK)           |
                                    | institution_id (UUID)   |
                                    | session_id (UUID, FK)   |
                                    | student_id (UUID, FK)   |
                                    | status (ENUM: P, A, L)  |
                                    | check_in_time (VARCHAR) |
                                    | marked_at (TIMESTAMP)   |
                                    +-------------------------+
                                                 | 1
                                                 | N
                                    +-------------------------+
                                    |   attendance_audits     |
                                    +-------------------------+
                                    | id (UUID, PK)           |
                                    | record_id (UUID, FK)    |
                                    | modified_by (UUID)      |
                                    | old_status (VARCHAR)    |
                                    | new_status (VARCHAR)    |
                                    | change_reason (TEXT)    |
                                    | modified_at (TIMESTAMP) |
                                    +-------------------------+
```

---

## 6. Important Business Logic

### Attendance Percentage Calculation
- **Subject Attendance Percentage**:
  $$\text{Percentage} = \left( \frac{\text{Attended Sessions}}{\text{Total Conducted Sessions}} \right) \times 100$$
- **Lateness Policy**: A `LATE` record is recorded as present with an audit check-in timestamp (e.g., "6:35 PM"), counting towards student attendance for accreditation while maintaining disciplinary tracking.
- **Overall Student Attendance**: Weighted average of all enrolled subject attendances.

### Defaulter Criteria
- A student is categorized as an **Active Defaulter** when:
  $$\text{Overall Percentage} < \text{Alert Threshold (Default: 75\%)}$$
- Color coding in UI:
  - $\ge 75\%$: Normal status (Emerald / `PresentGreenSolid`).
  - $< 75\%$: Critical warning status (Coral / `AbsentCoralSolid`).

### 48-Hour Faculty Edit Window
1. When faculty marks a session, `attendance_sessions.is_locked` remains `false` for 48 hours.
2. During this window, faculty can toggle student status without administrator elevation.
3. At $T + 48\text{h}$, background cron sets `is_locked = true`.
4. Any change after lock requires `ADMIN` role and generates an `attendance_audits` entry.

### Single-Tap Status Toggle Sequence
Tapping a student's status pill rotates cyclically:
$$\text{PRESENT} \longrightarrow \text{ABSENT} \longrightarrow \text{LATE} \longrightarrow \text{PRESENT}$$

---

## 7. Known Issues & Operational Considerations

1. **In-Memory Mock Persistence**:  
   - Currently, modifications to students, classes, or attendance sessions persist only for the active lifecycle of the Android process.
   - Restarting the app resets state back to `MockDataProvider` defaults.
   - *Fix*: Implementation of Android Room DB local storage in Phase 2.
2. **Keyboard Pan on Class Creation Screen**:  
   - On compact Android devices with small vertical viewports, the software keyboard may obscure the bottom "Create Class" button when typing into room location.
   - *Mitigation*: The form is wrapped in `verticalScroll(scrollState)`, allowing users to pan manually.
3. **Chart Tooltip Precision on Touch**:  
   - Canvas-based spline charts (`AttendanceTrendsChart`) currently render static month nodes without interactive finger-drag touch scrubbing.
   - *Roadmap*: Enhance Canvas touch gestures in Phase 3.

---

## 8. Future Roadmap

```
+--------------------------------------------------------------------------+
| PHASE 1: MVP & UI Architecture (COMPLETED - v2.0.0)                      |
| * Responsive Material 3 dual-layout (sidebar + compact drawer/bottom nav)|
| * Role simulation (Admin, Teacher, Student)                              |
| * Fast classroom marking, bulk toggle, trends & bar charts               |
| * Full class creation suite, defaulter reporting & alert simulation      |
+--------------------------------------------------------------------------+
                                    |
                                    v
+--------------------------------------------------------------------------+
| PHASE 2: Offline Persistence & Room DB (COMPLETED - v2.1.0)              |
| * Local Room DB with SQLite for offline-first zero-latency resilience     |
| * DAOs, TypeConverters, Repository pattern & SyncQueueEntity             |
| * Automated sync manager flushing local operations when network recovers  |
+--------------------------------------------------------------------------+
                                    |
                                    v
+--------------------------------------------------------------------------+
| PHASE 3: SaaS Backend API & PostgreSQL RLS (COMPLETED - v2.2.0)          |
| * Python Flask containerized backend with Gunicorn & Dockerfile          |
| * Multi-tenant PostgreSQL schema with Row-Level Security (RLS) policies  |
| * Firebase JWT Auth middleware & role-based access control (RBAC)        |
| * Android Retrofit 2 & OkHttp 3 live cloud networking layer              |
+--------------------------------------------------------------------------+
                                    |
                                    v
+--------------------------------------------------------------------------+
| PHASE 4: Defaulter Automation & PDF Reports (COMPLETED - v2.3.0)         |
| * Asynchronous Celery + Redis background task worker queue               |
| * Automated Jinja2 email alert templates for low-attendance warnings     |
| * Official ReportLab attendance transcript PDF generator endpoint        |
+--------------------------------------------------------------------------+
                                    |
                                    v
+--------------------------------------------------------------------------+
| PHASE 5: Smart Attendance & Geofencing (COMPLETED - v3.0.0)              |
| * 10-second rolling TOTP QR code generator signed with HMAC-SHA256       |
| * 50-meter Haversine GPS classroom geofence enforcement                  |
| * Anti-proxy hardware device fingerprinting binding                      |
| * Compose Canvas 2D QR visualizer, Faculty modal & Student Viewfinder    |
+--------------------------------------------------------------------------+
                                    |
                                    v
+--------------------------------------------------------------------------+
| PHASE 6: Gemini AI & Multi-Campus Federation (COMPLETED - v3.1.0)        |
| * Server-side Gemini 1.5 Flash pipeline with strict PII pseudonymization |
| * Predictive Dropout Risk Model with 4 risk tiers and completion forecast|
| * Automated Academic Intervention Generator & counseling letter contracts|
| * Enterprise Multi-Campus Federation analytics (System -> Campuses)      |
| * Interactive AiRiskDialog, AiInterventionDialog & Multi-Campus Cards    |
+--------------------------------------------------------------------------+
```

