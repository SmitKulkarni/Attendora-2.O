# Attendora-2.O

> Modern EdTech Multi-Tenant SaaS Attendance Tracking, Predictive Intelligence & Class Management Platform.

Attendora is an enterprise-grade digital attendance solution designed for universities, colleges, and multi-campus educational institutions. The platform combines a responsive Web application (React + Vite + TypeScript), an offline-first Android client (Jetpack Compose + Room DB), and a containerized Python Flask backend powered by Google Gemini 1.5 Flash, PostgreSQL with Row-Level Security (RLS), and Celery task workers.

---

## Architecture & Directory Structure

The project has been refactored into completely separated frontend and backend workspaces:

```text
Attendora-2.O/
├── frontend/                     # Frontend Application Workspace
│   ├── src/                      # React + TypeScript Source Code
│   │   ├── context/              # Authentication context & session state (AuthContext.tsx)
│   │   ├── components/           # Reusable UI Components (Sidebar, TopBar, AuthModal, ProtectedRoute, Modals)
│   │   ├── screens/              # Core Application Screens (Dashboard, Marking, Roster, Reports, etc.)
│   │   ├── services/             # API client connecting to backend with resilient offline fallback
│   │   ├── types/                # Domain models & TypeScript interfaces
│   │   ├── App.tsx               # Main application shell with responsive sidebar & role switching
│   │   └── main.tsx              # React DOM entry point
│   ├── public/                   # Public assets, design tokens, logos & PRD documents
│   ├── android/                  # Native Android Jetpack Compose project (Room DB, Retrofit)
│   ├── .env                      # Frontend environment variables
│   ├── .env.example              # Example environment variables template
│   ├── package.json              # Frontend dependencies (React, Vite, Lucide)
│   ├── tsconfig.json             # TypeScript compiler configuration
│   └── vite.config.ts            # Vite bundler & reverse proxy configuration
│
├── backend/                      # Backend SaaS API Service
│   ├── src/                      # Python Flask Microservice Source Code
│   │   ├── app.py                # Main Flask application & REST API routes
│   │   ├── auth.py               # Cryptographic JWT signing & salted password hashing
│   │   ├── auth_routes.py        # Authentication blueprint (register, login, logout, me)
│   │   ├── validators.py         # Request validation engine (email RFC, password complexity)
│   │   ├── security.py           # IP rate limiting, OWASP security headers, cookie helpers
│   │   ├── ai_engine.py          # Gemini AI Predictive Dropout Risk & Intervention Engine
│   │   ├── qr_engine.py          # Dynamic 10s rolling TOTP QR generation & anti-proxy binding
│   │   ├── geofence.py           # Haversine 50-meter classroom GPS geofencing
│   │   ├── pdf_generator.py      # ReportLab official PDF attendance transcript generator
│   │   ├── tasks.py              # Celery & thread-pool asynchronous background tasks
│   │   ├── emailer.py            # Responsive HTML email dispatcher for defaulters
│   │   ├── db.py                 # Multi-tenant PostgreSQL connection & memory fallback
│   │   ├── middleware.py         # JWT verification, tenant isolation & RBAC middleware
│   │   ├── celery_app.py         # Celery task queue configuration
│   │   ├── schema.sql            # PostgreSQL RLS database schema & seed data
│   │   └── templates/            # Jinja2 HTML email alert templates
│   ├── tests/                    # Automated pytest test suites (47 tests passing)
│   │   ├── test_auth.py          # Authentication, password hashing, cookies & RBAC tests
│   │   ├── test_app.py           # Core endpoints & tenant auth tests
│   │   ├── test_phase4.py        # Asynchronous worker, emailer & PDF tests
│   │   ├── test_phase5.py        # Dynamic QR & GPS geofencing tests
│   │   └── test_phase6.py        # Gemini AI predictive risk & multi-campus tests
│   ├── .env                      # Backend environment variables
│   ├── .env.example              # Example environment variables template
│   ├── package.json              # Backend npm scripts wrapper (npm start, npm test)
│   ├── requirements.txt          # Python pip dependencies
│   ├── pytest.ini                # Pytest configuration
│   └── Dockerfile                # Production container deployment definition
│
├── changelog.md                  # Chronological release notes (Keep a Changelog)
├── decisions.md                  # Architectural Decision Records (ADR-001 through ADR-011)
├── memory.md                     # Persistent system memory, ERD, and API contracts
├── phases.md                     # Master implementation roadmap (Phases 1-6 completed)
├── rules.md                      # Engineering standards & code quality rules
├── package.json                  # Root workspace orchestration scripts
└── .gitignore                    # Git ignore rules for Node, Python, and Android
```

---

## Prerequisites

Ensure you have the following installed on your machine:
- **Node.js**: v18.0.0 or higher (v24.x tested)
- **npm**: v9.0.0 or higher (v11.x tested)
- **Python**: v3.10 or higher (v3.14 tested)
- **pip**: Python package manager

---

## Quick Start: Running Frontend and Backend

### 1. Installing Dependencies

#### Frontend Dependencies:
```bash
# Navigate to the frontend directory and install npm packages
cd frontend
npm install
cd ..
```

#### Backend Dependencies:
```bash
# Navigate to the backend directory and install Python dependencies
cd backend
pip install -r requirements.txt
cd ..
```

*Alternatively, from the project root you can run:*
```bash
npm run install:all
```

---

### 2. Environment Variables Configuration

Keep environment variables strictly separated between frontend and backend:

#### Frontend (`frontend/.env`):
```env
VITE_API_BASE_URL=http://localhost:5000
VITE_APP_TITLE=Attendora SaaS
```

#### Backend (`backend/.env`):
```env
PORT=5000
FLASK_ENV=development
SECRET_KEY=attendora_super_secret_jwt_hmac_key_2026
DATABASE_URL=
REDIS_URL=redis://localhost:6379/0
GEMINI_API_KEY=
INSTITUTION_ID=00000000-0000-0000-0000-000000000001
```

*(If `GEMINI_API_KEY` is not provided, the backend automatically engages its deterministic heuristic fallback engine with 100% functionality).*

---

### 3. Starting the Services

#### Starting the Backend:
```bash
# Option A: Using npm script inside backend/
cd backend
npm start

# Option B: Direct Python execution
cd backend
python src/app.py
```
The backend API server will start at: **`http://localhost:5000`**
- Health Check: `GET http://localhost:5000/health`
- Campuses Endpoint: `GET http://localhost:5000/api/v1/campuses`

#### Starting the Frontend:
In a new terminal window:
```bash
cd frontend
npm start
# or: npm run dev
```
The frontend web application will start at: **`http://localhost:3000`** (or the port allocated by Vite).

#### Running from Project Root:
From the root workspace, you can trigger:
```bash
# Start backend
npm run start:backend

# Start frontend (in a separate terminal)
npm run start:frontend
```

---

## Running Automated Tests

### Backend Test Suite (Pytest)
The backend includes 51 automated tests covering all phases (Cryptographic Authentication, 6-Digit Email Verification, RBAC Authorization, Request Validation, Defaulter Alerts, Dynamic QR, GPS Geofencing, Gemini AI PII Sanitization, Risk Forecasting, and Multi-Campus Federation):

```bash
# Option A: From root directory
python -m pytest backend/tests

# Option B: Inside backend directory
cd backend
npm test
# or: python -m pytest tests/
```

Expected output:
```text
============================= 51 passed in 4.26s ==============================
```

### Frontend Type Check & Build
```bash
cd frontend
npm run build
```
Generates production-optimized bundle in `frontend/dist/` (0 TypeScript errors).

---

## Production Deployment Guide

Attendora is decoupled into a static React frontend and a Python WSGI backend, making it adaptable to any hosting environment:

### Method 1: Cloud PaaS (Recommended: Render + Vercel / Netlify)

#### Step 1: Deploy Backend on Render (or Railway / AWS App Runner)
1. Link your GitHub repository in the [Render Dashboard](https://dashboard.render.com/).
2. Create a new **Web Service**:
   - **Root Directory**: `backend`
   - **Runtime**: `Python 3`
   - **Build Command**: `pip install -r requirements.txt`
   - **Start Command**: `gunicorn --chdir src app:app --bind 0.0.0.0:$PORT --workers 4 --timeout 60`
3. Configure Backend **Environment Variables**:
   | Variable | Value | Description |
   | :--- | :--- | :--- |
   | `FLASK_ENV` | `production` | Enables production security mode |
   | `JWT_SECRET` | *(generate a strong 64-char key)* | Secret for signing HS256 tokens |
   | `JWT_EXPIRES_DAYS` | `7` | Token validity |
   | `FRONTEND_URL` | `https://your-frontend-domain.vercel.app` | Allowed CORS origin |
   | `COOKIE_SECURE` | `true` | Enforces HTTPS-only cookies |
   | `DATABASE_URL` | `postgresql://...` | Managed PostgreSQL instance URL |
   | `REDIS_URL` | `rediss://...` | Managed Redis instance URL |
   | `SENDGRID_API_KEY` | *(optional SendGrid key)* | For live transactional emails |

#### Step 2: Deploy Frontend on Vercel (or Netlify / Cloudflare Pages)
1. Import your repository in [Vercel](https://vercel.com/) or [Netlify](https://www.netlify.com/).
2. Select the **`frontend`** directory as the root:
   - **Framework Preset**: `Vite`
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
3. Configure Frontend **Environment Variables**:
   | Variable | Value |
   | :--- | :--- |
   | `VITE_API_BASE_URL` | `https://your-backend-service.onrender.com` |
4. Deploy! `vercel.json` and `public/_redirects` are pre-configured to handle SPA client routing (`/* -> index.html`).

---

### Method 2: Docker & Docker Compose (VPS / AWS EC2 / DigitalOcean)

The project includes pre-configured Dockerfiles and a root `docker-compose.yml` for unified single-command deployment:

```bash
# 1. Clone repository on your server
git clone https://github.com/your-username/Attendora-2.O.git
cd Attendora-2.O

# 2. Build and start all 4 services in detached mode
docker compose up --build -d
```

This launches:
- **`frontend`**: React SPA served by optimized Nginx on port `3000` (or `80`).
- **`backend`**: Gunicorn WSGI Python Flask API on port `5000`.
- **`postgres`**: PostgreSQL 15 database initialized with `schema.sql` and RLS policies on port `5432`.
- **`redis`**: Redis 7 cache and Celery broker on port `6379`.

---

## Authentication, Authorization & Security Architecture

Attendora features an enterprise-grade authentication and access control layer:

### Security Pillars
1. **Cryptographic Tokens & Expiration**: Signed HS256 JWT access tokens containing identity, tenant context, and role claims with configurable expiration (`JWT_EXPIRES_DAYS`).
2. **Dual-Transport Sessions**:
   - **Web Browsers**: Secure, HttpOnly, `SameSite=Lax` cookies (`attendora_token`) preventing XSS token theft.
   - **Mobile & API Clients**: Standard `Authorization: Bearer <token>` headers.
3. **Password Security**: Salted key-derivation password hashing (`scrypt`) with constant-time hash comparisons resisting side-channel timing attacks.
4. **Anti-Enumeration Defense**: Authentication failures return uniform `"Invalid email or password"` HTTP 401 responses to prevent account discovery.
5. **Role-Based Access Control (RBAC)**: Reusable `@require_auth()` and `@require_roles()` middleware enforcing `ADMIN`, `TEACHER`, and `STUDENT` permissions (HTTP 403 Forbidden).
6. **Multi-Tier Input Validation**: RFC 5322 regex email validation, password complexity (uppercase, lowercase, number, symbol), and duplicate account rejection (HTTP 409 Conflict).
7. **Rate Limiting & OWASP Headers**: Sliding-window IP rate limiting (HTTP 429) on authentication routes and standard security headers (`nosniff`, `DENY`, `X-XSS-Protection`).

### Quick Test Credentials
The frontend `AuthModal` includes 1-click test preset buttons:

| Role | Email | Password | Allowed Capabilities |
| :--- | :--- | :--- | :--- |
| **Institutional Admin** | `admin@attendora.edu` | `Admin@12345` | Full administrative control, classes creation, multi-campus reports |
| **Faculty / Teacher** | `faculty@attendora.edu` | `Faculty@12345` | Attendance marking, TOTP QR session launch, student AI evaluations |
| **Student** | `smitkulkarni96@gmail.com` | `Smit@12345` | Student mobile portal, QR camera check-in, personal PDF transcripts |

### Authentication Endpoints
- `POST /api/v1/auth/register`: User registration with email normalization, duplicate check, and HttpOnly cookie.
- `POST /api/v1/auth/login`: Credential verification with anti-enumeration protection and HttpOnly cookie.
- `POST /api/v1/auth/logout`: Invalidation endpoint clearing `attendora_token` cookie.
- `GET /api/v1/auth/me`: Sanitized current user profile (excludes password hashes and internal credentials).

---

## Core Feature Highlights

1. **Responsive Multi-Role Scaffold**:
   - Seamless role switching between **Administrator**, **Faculty / Professor**, and **Student Portal**.
   - Wide layout (pinned navigation sidebar) and compact layout (optimized viewports).

2. **Smart Attendance (Dynamic QR & GPS Geofencing)**:
   - Dynamic 10-second rolling TOTP QR code signed with HMAC-SHA256.
   - Strict 50-meter Haversine GPS classroom perimeter check around Lecture Hall A-101.
   - Anti-proxy hardware device fingerprint binding preventing proxy attendance.

3. **Gemini AI Predictive Intelligence & Intervention**:
   - PII Pseudonymization: strips real names, roll numbers, and emails before prompting LLMs.
   - Predictive Dropout Risk: classifies student attendance vectors into `LOW`, `MODERATE`, `HIGH`, and `CRITICAL` risk tiers with projected semester-end completion rates.
   - Automated Academic Intervention: calculates consecutive classes needed to restore standing above 75% and drafts formal counseling contracts.

4. **Enterprise Multi-Campus Federation**:
   - Hierarchical data model (`System -> Campuses -> Departments`).
   - Unified cross-campus comparative telemetry and strategic briefs.

5. **Native Android Jetpack Compose Client**:
   - Located in `frontend/android/`.
   - Complete Room DB offline persistence, Retrofit cloud networking, and Compose Canvas charts.

