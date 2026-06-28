# TasksApp

A modern Android sample app focused on **state management in Jetpack Compose**.

It demonstrates common Compose state management patterns through tasks, profile, and settings features, showing how different types of state are managed across the UI, ViewModels, and the data layer.

---

## What you'll learn

This project demonstrates practical Compose state management patterns, including:

* Local UI state with `remember` and `rememberSaveable`
* Stateful and stateless composables
* State hoisting and unidirectional data flow
* Screen-level state with `ViewModel` and immutable `UiState`
* Producing UI state from `Flow`, `combine`, and `stateIn`
* Event-driven state updates
* `derivedStateOf`, `snapshotFlow`, and Compose side effects
* Lifecycle-aware state collection
* State restoration across configuration changes and process death
* Custom `Saver` implementations
* Stable immutable models for predictable recomposition

The project focuses on how these patterns work together in a typical Compose application.

---

## Features

### Tasks

* Priority filters with live counts
* Debounced search
* Swipe-to-delete with Undo
* Expandable task rows
* Multi-selection with bulk actions

### Task Editor

* Create and edit tasks
* State restoration after process death

### Profile

* Editable profile information
* Input validation
* Avatar picker

### Settings

* Theme selection
* Task sorting
* Hide completed tasks

### Adaptive UI

* Bottom navigation
* Navigation rail on larger layouts

---

## Project structure

```text
app/src/main/java/com/venturedive/tasksapp/

├── ui/                 App shell and app-level state
├── navigation/         Type-safe Navigation Compose routes
├── core/
│   ├── designsystem/   Material 3 theme and reusable UI
│   ├── ui/             Shared UI utilities
│   └── di/             Hilt dependency injection
├── data/
│   ├── local/          Room database
│   └── repository/     Repositories and DataStore
├── domain/
│   └── model/          Immutable domain models
└── feature/
    ├── tasks/
    ├── taskedit/
    ├── profile/
    └── settings/
```

Each feature follows the same structure:

* Screen
* Stateless Content
* UiState
* ViewModel

This keeps state ownership explicit and data flow predictable.

---

## Tech stack

* Kotlin
* Jetpack Compose
* Material 3
* Navigation Compose (type-safe routes)
* ViewModel
* Kotlin Coroutines & Flow
* Hilt
* Room
* DataStore
* Robolectric
* Turbine

**Minimum SDK:** 27

---

## Build & run

Build the debug APK:

```bash
./gradlew :app:assembleDebug
```

Install it on a connected device or emulator:

```bash
./gradlew :app:installDebug
```

Or open the project in Android Studio and run the **app** configuration.

Sample data is automatically seeded on first launch.

---

## Testing

Run unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

Run instrumentation tests:

```bash
./gradlew :app:connectedDebugAndroidTest
```

The project includes tests for:

* ViewModel logic
* Flow pipelines
* Compose UI behavior
* State restoration using `StateRestorationTester`
* Reactive streams using Turbine

---

Built at VentureDive.
