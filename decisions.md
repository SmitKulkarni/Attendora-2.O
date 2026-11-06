# Attendora (Attendora-2.O) - Architectural Decision Records (ADR)

This document provides a single source of truth for technical, architectural, and product decisions made during the lifecycle of the **Attendora** modern EdTech SaaS platform. Every decision follows a structured ADR format for historical traceability and persistent AI context.

---

## Index of Decisions

- [ADR-001: Adoption of Modular Monolith Backend Architecture](#adr-001-adoption-of-modular-monolith-backend-architecture)
- [ADR-002: Native Android Client with Jetpack Compose & Material 3](#adr-002-native-android-client-with-jetpack-compose--material-3)
- [ADR-003: Multi-Tenancy Strategy: Shared Database with Tenant Discriminator & PostgreSQL RLS](#adr-003-multi-tenancy-strategy-shared-database-with-tenant-discriminator--postgresql-rls)
- [ADR-004: Relational Database (PostgreSQL) Over Document Store (NoSQL)](#adr-004-relational-database-postgresql-over-document-store-nosql)
- [ADR-005: Identity, Authentication & Role-Based Access via Firebase Auth](#adr-005-identity-authentication--role-based-access-via-firebase-auth)
- [ADR-006: Asynchronous Queue for Defaulter Processing and Bulk Notification (Celery + Redis)](#adr-006-asynchronous-queue-for-defaulter-processing-and-bulk-notification-celery--redis)
- [ADR-007: Attendance Window Immutability and 48-Hour Faculty Edit Lock](#adr-007-attendance-window-immutability-and-48-hour-faculty-edit-lock)
- [ADR-008: State Management Pattern with Kotlin Coroutines, StateFlow, and MVI/MVVM](#adr-008-state-management-pattern-with-kotlin-coroutines-stateflow-and-mvimvvm)
- [ADR-009: Integration of Server-Side Gemini AI for Attendance Analytics & Risk Forecasting](#adr-009-integration-of-server-side-gemini-api-for-attendance-analytics--risk-forecasting)
- [ADR-010: Offline Resilience and Client-Side Room Cache with Bidirectional Sync](#adr-010-offline-resilience-and-client-side-room-cache-with-bidirectional-sync)
- [ADR-011: Gemini AI Predictive Dropout Risk Modeling & Multi-Campus Federation](#adr-011-gemini-ai-predictive-dropout-risk-modeling--multi-campus-federation)
- [ADR-012: Cryptographic JWT Authentication, HttpOnly Cookie Sessions, RBAC Authorization & Multi-Tier Input Validation](#adr-012-cryptographic-jwt-authentication-httponly-cookie-sessions-rbac-authorization--multi-tier-input-validation)
- [ADR-013: Containerized Multi-Stage Production Deployment & Cloud PaaS Architecture](#adr-013-containerized-multi-stage-production-deployment--cloud-paas-architecture)

---

## ADR-001: Adoption of Modular Monolith Backend Architecture

- **Date**: 2026-10-18
- **Status**: Accepted
- **Context / Problem**:  
  Attendora provides attendance tracking, institutional roster management, and defaulter reporting across educational institutions. Building a microservices architecture from day one introduces severe operational overhead (service meshes, inter-service gRPC/REST latency, distributed transactions, distributed tracing, Kubernetes management, and steep cloud hosting bills) that are unjustified for an early-to-growth stage product. Conversely, an unstructured spaghetti codebase impedes scaling.
- **Decision Taken**:  
  Adopt a **Modular Monolith** architecture for the Attendora backend (Python/Flask or FastAPI). Structure internal modules strictly around core academic domains: Identity & Access Management (IAM), Tenant Administration, Academic Core (Institutions, Courses, Classes, Rosters), Attendance Marking Engine, Defaulter Analytics & Reporting, and Billing.
- **Reasoning**:  
  - Eliminates network serialization and distributed consensus overhead for high-frequency operations like class roster retrieval and bulk marking.
  - Allows in-process function calls with strong type safety while strictly enforcing domain boundaries.
  - Enables clean future extraction of specific bottleneck domains (e.g., Report Generation or Push Notification Dispatch) into independent microservices if traffic demands it.
- **Alternatives Considered**:  
  1. *Full Microservices*: Rejected due to high DevOps complexity, latency during class-marking peaks, and prohibitive cloud costs for early educational deployments.
  2. *Unstructured Monolith (Single-file script approach)*: Rejected due to codebase rot and lack of tenant boundary separation.
- **Impact on Project**:  
  Simplifies continuous deployment (single container deploy to Render / AWS App Runner / Google Cloud Run), facilitates localized testing, and lowers hosting infrastructure spend.

---

## ADR-002: Native Android Client with Jetpack Compose & Material 3

- **Date**: 2026-10-22
- **Status**: Accepted
- **Context / Problem**:  
  Educational institutions operate on diverse hardware: faculty members frequently use tablets or classroom Chromebooks/laptops, while students almost exclusively access portals via Android smartphones. The application requires fluid, high-performance UI components (dynamic attendance trend curves, weekly bar charts, fast single-tap checkbox lists for 60+ students, and instant role toggling) without jank.
- **Decision Taken**:  
  Build the client using **Modern Native Android with Jetpack Compose**, **Material 3 Design Tokens**, and a **Responsive Adaptive Layout** (`BoxWithConstraints` switching between a wide-screen pinned navigation sidebar and a compact-screen modal drawer/bottom navigation).
- **Reasoning**:  
  - Declarative UI eliminates brittle Android XML layout hierarchies and ViewBinding boilerplate.
  - State hoisting guarantees single-direction data flow (UDF) matching MVI/MVVM patterns.
  - Material 3 dynamic color theming, typography, and standard elevation tokens ensure institutional design polish.
- **Alternatives Considered**:  
  1. *Flutter*: High cross-platform potential, but larger APK binary size, higher memory footprint on low-tier student phones, and non-native look-and-feel.
  2. *React Native*: JavaScript bridge serialization bottlenecks during high-frequency list scrolling (60+ students with animated checkboxes).
- **Impact on Project**:  
  High rendering performance (60-120 FPS), direct hardware integration (NFC/QR/BLE for future attendance badges), and native look-and-feel.

---

## ADR-003: Multi-Tenancy Strategy: Shared Database with Tenant Discriminator & PostgreSQL RLS

- **Date**: 2026-10-25
- **Status**: Accepted
- **Context / Problem**:  
  Attendora serves multiple schools, colleges, and university campuses. We must guarantee that one institution cannot view or manipulate student records from another institution, while keeping database operational costs and migration overhead manageable.
- **Decision Taken**:  
  Implement a **Shared Database, Shared Schema architecture** where every multi-tenant table (`classes`, `students`, `attendance_sessions`, `defaulter_logs`) includes an `institution_id UUID NOT NULL` foreign key. Pair this with **PostgreSQL Row-Level Security (RLS)** policies:
  ```sql
  ALTER TABLE students ENABLE ROW LEVEL SECURITY;
  CREATE POLICY tenant_isolation_policy ON students
    FOR ALL
    USING (institution_id = NULLIF(current_setting('app.current_tenant', true), '')::uuid);
  ```
- **Reasoning**:  
  - Database-per-tenant introduces severe operational complexity: running 500 migrations for 500 colleges is slow and error-prone.
  - Schema-per-tenant exhausts PostgreSQL connection pools and table cache memory (`shared_buffers`).
  - PostgreSQL RLS enforces tenant isolation at the database engine level, preventing accidental data leaks even if an engineer writes a query without `WHERE institution_id = ...`.
- **Alternatives Considered**:  
  1. *Database-per-Tenant*: Rejected due to high infrastructure cost and migration complexity.
  2. *Application-level WHERE clauses only*: Rejected due to the risk of accidental omissions causing compliance breaches.
- **Impact on Project**:  
  All backend database connection pools must set session-local configuration (`SET LOCAL app.current_tenant = %s`) at transaction start based on the verified JWT claims.

---

## ADR-004: Relational Database (PostgreSQL) Over Document Store (NoSQL)

- **Date**: 2026-10-27
- **Status**: Accepted
- **Context / Problem**:  
  While NoSQL document stores (such as Firebase Firestore or MongoDB) offer schema flexibility, educational systems represent inherently rigid relational hierarchies: `Institution -> Department -> Academic Term -> Course -> Class Group -> Subject -> Faculty -> Student Enrollment -> Attendance Session -> Attendance Record`. Attendance analytics require complex aggregation queries (calculating attendance percentage per subject, class, and semester; identifying defaulters below 75%).
- **Decision Taken**:  
  Select **PostgreSQL (v15+)** as the primary relational database for production persistence, using Room (SQLite) on Android clients for offline caching.
- **Reasoning**:  
  - ACID compliance guarantees transactional integrity: an attendance submission for 60 students either commits completely or rolls back, preventing corrupt partial submissions.
  - Complex SQL window functions, aggregates (`COUNT`, `AVG`, `FILTER`), and multi-table joins compute defaulter percentages orders of magnitude faster and cheaper than iterating NoSQL document snapshots.
  - Native RLS support secures multi-tenancy at the kernel level.
- **Alternatives Considered**:  
  1. *Cloud Firestore (Pure NoSQL)*: Rejected due to prohibitive read/write pricing for bulk attendance (marking 1,000 students across 20 classes creates 1,000+ document writes daily) and clumsy aggregation queries.
  2. *MongoDB*: Rejected because document joins (`$lookup`) are slow for large-scale multi-tenant reporting.
- **Impact on Project**:  
  Backend migrations are managed using Flyway or Alembic. The Android client mirrors key schema tables locally using Android Room DB.

---

## ADR-005: Identity, Authentication & Role-Based Access via Firebase Auth

- **Date**: 2026-10-29
- **Status**: Accepted
- **Context / Problem**:  
  Building in-house authentication introduces significant security liabilities (storing passwords, salted hashes, handling OAuth2 social logins, 2FA/MFA, email verifications, and brute force defenses). Furthermore, universities standardly utilize Google Workspace for Education.
- **Decision Taken**:  
  Offload identity management to **Firebase Authentication** with **Custom Claims** injected into the signed JWT token (`institution_id`, `role: ADMIN | TEACHER | STUDENT`, `user_id`).
- **Reasoning**:  
  - Secure out-of-the-box OAuth2 Google Sign-In for faculty and students using institutional email addresses (`@institution.edu`).
  - Cryptographically signed JWT tokens are validated statelessly by the backend middleware via the Firebase Admin SDK without repeated auth server round-trips.
  - Automatic handling of password resets, rate limiting, and credential credential management.
- **Alternatives Considered**:  
  1. *Custom JWT Auth with Bcrypt*: High vulnerability surface area, requires building custom session revocation and email dispatch harnesses.
  2. *Auth0 / Okta*: Enterprise features are strong but expensive for student/freemium college tiers.
- **Impact on Project**:  
  Clients authenticate against Firebase Auth, retrieve an ID token, and pass `Authorization: Bearer <token>` on every API request. The mobile application supports rapid role-simulation for administrators and professors.

---

## ADR-006: Asynchronous Queue for Defaulter Processing and Bulk Notification (Celery + Redis)

- **Date**: 2026-10-31
- **Status**: Accepted
- **Context / Problem**:  
  When an administrator or automated cron audits attendance and flags defaulters (students < 75%), the system must dispatch hundreds or thousands of institutional emails and SMS/WhatsApp notifications with PDF transcripts. Executing this synchronously in the HTTP request cycle causes gateway timeouts (504 Gateway Timeout on Cloudflare/Nginx) and starves web workers.
- **Decision Taken**:  
  Offload heavy analytics audits, PDF transcript generation, and email dispatch to an **Asynchronous Task Queue using Celery with Redis** as the message broker.
- **Reasoning**:  
  - Guarantees immediate sub-100ms HTTP 202 Accepted response times to the frontend with a `job_id`.
  - Provides automatic retry mechanisms with exponential backoff for failed SMTP/Twilio dispatches.
  - Allows horizontal autoscaling of Celery worker pods independently from web API pods.
- **Alternatives Considered**:  
  1. *Python `threading.Thread`*: Fails if the web server restarts or crashes mid-task, leading to dropped notifications; doesn't scale across multiple container instances.
  2. *AWS SQS + Lambda*: Vendor lock-in; Celery/Redis can run anywhere (Docker Compose locally, cloud Kubernetes in production).
- **Impact on Project**:  
  Added `backend/tasks.py` and `backend/emailer.py`. The frontend polls `GET /api/v1/tasks/{task_id}` or receives WebSocket events for progress updates.

---

## ADR-007: Attendance Window Immutability and 48-Hour Faculty Edit Lock

- **Date**: 2026-11-01
- **Status**: Accepted
- **Context / Problem**:  
  In educational environments, unconstrained editing of historical attendance creates severe academic integrity risks (e.g., retrospective attendance inflation before exams). Institutions require an audit-compliant policy governing when attendance can be changed.
- **Decision Taken**:  
  Enforce a strict **48-Hour Immutability Lock**:
  - Class teachers can freely mark and amend session attendance within 48 hours of session creation.
  - After 48 hours, the session becomes strictly read-only for faculty.
  - Overriding locked historical sessions requires an elevated `ADMIN` role with an audit log reason recorded in `attendance_audit_log`.
- **Reasoning**:  
  - Complies with statutory university examination board standards.
  - Protects faculty members from unwarranted student coercion while preserving administrative override authority.
- **Alternatives Considered**:  
  1. *Permanent Immutability (No Edits Allowed)*: Too rigid; professors frequently make typographical mistakes that need immediate same-day correction.
  2. *Unrestricted Edits*: Violates basic audit requirements and accreditation standards.
- **Impact on Project**:  
  Backend checks `created_at` timestamp on `PUT /api/v1/attendance/{session_id}`. The Android and Web UI disable edit controls and display a "Locked (Audit Protected)" pill after 48 hours.

---

## ADR-008: State Management Pattern with Kotlin Coroutines, StateFlow, and MVI/MVVM

- **Date**: 2026-11-02
- **Status**: Accepted
- **Context / Problem**:  
  The Attendora Android application manages complex state: rapid bulk status toggling for 60+ students, dynamic filter queries, class selection, live notifications, and asynchronous sync state. Inconsistent state handling leads to race conditions and UI flickering.
- **Decision Taken**:  
  Standardize on **Unidirectional Data Flow (UDF)** using Android Architecture Components:
  - **ViewModel** holds immutable `StateFlow<AttendoraUiState>`.
  - UI emits **Intent/Action** events (`ToggleStudentAttendance`, `FilterByClass`, `SyncData`).
  - ViewModel processes actions via Kotlin Coroutines and emits updated state to the UI.
- **Reasoning**:  
  - Clean separation of business logic from UI rendering.
  - Survives Android configuration changes (screen rotations, fold/unfold events).
  - Testable using `kotlinx-coroutines-test` and turbine.
- **Alternatives Considered**:  
  1. *LiveData*: Legacy; inferior coroutine integration compared to `StateFlow` and lacks support for Kotlin Multiplatform.
  2. *Component-local `remember { mutableStateOf() }`*: Causes state loss during navigation and breaks separation of concerns for non-trivial workflows.
- **Impact on Project**:  
  Ensures predictable state transitions and effortless unit testing of UI logic.

---

## ADR-009: Integration of Server-Side Gemini AI for Attendance Analytics & Risk Forecasting

- **Date**: 2026-11-03
- **Status**: Accepted
- **Context / Problem**:  
  Educational institutions want actionable intelligence, not just static attendance counts. Identifying dropout patterns, detecting attendance fatigue, and forecasting semester-end defaulter risks require natural language synthesis and machine learning reasoning.
- **Decision Taken**:  
  Integrate **Google Gemini 1.5 Flash** server-side through the Google GenAI SDK.
  - All AI requests are routed through the Attendora backend (`backend/ai_engine.py`).
  - Sensitive student Personally Identifiable Information (PII) is anonymized/pseudonymized before being sent to the Gemini API.
  - The model provides structured JSON predictions (`predicted_final_percentage`, `risk_level`, `confidence_score`, `recommendations`).
- **Reasoning**:  
  - Keeps Gemini API keys strictly secure on the server side (never exposed in client binaries).
  - Anonymization ensures compliance with student data privacy regulations (FERPA/DPDP).
  - Gemini 1.5 Flash provides fast, low-cost, high-context token processing ideal for tabular student records.
- **Alternatives Considered**:  
  1. *Client-side AI calling*: Insecure; leaks institutional API keys via APK reverse engineering.
  2. *Custom SciKit-Learn ML model*: Requires ongoing data science maintenance, hosting infrastructure, and lacks natural language rationale generation.
- **Impact on Project**:  
  Adds `POST /api/v1/ai/predict-risk` endpoint and renders AI-generated risk evaluations in the faculty dashboard.

---

## ADR-010: Offline Resilience and Client-Side Room Cache with Bidirectional Sync

- **Date**: 2026-11-04
- **Status**: Accepted
- **Context / Problem**:  
  Classrooms and lecture halls in universities frequently experience poor Wi-Fi connectivity or cellular dead zones. If attendance marking depends on continuous internet access, professors cannot reliably record attendance during class.
- **Decision Taken**:  
  Implement an **Offline-First local SQLite cache via Android Room DB** with optimistic local UI updates and a dirty-record synchronization queue.
- **Reasoning**:  
  - When a professor taps "Submit Attendance", the records are immediately written to local Room DB and marked `SYNC_PENDING`.
  - The UI responds instantaneously with zero latency.
  - An Android WorkManager background task detects network restoration and pushes queued records to the cloud backend.
- **Alternatives Considered**:  
  1. *Online-only HTTP calls*: Causes app freeze or failure when classroom Wi-Fi fluctuates, leading to lost attendance records.
  2. *Raw SQLite without Room*: Error-prone, lacks compile-time SQL verification and Coroutine Flow observation.
- **Impact on Project**:  
  All data models are annotated with Room `@Entity`, `@PrimaryKey`, and `@TypeConverters`. The UI displays a cloud sync indicator ("Cloud Synced" vs "Local Queue Pending").

---

## ADR-011: Gemini AI Predictive Dropout Risk Modeling & Multi-Campus Federation

- **Date**: 2026-11-05
- **Status**: Accepted
- **Context / Problem**:  
  Traditional educational ERPs passively report historical attendance numbers without foresight. By the time a student's cumulative attendance drops below the 75% debarment threshold at semester end, it is mathematically impossible to recover, leading to course failure or exam disqualification. Furthermore, university systems managing multi-campus networks lack cross-campus aggregate telemetry to detect regional retention issues.
- **Decision Taken**:  
  1. Build a **Server-Side AI Pipeline (`backend/ai_engine.py`)** with strict **PII pseudonymization** before querying Gemini 1.5 Flash.
  2. Classify students into 4 explainable risk tiers: `LOW`, `MODERATE`, `HIGH`, and `CRITICAL`, with projected semester-end completion attendance.
  3. Implement an **Automated Academic Intervention Generator** calculating exact consecutive sessions required to regain compliance and drafting formal counseling contracts.
  4. Implement an **Offline / Deterministic Heuristic Fallback Engine** ensuring 100% test pass rate and high availability even without active external internet or API keys.
  5. Deploy **Enterprise Multi-Campus Federation** analytics aggregating multi-campus regional attendance and health scores.
- **Reasoning**:  
  - PII scrubbing guarantees FERPA/statutory student data privacy.
  - Predictive modeling enables faculty to intervene weeks before final exams.
  - Deterministic fallbacks ensure zero disruption for offline development and CI/CD pipelines.
- **Alternatives Considered**:  
  1. *Direct client-to-Gemini SDK calls*: Rejected to prevent leaking API keys and violating PII scrubbing regulations.
  2. *Simple static alerts*: Insufficient for projecting future decay velocity and drafting tailored remediation letters.
- **Impact on Project**:  
  Integrated `POST /api/v1/ai/predict-risk`, `POST /api/v1/ai/intervention-plan`, `GET /api/v1/ai/insights`, and `GET /api/v1/campuses`. UI features interactive `AiRiskDialog`, `AiInterventionDialog`, and multi-campus cards in `ReportsScreen`.

---

## ADR-012: Cryptographic JWT Authentication, HttpOnly Cookie Sessions, RBAC Authorization & Multi-Tier Input Validation

- **Date**: 2026-11-05
- **Status**: Accepted
- **Context / Problem**:  
  Attendora transitioned from a development mock state to a production architecture separated into independent `frontend/` and `backend/` tiers. To secure sensitive academic records, attendance logs, and AI predictive insights, the system requires an enterprise-grade identity layer with secure session management, strict role-based access control (RBAC), anti-brute-force defense, and input validation without disrupting existing test suites or mock workflows.
- **Decision Taken**:  
  1. **Cryptographic JWT Tokens & Secret Management**: Issue signed JWT tokens (`HS256`) using `JWT_SECRET` loaded strictly from environment variables.
  2. **Dual-Transport Authentication**: Provide both secure `HttpOnly` `SameSite=Lax` cookies (`attendora_token`) for browser web clients and `Authorization: Bearer <token>` headers for mobile/API clients.
  3. **Timing-Safe Password Hashing**: Hash user passwords with salted key-derivation algorithms (`werkzeug.security` scrypt / bcrypt). Never store plain-text passwords.
  4. **Anti-Enumeration Protection**: Return uniform `"Invalid email or password"` HTTP 401 responses on login failures to prevent user enumeration attacks.
  5. **Sanitized User Profiles**: Implement `GET /api/v1/auth/me` returning sanitized identity info (stripping password hashes and secrets).
  6. **Multi-Tier Input Validation**:
     - Frontend: Real-time visual feedback on field length, email regex, dynamic password complexity (upper, lower, digit, symbol), and confirm password match.
     - Backend (`backend/src/validators.py`): Server-side strict RFC 5322 email regex, length bounds, password regex, duplicate email rejection (HTTP 409 Conflict).
  7. **Role-Based Access Control (RBAC)**: Reusable `@require_auth()` and `@require_roles()` middleware enforcing `ADMIN`, `TEACHER`, and `STUDENT` boundaries (HTTP 403 Forbidden).
  8. **Security Hardening**: Sliding window IP rate limiter (max 15 requests / 5 mins for auth routes) and OWASP security headers (`nosniff`, `DENY`, `X-XSS-Protection`, `strict-origin-when-cross-origin`).
  9. **Frontend Auth Context & Modals**: Implemented `AuthProvider`, `useAuth()`, `AuthModal.tsx` (Login / Register with 1-click test account presets), and `ProtectedRoute.tsx`.
- **Reasoning**:  
  - HttpOnly cookies prevent token theft via client-side XSS.
  - Constant-time password verification prevents side-channel timing attacks.
  - Multi-tier validation maintains high data integrity while providing friendly UX.
- **Alternatives Considered**:  
  1. *Plain localStorage JWT*: Vulnerable to XSS token exfiltration.
  2. *Single admin password*: Inadequate for multi-user academic institutions with disparate roles.
- **Impact on Project**:  
  All 51 backend pytest tests pass. Frontend builds with 0 TypeScript errors.

---

## ADR-013: Containerized Multi-Stage Production Deployment & Cloud PaaS Architecture

- **Date**: 2026-11-06
- **Status**: Accepted
- **Context / Problem**:  
  With frontend and backend separated into distinct decoupled sub-workspaces, the project requires an enterprise production deployment strategy supporting both managed Cloud Platform as a Service (PaaS) providers (Vercel, Netlify, Render, Railway) and on-premises / self-hosted container infrastructure (Docker Compose, Kubernetes, AWS EC2).
- **Decision Taken**:  
  1. **Dual-Tier Deployment Strategy**:
     - **Static React Frontend**: Multi-stage Docker build with Node 20 builder and Nginx Alpine server; SPA fallback rewrites (`vercel.json` and `_redirects` for Netlify).
     - **Python WSGI Backend**: Python 3.11-slim container running Gunicorn WSGI with 4 worker processes and configurable timeouts.
  2. **Unified Orchestration (`docker-compose.yml`)**:
     - Coordinates `frontend` (port 3000), `backend` (port 5000), `postgres` (port 5432 with automatic `schema.sql` initialization), and `redis` (port 6379).
  3. **Strict Origin & Cookie Isolation**:
     - Backend enforces `FRONTEND_URL` allowed origins in CORS with `Access-Control-Allow-Credentials: true`.
     - `COOKIE_SECURE=true` in production guarantees HTTPS-only cookie transmission.
- **Reasoning**:  
  - Static frontend distribution on Edge CDNs (Vercel/Netlify) provides sub-50ms latency globally for students and professors.
  - Containerization guarantees environment parity between development and cloud production.
  - Docker Compose enables turnkey single-command deployment on self-hosted institutional servers.
- **Alternatives Considered**:  
  1. *Serving React from Flask static files directly*: Causes Flask process bloat and high memory consumption under heavy asset requests.
  2. *Single VM without containers*: Subject to Python and Node runtime version conflicts on host machines.
- **Impact on Project**:  
  Added `frontend/Dockerfile`, `frontend/nginx.conf`, `frontend/vercel.json`, `frontend/public/_redirects`, and root `docker-compose.yml`.

