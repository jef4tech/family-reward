# Bloom Family - Android Architecture & Developer Documentation

## Executive Overview
Bloom Family is an offline-first, family-oriented task management, habit-building, and reward app built using modern Android best practices:
- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: Clean Architecture + MVVM + Unidirectional Data Flow (UDF)
- **Database**: Room Database (SQLite) for persistent relational family data
- **Preferences**: DataStore Preferences for theme, notification, and display settings
- **Asynchronous Flow**: Kotlin Coroutines & `StateFlow` / `SharedFlow`

---

## 1. Package Structure & Directory Hierarchy
```
com.example
├── BloomFamilyApplication.kt     # Application entry point
├── MainActivity.kt               # Single Activity host
├── core
│   ├── di/AppContainer.kt        # Dependency Injection Container
│   ├── dispatchers/             # DispatcherProvider abstraction for unit testing
│   ├── logger/                  # Structured DebugLogger
│   ├── time/                    # TimeProvider for deterministic unit testing
│   └── viewmodel/BaseViewModel.kt
├── data
│   ├── database/
│   │   ├── BloomDatabase.kt     # Room Database class
│   │   ├── dao/                 # Room DAOs
│   │   └── entity/              # Database entities
│   └── repository/              # Repository implementations
├── domain
│   ├── model/                   # Domain models & seals
│   └── repository/              # Repository interfaces
├── designsystem/
│   ├── components/              # Reusable M3 Composables (Card, Header, Avatar, Buttons, BottomNav)
│   └── theme/                   # Material 3 Color Schemes, Typography, Spacing
├── feature
│   ├── onboarding/              # Onboarding flow
│   ├── dashboard/               # Family & Child Dashboard
│   ├── family/                  # Member management
│   ├── tasks/                   # Task creation, assignment, submission & approval
│   ├── rewards/                 # Reward creation, requesting & redemption
│   ├── history/                 # Activity history log
│   ├── notifications/           # In-app alert system
│   └── settings/                # Preferences & App configuration
└── navigation
    └── BloomNavHost.kt          # Type-safe Jetpack Navigation Graph
```

---

## 2. Core Architecture & Design Patterns

### Dependency Injection (Constructor Injection + Manual AppContainer)
- Avoids reflection overhead while guaranteeing clean decoupling.
- Centralized in `AppContainer`, which exposes repository singleton instances and testable abstractions (`DispatcherProvider`, `TimeProvider`, `Logger`).

### Single Source of Truth & Reactive Flows
- DAOs expose `Flow<List<Entity>>` or `Flow<Entity?>`.
- ViewModels consume repository Flows and project them to UI via `combine` or `map` into immutable `StateFlow<UiState>`.
- Composables collect state via `collectAsStateWithLifecycle()` or `collectAsState()`.

---

## 3. Data Persistence Layer

### Room Relational Schema
- `FamilyEntity`: Family name, setup timestamp, onboarding completion state.
- `ChildEntity`: Member profile, avatar, current available points, lifetime points.
- `TaskEntity`: Title, description, points, category, recurrence rule.
- `TaskAssignmentEntity`: Links Task to Child, status (PENDING, SUBMITTED, APPROVED, RETRY).
- `TaskSubmissionEntity`: Holds child notes, optional image URIs, submission timestamps.
- `RewardEntity`: Title, description, point cost, category.
- `RewardRequestEntity`: Links Reward to Child, request status (PENDING, APPROVED, REJECTED).
- `ActivityHistoryEntity`: Unified log for task approvals, reward redemptions, and point updates.
- `NotificationEntity`: In-app alerts for family members.

### DataStore Preferences
- Stores `UserSettings`: Theme preference (`SYSTEM`, `LIGHT`, `DARK`), Dynamic Color toggles, Notification preferences, and Family display order.

---

## 4. Testing & Verification Strategy
1. **Unit Tests**:
   - `SettingsRepositoryTest`: Verifies DataStore reads/writes and resetting options.
   - `TaskRepositoryTest`: Validates state machine for task approvals and point balance updates.
2. **Local JVM Robolectric / Roborazzi**:
   - Tests UI render states and screenshot regressions without hardware emulators.
3. **Deterministic Mocking**:
   - Uses `TestDispatcher` and `TestTimeProvider` for reproducible unit testing.

---

## 5. Play Store & Production Readiness Checklist
- **Unique Application ID**: `com.aistudio.bloomfamily.qrxkvd`
- **Build Variants**: Configured with `release` signing config placeholders and R8 / ProGuard optimization rules.
- **Adaptive App Icons**: Configured via `ic_launcher` adaptive vector drawables.
- **Accessibility Compliance**: All touch targets meet minimum 48dp guidelines and include meaningful content descriptions.
