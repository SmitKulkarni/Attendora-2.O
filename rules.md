# Attendora AI Rules & Engineering Standards

These rules define the strict coding, architecture, security, and UI/UX conventions for the **Attendora** project. Any AI coding assistant, human developer, or automated pipeline operating in this repository **must strictly follow these rules**.

---

## 0. Golden Rule: Never Break Existing Functionality

> [!CAUTION]
> **NEVER BREAK EXISTING FUNCTIONALITY UNLESS EXPLICITLY REQUESTED BY THE USER.**
> All changes must be additive, backward-compatible, and non-destructive.

- **Preserve Existing Behaviors**: Never delete, refactor away, or alter functioning screens, viewmodels, mock providers, or navigation tabs without an explicit directive.
- **Maintain Test Tags**: All composable functions, buttons, fields, and interactive cards must retain their existing `testTag` attributes (e.g., `admin_dashboard_screen`, `role_selector_card`, `attendance_submit_btn`, etc.) to prevent breaking automated instrumentation and UI tests.
- **Safe Fallbacks**: If new backend integrations or API calls are introduced, ensure mock fallbacks (`MockDataProvider`) remain available so the UI continues to preview, build, and operate reliably in development and offline environments.
- **Regression Verification**: After making changes, verify that all three roles (`ADMIN`, `TEACHER`, `STUDENT`) continue to render and navigate without crashes or visual clipping.

---

## 1. Coding Standards

### Kotlin & Android Modern Practices
- **Target Android SDK 36**: Code against Android SDK 36 (minor API level 1) with minimum SDK 24.
- **Strict Immutability**:
  - Prefer `val` over `var` at all times.
  - Data classes must have read-only properties (`val`) with default values where appropriate.
  - Collections exposed from repositories or ViewModels must be read-only `List<T>` or `StateFlow<List<T>>`, never mutable collections.
- **Jetpack Compose Best Practices**:
  - **State Hoisting**: Composables should be stateless whenever feasible. Hoist state and event callbacks to the caller (e.g., pass `value: String, onValueChange: (String) -> Unit`).
  - **Recomposition Optimization**: Use `@Stable` and `@Immutable` data models. Do not allocate heavy objects (like `SimpleDateFormat`, `Regex`, or heavy lists) inside Composable execution blocks; use `remember { ... }` or hoist to the ViewModel.
  - **Modifier Parameter Convention**: Every Composable that emits UI must accept an optional `modifier: Modifier = Modifier` as its first optional parameter, chained to the root layout element.
  - **Explicit Preview Providers**: Provide `@Preview` composables with lightweight sample data for desktop previewing.
- **Coroutines & Asynchronous Programming**:
  - Always launch coroutines within appropriate lifecycle scopes (`viewModelScope` in ViewModels, `rememberCoroutineScope()` for transient UI events).
  - Never use `GlobalScope` or blocking operations (`runBlocking`, `Thread.sleep()`).
  - Switch heavy I/O or database operations to `Dispatchers.IO`, keeping the main thread free for 60 FPS Compose rendering.
- **Clean Architecture & Unidirectional Data Flow (UDF)**:
  - **UI Layer (`ui`)**: Observes state from ViewModel and emits user intent events. No business calculations.
  - **State / ViewModel Layer (`viewmodel`)**: Holds `StateFlow`, executes business rules, coordinates repositories.
  - **Model Layer (`model`)**: Pure Kotlin domain data classes and enums. Free of Android framework dependencies.
  - **Data Layer (`data`, `db`, `network`)**: Handles data persistence, network requests, and local caching.

---

## 2. Folder Structure Rules

The project directory structure is strictly organized by technical layer and feature boundaries. Do not scatter files into arbitrary root folders.

```plaintext
d:\Mayuri Cloud Computing Project\Attendora-2.O\
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt               # Single activity entry point
│   │   │   │   ├── data/                         # Data layer, mock data & repositories
│   │   │   │   │   ├── MockDataProvider.kt
│   │   │   │   │   └── repository/               # (Future) Concrete data repositories
│   │   │   │   ├── model/                        # Domain entities and enums
│   │   │   │   │   └── AttendoraModels.kt
│   │   │   │   ├── ui/                           # Jetpack Compose UI
│   │   │   │   │   ├── AttendoraApp.kt           # Main application scaffold & navigation
│   │   │   │   │   ├── components/               # Reusable UI widgets & charts
│   │   │   │   │   │   ├── AttendanceTrendsChart.kt
│   │   │   │   │   │   ├── AttendoraSidebar.kt
│   │   │   │   │   │   ├── AttendoraTopBar.kt
│   │   │   │   │   │   ├── MetricCards.kt
│   │   │   │   │   │   ├── StatusPill.kt
│   │   │   │   │   │   ├── StudentDetailDialog.kt
│   │   │   │   │   │   ├── StudentTable.kt
│   │   │   │   │   │   └── WeeklyAttendanceChart.kt
│   │   │   │   │   ├── screens/                  # Top-level feature screens
│   │   │   │   │   │   ├── AdminDashboardScreen.kt
│   │   │   │   │   │   ├── AttendanceMarkingScreen.kt
│   │   │   │   │   │   ├── ClassesManagementScreen.kt
│   │   │   │   │   │   ├── CreateClassScreen.kt
│   │   │   │   │   │   ├── ReportsScreen.kt
│   │   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   │   ├── StudentMobileScreen.kt
│   │   │   │   │   │   └── StudentsManagementScreen.kt
│   │   │   │   │   └── theme/                    # Design tokens & Material 3 styling
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   └── viewmodel/                    # State management & business logic
│   │   │   │       └── AttendoraViewModel.kt
│   │   │   └── res/                              # Android XML resources (drawables, mipmaps, values)
│   │   ├── test/                                 # Unit & Robolectric tests
│   │   └── androidTest/                          # Compose UI instrumentation tests
│   ├── build.gradle.kts                          # App-level dependencies & plugins
│   └── proguard-rules.pro                        # R8/ProGuard configuration
├── gradle/                                       # Gradle wrapper and version catalog
│   ├── libs.versions.toml                        # Centralized dependency catalog
│   └── wrapper/
├── public/                                       # Design specs, PRD assets, documentation
├── .env.example                                  # Template for local environment variables
├── build.gradle.kts                              # Top-level build config
├── decisions.md                                  # Architectural Decision Records (ADRs)
├── rules.md                                      # AI & project operational rules (this file)
├── memory.md                                     # Persistent project memory & knowledge base
├── phases.md                                     # Implementation roadmap, phases & task checklists
├── changelog.md                                  # Chronological version changelog
└── settings.gradle.kts                           # Module registration
```

### Folder Boundary Rules
1. **Never put UI Composables in `model/` or `data/`**: Keep domain data classes 100% UI-agnostic.
2. **Components vs Screens**:
   - `ui/components/`: Self-contained, reusable across multiple screens (e.g., `StatusPill`, `AttendoraTopBar`, `MetricCards`).
   - `ui/screens/`: High-level feature screens mapping directly to application navigation routes or primary dialog flows.
3. **Themes**: All colors, typography, and shapes must be declared in `ui/theme/`. No hardcoded hex color literals in UI code.

---

## 3. Naming Conventions

| Category | Convention | Examples |
| :--- | :--- | :--- |
| **Composable Functions** | PascalCase, Noun or Noun phrase | `AttendoraApp`, `StatusPill`, `WeeklyAttendanceCard` |
| **Screens** | PascalCase with `Screen` suffix | `AdminDashboardScreen`, `CreateClassScreen` |
| **ViewModels** | PascalCase with `ViewModel` suffix | `AttendoraViewModel` |
| **Models & Enums** | PascalCase, singular | `Student`, `AttendanceStatus`, `ClassGroup` |
| **StateFlow (Private)** | camelCase prefixed with underscore `_` | `_students`, `_selectedTab`, `_currentRole` |
| **StateFlow (Public)** | camelCase read-only `asStateFlow()` | `students`, `selectedTab`, `currentRole` |
| **Event Flows** | camelCase with `Event` or `Action` suffix | `_toastEvent`, `toastEvent` |
| **Event Handlers** | camelCase prefixed with `on` | `onToggleStatus`, `onAddNewClass`, `onStudentClick` |
| **Color Constants** | PascalCase descriptive token name | `OceanTeal`, `CanvasIceSlate`, `PresentGreenSolid` |
| **Packages** | lowercase dot-separated | `com.example.ui.components` |
| **Test Tags** | snake_case descriptive identifier | `admin_dashboard_screen`, `student_table_card` |

---

## 4. UI/UX Consistency Rules

Attendora follows a tailored modern EdTech design language that combines administrative clarity with mobile responsiveness.

### Color Tokens & Semantic Badges
All visual components must use the semantic color tokens defined in `com.example.ui.theme.Color.kt`:
- **Primary Brand**:
  - `OceanTeal` (`#2E8B9A`): Primary interactive buttons, active tab indicators, chart curves.
  - `OceanTealDark` (`#006673`): Primary container fills, dark contrast badges.
  - `OceanTealLight` (`#7ED3E3`): Subtle highlights, secondary graph lines.
- **Canvas & Neutral Surfaces**:
  - `CanvasIceSlate` (`#F4FAFE`): Base background canvas for all screens.
  - `SurfaceCard` (`#FFFFFF`): Elevated cards, dialog bodies, sidebar background.
  - `OutlineBorder` (`#E2E8F0` / `#BEC8CB`): Subtle 1dp borders for cards and tables.
  - `TextPrimary` (`#161C1F`): Primary typography, headings, student names.
  - `TextSecondary` (`#475569`): Subheadings, roll numbers, timestamps.
- **Attendance Status Badges**:
  - **Present**: Fill `PresentGreenFill` (`#E6F8F0`), Stroke `PresentGreenStroke` (`#34D399`), Text `PresentGreenText` (`#065F46`), Solid `PresentGreenSolid` (`#10B981`).
  - **Absent**: Fill `AbsentCoralFill` (`#FEECEB`), Stroke `AbsentCoralStroke` (`#F87171`), Text `AbsentCoralText` (`#991B1B`), Solid `AbsentCoralSolid` (`#EF4444`).
  - **Late**: Fill `LateAmberFill` (`#FEF7E6`), Stroke `LateAmberStroke` (`#FBBF24`), Text `LateAmberText` (`#92400E`), Solid `LateAmberSolid` (`#F59E0B`).

### Layout & Responsiveness
- **Dual-Layout Threshold**: Use `BoxWithConstraints` with a breakpoint at `800.dp`.
  - **>= 800.dp (Desktop / Tablet / Wide Screen)**: Render a fixed 240dp `AttendoraSidebar` on the left, top navigation header, and fluid content pane on the right.
  - **< 800.dp (Mobile Screen)**: Render `ModalNavigationDrawer` with drawer sheet, compact top bar with hamburger menu, and sticky bottom navigation bar.
- **Spacing Grid**: Standardize all padding and margins on the 4dp/8dp grid system (`4.dp`, `8.dp`, `12.dp`, `16.dp`, `24.dp`, `32.dp`).
- **Touch Targets**: All clickable elements (buttons, status pills, filter chips, table rows) must maintain a minimum touch target of `48.dp` height/width.
- **Card Styling**: Use `RoundedCornerShape(12.dp)` or `RoundedCornerShape(16.dp)` with subtle 1dp border `OutlineBorder` and clean elevations (`CardDefaults.cardElevation(defaultElevation = 0.dp)` or `1.dp` with surface fill).

---

## 5. Git Commit Rules

The repository strictly enforces **Conventional Commits** for clean changelog generation and Git history traceability:

### Format
```plaintext
<type>(<scope>): <short description in present tense>

[optional body explaining context, reasoning, and breaking changes]

[optional footer referencing issues or PRs]
```

### Commit Types
- `feat`: A new user-facing feature or screen (e.g., `feat(classes): add multi-step class creation screen`).
- `fix`: A bug fix or visual defect correction (e.g., `fix(table): prevent horizontal overflow on student status pills`).
- `docs`: Documentation updates, ADRs, or README edits (e.g., `docs(adr): record PostgreSQL multi-tenancy decision`).
- `style`: Formatting, whitespace, or Composable code styling changes with no logic change.
- `refactor`: Code change that neither fixes a bug nor adds a feature.
- `perf`: Performance improvement (e.g., memoizing chart calculations).
- `test`: Adding or correcting unit, Robolectric, or instrumentation tests.
- `chore`: Build system changes, Gradle dependencies, `.gitignore` updates.

### Branching & PR Guidelines
- Main branch: `main` (production-ready at all times).
- Feature branches: `feat/<feature-name>`, `fix/<issue-name>`, `chore/<task-name>`.
- Commit messages must never use generic phrases like "fixed bug", "updated files", or "wip".

---

## 6. Security & Environment Variable Rules

### API Keys and Secrets Management
- **Never Hardcode Secrets**: Under no circumstances should API keys, database credentials, Firebase service accounts, JWT secrets, or Gemini keys be committed directly into Kotlin code, TypeScript code, Python files, or Git history.
- **Environment Files**:
  - `backend/.env` & `frontend/.env`: Local secrets files (MUST be listed in `.gitignore`).
  - `backend/.env.example` & `frontend/.env.example`: Public template files containing sanitized placeholder keys (e.g., `JWT_SECRET=your_secret_here`).
- **Token Security**:
  - Prefer secure, HttpOnly, SameSite=Lax cookies for web clients to prevent XSS-based token theft.
  - API and mobile clients use standard `Authorization: Bearer <token>` headers.
  - Never log raw authorization tokens, passwords, or password hashes to logging frameworks or console outputs.
- **Multi-Tenant Authorization Verification**:
  - Always verify that the caller's JWT `institution_id` matches the target entity's `institution_id` before processing modifications.
  - Student role users must strictly be restricted to read-only access for their own individual student records.

---

## 7. Authentication, Authorization & Validation Rules

### Password & Credential Security
- **Salted Key-Derivation Hashing**: Passwords must always be hashed with strong, salted key-derivation algorithms (`scrypt` or `bcrypt`). Plaintext passwords must never be stored, logged, or returned in responses.
- **Timing-Attack Resistance**: Always compare password hashes using constant-time verification (`check_password_hash` or `bcrypt.checkpw`).
- **Anti-Enumeration Login**: Authentication failures must return generic error messages (e.g., `"Invalid email or password"`) with HTTP 401 Unauthorized to prevent attackers from discovering whether an email exists in the system.

### Reusable Authorization Middleware
- **Decoupled RBAC**: Never duplicate role checks across controllers. Enforce permissions using reusable middleware (`require_auth(allowed_roles=...)` and `require_roles(...)`).
- **Backend Enforcement**: Frontend route protection provides UX guidance; the backend must independently enforce authentication (HTTP 401) and authorization (HTTP 403 Forbidden).
- **Sanitized Profiles**: Current user endpoints (`GET /api/v1/auth/me`) must strictly sanitize output, stripping `password_hash`, `password`, and internal flags.

### Multi-Tier Input Validation
- **Defense in Depth**: Client-side validation delivers instant feedback to the user; backend validation enforces security and data integrity. Never trust client validation alone.
- **RFC Email Normalization**: Validate email format with standard RFC 5322 regex and normalize (`.strip().lower()`).
- **Password Complexity**: Enforce minimum 8 characters, at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special symbol. Reject mismatched password and confirm password fields.
- **Duplicate Account Rejection**: Reject registration of existing emails with HTTP 409 Conflict.
- **Rate Limiting**: Protect authentication endpoints against brute force with sliding-window IP rate limiting (HTTP 429 Too Many Requests).

---

## Checklist for AI Code Assistant

Before submitting any code change or generated file, verify the following checklist:

```markdown
- [ ] Golden Rule: Does this change preserve all existing functional features, screens, and models?
- [ ] Test Suite: Do all existing pytest tests and new auth tests pass?
- [ ] Frontend Build: Does `npm --prefix frontend run build` compile with 0 TypeScript errors?
- [ ] Authentication: Are passwords salted and hashed? Are JWTs verified with expiration?
- [ ] Authorization: Are endpoints protected by role-based middleware (401/403)?
- [ ] Validation: Are all inputs validated on both frontend and backend?
- [ ] Anti-Enumeration: Does login return generic "Invalid email or password" on failure?
- [ ] Sanitization: Does `GET /api/v1/auth/me` strip password hashes and secrets?
- [ ] Security: Are secrets strictly loaded from `.env`?
```


