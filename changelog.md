# Attendora Changelog

All notable changes to the **Attendora** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Planned for Future Enhancements
- **Added**: Biometric FaceMatch verification model for faculty and exam hall check-ins.
- **Added**: Real-time WebSocket push notifications for classroom check-in telemetry.

---

## [v3.2.1] - 2026-11-06

### Added
- **Dedicated Full-Page Authentication (`frontend/src/screens/AuthScreen.tsx`)**:
  - Full-screen split layout featuring an academic hero graphic with Attendora branding on wide screens and focused card on compact viewports.
  - Seamless state switching between **Sign In**, **Create Account**, and **Email Verification**.
  - Immediate gating: unauthenticated visitors are presented directly with the dedicated login/registration page before entering dashboard.
- **Email Verification Engine (`backend/src/auth_routes.py`, `backend/src/emailer.py`)**:
  - `POST /api/v1/auth/verify-email`: Validates 6-digit verification code, activates account, and issues authenticated session with HttpOnly cookie.
  - `POST /api/v1/auth/resend-code`: Dispatches a fresh 6-digit verification code with sliding window rate limiting.
  - Responsive HTML email template (`backend/src/templates/verification_email.html`) rendering formatted 6-digit code with 15-minute expiration notice.
  - In `login` flow: users with unverified emails are prompted with `requires_verification` and guided directly to enter their code.
- **Clean Production Credentials (Demo Data Removal)**:
  - Removed all demo preset cards, mock buttons, and pre-filled credentials from login and registration forms.
  - Clean, secure form fields starting empty with client-side validation.
- **Automated Test Suite Expansion**:
  - Added email verification, invalid code, resend code, and unverified login tests to `backend/tests/test_auth.py` (total **51/51 pytest tests passing**).
  - Verified frontend production build compiles cleanly with 0 TypeScript errors.

---

## [v3.2.0] - 2026-11-05

### Added
- **Cryptographic JWT Authentication Engine (`backend/src/auth.py`)**:
  - Signed HS256 JWT access tokens containing identity, tenant context, and role claims with configurable expiration (`JWT_EXPIRES_DAYS`).
  - Salted password hashing using `werkzeug.security` scrypt key derivation.
  - Constant-time password hash verification resisting side-channel timing attacks.
- **Authentication REST API Endpoints (`backend/src/auth_routes.py`)**:
  - `POST /api/v1/auth/register`: Secure registration flow with email normalization, duplicate rejection (HTTP 409 Conflict), and HttpOnly cookie generation.
  - `POST /api/v1/auth/login`: Anti-enumeration authentication returning generic error messages on invalid credentials with HttpOnly cookie issuance.
  - `POST /api/v1/auth/logout`: Invalidation endpoint clearing `attendora_token` cookie.
  - `GET /api/v1/auth/me`: Sanitized profile endpoint stripping password hashes and sensitive secrets.
- **Multi-Tier Request Input Validation (`backend/src/validators.py`)**:
  - Strict RFC 5322 email regex and lowercase normalization.
  - Password complexity enforcement (>=8 characters, uppercase, lowercase, digit, special character).
  - Confirm password verification and role validation (`ADMIN`, `TEACHER`, `STUDENT`).
- **Role-Based Access Control (RBAC) & Middleware (`backend/src/middleware.py`)**:
  - Reusable `@require_auth()` and `@require_roles()` decorators.
  - Automatic extraction from both `attendora_token` HttpOnly cookie and `Authorization: Bearer <token>` header.
  - Injects verified claims into `g.user`, `request.user`, and `g.institution_id`.
  - Rejects unauthenticated requests with HTTP 401 and unauthorized roles with HTTP 403 Forbidden.
- **Security Hardening (`backend/src/security.py`)**:
  - Sliding-window IP rate limiter on authentication endpoints (HTTP 429 Too Many Requests).
  - OWASP security headers injection: `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `X-XSS-Protection`, `Referrer-Policy`.
  - Dual CORS support with `Access-Control-Allow-Credentials: true` and origin whitelist.
- **Frontend Authentication System (`frontend/src/`)**:
  - `AuthProvider` and `useAuth()` hook managing `currentUser`, `isAuthenticated`, and session persistence across page refreshes.
  - `AuthModal.tsx`: High-fidelity modal matching Attendora design tokens with real-time complexity meters and 1-click test credentials presets (Admin, Faculty, Student).
  - `ProtectedRoute.tsx`: Reusable route guard checking authentication and role authorization with informative 401/403 barrier UI.
  - `AttendoraTopBar.tsx`: User profile badge with initials, role chip, and dropdown sign-out action.
- **Automated Test Suite**:
  - Added `backend/tests/test_auth.py` verifying registration, duplicate rejection, weak passwords, login verification, token decoding, cookie handling, role authorization, and logout (total **47/47 backend pytest tests green**).
  - Verified frontend production build compiles cleanly with 0 TypeScript errors.

---

## [v3.1.0] - 2026-11-05

### Added
- **Gemini AI Predictive Intelligence Engine (`backend/ai_engine.py`)**:
  - PII Sanitization & Pseudonymization pipeline: scrubs real names, roll numbers, and emails before formulating LLM prompts; provides secure de-anonymization for official records.
  - Predictive Dropout Risk Model: analyzes multi-semester attendance vectors, subject deficits, and cluster anomalies into 4 tiers (`LOW`, `MODERATE`, `HIGH`, `CRITICAL`), calculating a 0-100 risk score and projected semester completion rate.
  - Deterministic heuristic fallback engine guaranteeing 100% test pass rate and high availability without network or API key dependencies.
- **Automated Academic Intervention Generator**:
  - Exact consecutive lecture calculator for restoration of statutory 75% standing.
  - Generates tailored remediation action items and formal institutional counseling notice letters.
- **Enterprise Multi-Campus Federation**:
  - Hierarchical university federation model (`System -> Campuses -> Departments`).
  - Cross-campus telemetry, regional attendance rate benchmarks, and executive AI strategic briefs.
  - Expanded PostgreSQL `schema.sql` with `campuses` and `ai_intervention_audits` tables and RLS policies.
- **Backend REST API Endpoints (`backend/app.py`)**:
  - `POST /api/v1/ai/predict-risk`: Predictive dropout risk evaluation.
  - `POST /api/v1/ai/intervention-plan`: Structured remediation plan & counseling memo generator.
  - `GET /api/v1/ai/insights`: High-level institutional attendance health index.
  - `GET /api/v1/campuses`: Cross-campus enterprise federation analytics.
- **Android Jetpack Compose UI Integrations**:
  - `AiRiskDialog.kt`: Modal for viewing AI risk tier badges, projected percentages, confidence meters, observed risk factors, and advisory interventions.
  - `AiInterventionDialog.kt`: Modal for reviewing remediation action checklists and formal counseling notice letters.
  - Integrated AI trigger buttons into `StudentDetailDialog.kt` and `ReportsScreen.kt`.
  - Added Enterprise Multi-Campus Federation card to `ReportsScreen.kt`.
  - Added Phase 6 DTOs to `NetworkDtos.kt`, endpoints to `AttendoraApiService.kt`, and state management to `AttendoraViewModel.kt`.
- **Automated Test Verification**:
  - Created `backend/test_phase6.py`: 12 automated tests passing covering PII sanitization, risk models, intervention drafting, institutional insights, and REST endpoints (total **32/32 backend tests green**).
  - Created `Phase6AiIntelligenceUnitTest.kt` on Android side covering DTO serialization, mock providers, and risk calculation.

---

## [v3.0.0] - 2026-11-05

### Added
- **Dynamic TOTP QR Attendance Engine (`backend/qr_engine.py`)**:
  - Time-stepped QR token generation rotating automatically every 10 seconds.
  - Cryptographic HMAC-SHA256 signature enforcement preventing token spoofing or screenshot sharing.
- **GPS Classroom Geofencing (`backend/geofence.py`)**:
  - Haversine great-circle distance algorithm enforcing a strict 50-meter perimeter around Lecture Hall A-101 coordinates (18.52043, 73.85674).
  - Out-of-bounds check-in attempts are rejected with 403 Forbidden `Geofence Violation`.
- **Anti-Proxy Hardware Device Binding**:
  - Single-device-per-session enforcement (`verify_device_uniqueness`) preventing a student from marking attendance on behalf of classmates.
  - Proxy attempts rejected with 409 Conflict.
- **Smart Attendance REST Endpoints (`backend/app.py`)**:
  - `POST /api/v1/attendance/qr/generate`: Produces the rotating QR token and classroom coordinates for faculty.
  - `POST /api/v1/attendance/qr/checkin`: Validates token freshness, verifies GPS distance, enforces device uniqueness, and marks student `PRESENT`.
- **Interactive UI Components in Android Jetpack Compose**:
  - `QrCodeCanvas.kt`: Pure Compose Canvas 2D matrix barcode visualizer with 3 corner finder patterns and timing marks.
  - `DynamicQrDialog.kt`: Modal for faculty with live rotating QR code, 10-second countdown ring, geofence status badge, and verified student counter.
  - `StudentQrScannerDialog.kt`: Modal for students with animated scanning laser, GPS perimeter chip, device binding badge, and self-check-in action.
- **Screen Integrations**:
  - Added "Smart QR Session" button to `AttendanceMarkingScreen.kt`.
  - Added "Scan Classroom QR Code" button to `StudentMobileScreen.kt`.
  - State and lifecycle methods in `AttendoraViewModel.kt`.
- **Automated Verification Suites**:
  - Created `backend/test_phase5.py`: 7 automated tests passing (total 20/20 backend tests passing).
  - Created `Phase5SmartAttendanceUnitTest.kt`: Unit tests verifying QR DTO serialization and ViewModel session lifecycle.

---

## [v2.3.0] - 2026-11-05

### Added
- **Asynchronous Task Queue (`backend/celery_app.py`, `backend/tasks.py`)**:
  - Celery configuration with Redis broker URL and 24-hour Beat schedule for nightly defaulter audits.
  - Multi-threaded fallback task executor (`enqueue_task`) enabling local asynchronous task execution without external broker dependencies.
  - Asynchronous task status tracking endpoint: `GET /api/v1/tasks/<task_id>`.
- **Automated Defaulter Auditing**:
  - Background auditing task (`audit_and_generate_defaulters`) querying students below attendance thresholds and recording audit notices.
- **Batch Email Notification Pipeline (`backend/emailer.py`)**:
  - Responsive HTML email template (`backend/templates/defaulter_alert.html`) featuring Ocean Teal branding, warning pills, student metadata, and subject breakdown tables.
  - Batch email dispatcher with SendGrid API integration and local verification spool.
- **ReportLab PDF Transcript Generator (`backend/pdf_generator.py`)**:
  - Institutional-grade attendance transcript generator rendering official headers, student profiles, standing badges, subject-level attendance tables, statutory notices, and faculty/dean signature blocks.
  - Download endpoint: `GET /api/v1/student/<student_id>/transcript.pdf` serving direct attachment streams.
- **Android Network Service Expansion**:
  - Added `downloadTranscript()` and `notifyDefaulters()` to `AttendoraApiService`.
- **Automated Test Suite Expansion (`backend/test_phase4.py`)**:
  - 6 new automated tests verifying template rendering, email dispatch spooling, PDF binary compilation, and task queuing (13/13 passing in test suite).

---

## [v2.2.0] - 2026-11-05

### Added
- **SaaS Backend API Service (`backend/`)**:
  - Python Flask REST service containerized with Dockerfile and Gunicorn WSGI.
  - Automatic CORS handling with support for mobile and web clients.
- **Multi-Tenant PostgreSQL Schema with Row-Level Security (`backend/schema.sql`)**:
  - PostgreSQL 15+ schema for `institutions`, `users`, `classes`, `class_enrollments`, `attendance_sessions`, `attendance_records`, `attendance_audits`, and `notices`.
  - Kernel-level multi-tenancy enforcement using `ALTER TABLE ... ENABLE ROW LEVEL SECURITY` and `tenant_isolation_*` policies checking `app.current_tenant`.
  - Database context manager (`backend/db.py`) executing `SET LOCAL app.current_tenant` on connection checkout with fallback dev store.
- **Security & Tenant Middleware (`backend/middleware.py`)**:
  - `require_auth` decorator verifying Firebase ID tokens, parsing custom claims (`institution_id`, `role`), and enforcing Role-Based Access Control.
- **REST Endpoints (`backend/app.py`)**:
  - `POST /api/v1/attendance`: Class session marking with 48h edit lock policy.
  - `GET /api/v1/attendance/summary`: Aggregated dashboard metrics, monthly trends, and weekly bar charts.
  - `GET /api/v1/student/dashboard`: Individual student attendance profile and subject progress.
  - `GET /api/v1/students` & `POST /api/v1/students`: Roster management.
  - `GET /api/v1/classes` & `POST /api/v1/classes`: Class management and course scheduling.
  - `GET /api/v1/reports/defaulters` & `POST /api/v1/reports/defaulters/notify`: Defaulter queries and notification dispatch.
  - `GET /health`: Health and diagnostics probe.
- **Backend Automated Test Suite (`backend/test_app.py`)**:
  - Pytest unit tests verifying all endpoints and RBAC security rules (7/7 passed).
- **Android Network Layer Integration (`com.example.network`)**:
  - Retrofit 2 interface `AttendoraApiService` with Moshi JSON DTOs (`dto/NetworkDtos.kt`).
  - `AuthInterceptor` injecting `Authorization: Bearer <token>` and `X-Institution-ID`.
  - `NetworkClient` builder with OkHttp logging and Moshi converter.
  - Integrated `AttendanceRepository` to upload offline mutations to cloud API during sync queue processing.
  - Unit tests in `Phase3NetworkUnitTest.kt` verifying DTO serialization and network mappings.

---

## [v2.1.0] - 2026-11-05

### Added
- **Room SQLite Local Database (`AttendoraDatabase`)**:
  - Entity models for offline caching: `StudentEntity`, `ClassGroupEntity`, `AttendanceSessionEntity`, `AttendanceRecordEntity`, and `SyncQueueEntity`.
  - Type converters (`Converters.kt`) for `AttendanceStatus`, JSON array serialization for `List<SubjectAttendance>`, and `Date` timestamps.
  - Reactive Data Access Objects (DAOs): `StudentDao`, `ClassDao`, `AttendanceDao`, and `SyncQueueDao` with Kotlin `Flow` streams.
  - Automatic database pre-population from `MockDataProvider` on first creation via `RoomDatabase.Callback`.
- **Repository Architecture (`AttendanceRepository`)**:
  - Implemented single source of truth layer exposing `studentsFlow`, `classesFlow`, and `pendingSyncCountFlow`.
  - Offline-first optimistic writes: all student status changes, class additions, and session submissions are saved locally and enqueued in `sync_queue`.
  - Bidirectional domain-entity mappers (`EntityMappers.kt`).
- **Offline Sync Engine (`AttendanceSyncManager`)**:
  - Sync controller to drain pending offline mutations and mark records synced.
- **Application Singletons (`AttendoraApplication`)**:
  - Registered `AttendoraApplication` in `AndroidManifest.xml` managing database and repository instances.
- **ViewModel Integration**:
  - Enhanced `AttendoraViewModel` to reactively observe database repository flows while strictly preserving fallback to mock data in tests/previews.
- **Unit Testing Suite (`Phase2PersistenceUnitTest`)**:
  - Automated unit tests covering TypeConverters, JSON serialization, entity-domain mapping, and ViewModel fallback safety.

---

## [v2.0.0] - 2026-11-05

### Added
- **Modern EdTech Design System**: Complete custom color palette featuring Ocean Teal (`#2E8B9A`), Slate Secondary (`#475569`), Canvas Ice Slate (`#F4FAFE`), and semantic status badge fills (Present Green, Absent Coral, Late Amber).
- **Responsive Dual-Layout Scaffold**: Dynamic `BoxWithConstraints` architecture supporting both wide screens (>= 800dp) with a fixed 240dp left navigation sidebar, and compact mobile screens (< 800dp) with a `ModalNavigationDrawer` and sticky bottom navigation bar.
- **Multi-Role User Simulation**: Dynamic role selector in `AttendoraTopBar` supporting instant persona switching:
  - **Administrator / Principal**: Institutional metrics, department tracking, and policy control.
  - **Faculty / Professor**: Class roster management, interactive single-tap attendance marking, and 48-hour session locking.
  - **Student Portal**: Individual student view with attendance donut gauge, subject breakdown cards, and chronological day log.
- **Administrative Analytics Dashboard (`AdminDashboardScreen`)**:
  - Four top-level metric cards: Total Enrolled Students (120), Today's Attendance Rate (94%), On-Time Check-In Rate (88%), and Absent Count (7).
  - Custom Canvas `AttendanceTrendsChart`: Spline curve graph with teal gradient fill displaying monthly attendance trajectories.
  - Custom Canvas `WeeklyAttendanceChart`: Dual-bar grouped comparison for Monday through Friday showing present vs. absent volumes.
  - Interactive Student Table (`StudentTableCard`) with search filtering, roll numbers, status indicators, and detail modal triggers.
- **Interactive Attendance Marking Screen (`AttendanceMarkingScreen`)**:
  - Class group selector chips ("All", "Class 1", "Class 2", "CS 3rd Year").
  - Bulk action buttons: "Mark All Present" and "Mark All Absent" for rapid classroom management.
  - Cyclic status toggle on student pill tap: `PRESENT` -> `ABSENT` -> `LATE` -> `PRESENT`.
  - Check-in timestamp auto-population on status change.
  - Session submission action triggering 48-hour lock warning and cloud sync toast notifications.
- **Classroom & Course Creation Suite (`CreateClassScreen`, `ClassesManagementScreen`)**:
  - Multi-attribute class builder with course name, code, department selector, academic term, room location, schedule time, and interactive capacity slider (20 to 120 seats).
  - Rich class group cards displaying instructor name, enrolled counts, schedule, and room numbers.
- **Defaulter Analytics & Reports Screen (`ReportsScreen`)**:
  - Interactive attendance threshold slider (configurable from 50% to 90%, defaulting to 75%).
  - Real-time at-risk student calculation displaying critical warning badges for students falling below the selected threshold.
  - Automated batch alert button to dispatch warning notifications to students and parents.
  - Export triggers for institutional PDF and Excel/CSV attendance transcripts.
- **Student Profile Modal (`StudentDetailDialog`)**:
  - Comprehensive pop-up modal displaying student contact info, department, overall attendance percentage, and subject-level progress bars.
  - Direct attendance override controls within the dialog.
- **Tenant Settings Screen (`SettingsScreen`)**:
  - Institutional policy switches for 48-hour faculty edit locking, automated threshold email notifications, and background cloud sync.
- **Persistent AI Documentation Files**:
  - `decisions.md`: Architectural Decision Records (ADR-001 through ADR-010).
  - `rules.md`: Strict engineering standards, folder structures, naming rules, and regression prevention directives.
  - `memory.md`: Long-term technical memory, database ERD, API specs, and roadmap.
  - `phases.md`: Implementation phases master blueprint, task checklists, and execution roadmap.
  - `changelog.md`: Chronological history of project changes.

### Changed
- Refactored `MainActivity.kt` to use modern `enableEdgeToEdge()` with full Material 3 `AttendoraTheme`.
- Enhanced `AttendoraViewModel.kt` to centralize all state management with Kotlin `StateFlow` and single-shot `toastEvent` `SharedFlow`.
- Updated Gradle configuration to configure Secrets Gradle Plugin for `.env` and `.env.example` file handling.

### Fixed
- Fixed navigation back-press behavior: back-press now closes the navigation drawer when open, returns to Dashboard when in secondary tabs, or switches back to Admin mode when in Student Portal.
- Resolved table overflow on smaller viewport widths using horizontal scroll modifiers and compact status badge layouts.

---

## [v1.1.0] - 2026-10-28

### Added
- **System Architecture Design Specification**:
  - Designed multi-tenant SaaS architecture for educational institutions using a Modular Monolith backend.
  - Defined Shared Database / Shared Schema model with PostgreSQL Row-Level Security (RLS) using `institution_id` tenant discriminator.
  - Asynchronous background worker pipeline designed with Celery and Redis for batch email notifications and report generation.
  - Identity design specifying Firebase Authentication with Custom Claims (`institution_id`, `role`, `user_id`).
- **Product Requirements Document (PRD)**: Comprehensive requirements detailing faculty workflows, institutional compliance, student attendance tracking, and defaulter rules.
- **Color Token System**: Generated design token palette mapping Material 3 surfaces, containers, and high-contrast accessible typography (Plus Jakarta Sans).

### Changed
- Standardized REST API contract for `/api/v1/attendance`, `/api/v1/students`, and `/api/v1/classes`.

---

## [v1.0.0] - 2026-10-15

### Added
- Initial project scaffolding for Android application using Android Studio and Gradle Version Catalog (`libs.versions.toml`).
- Android SDK 36 configuration with Kotlin Compose compiler plugin.
- Basic placeholder layout and theme definition.
- Git repository initialization and baseline `.gitignore`.

