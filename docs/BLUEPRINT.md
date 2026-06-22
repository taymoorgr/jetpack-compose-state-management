# TasksApp — Compose State Management demo

A polished, fully-persistent Material 3 app whose architecture is a 1:1 embodiment of the
*"Jetpack Compose — State Management"* talk. Every important state-management topic has a real,
runnable home. Supporting features (CRUD plumbing, navigation) exist so the app is genuinely
usable; only the state-management surfaces are narrated during the demo.

## Locked stack (resolved live, 2026-06-19)

| Tool / lib | Version | | Tool / lib | Version |
|---|---|---|---|---|
| Gradle | 9.6.0 | | Hilt | 2.59.2 |
| AGP | 9.2.1 | | Room | 2.8.4 |
| Kotlin | 2.4.0 | | DataStore (prefs) | 1.2.1 |
| KSP | 2.3.9 | | Navigation Compose | 2.9.8 |
| Compose BOM | 2026.06.00 | | kotlinx-serialization-json | 1.11.0 |
| Lifecycle | 2.11.0 | | kotlinx-collections-immutable | 0.5.0 |
| Coroutines | 1.11.0 | | Turbine / Robolectric | 1.2.1 / 4.16.1 |

`compileSdk`/`targetSdk` = 37, `minSdk` = 27, JVM target 17. AGP 9 built-in Kotlin (no separate
`kotlin-android` plugin); `kotlin-metadata-jvm` is added to the KSP classpath so Dagger/Hilt (and
Room) can read Kotlin 2.4.0 metadata while KSP is still at 2.3.9. `retain {}` / `rememberSerializable`
are intentionally **not** built
(too new for the stable Compose BOM) — mentioned only in the talk.

## Architecture

Layered, feature-first, UDF. `data (Flow) → repository → ViewModel (UiState) → Screen (collect at
edge) → Content (stateless)`, events flow back up. Immutable `UiState`, edge-to-edge, type-safe
navigation, version catalog, VM + Compose tests, ktlint/detekt clean (eight-pillar aligned).

```
com.venturedive.tasksapp
├─ TasksAppApp / MainActivity
├─ core/ designsystem (theme + components), ui (shared stateless), result/
├─ data/ local/db (Room), local/datastore, repository/, di/
├─ domain/model/ (immutable; ImmutableList)
├─ feature/ tasks/{list,detail,edit}, profile/, settings/
└─ navigation/ (type-safe @Serializable routes)
```

## State category → persistence (talk slide 5)

| Category | Lives in | Survives | Mechanism |
|---|---|---|---|
| UI element state | composable | recomposition / recreation | `remember` / `rememberSaveable` |
| Screen UI state | ViewModel + small keys | config change / process death | VM + `SavedStateHandle` |
| Business / app state | data layer | forever | **Room** (entities) + **DataStore** (prefs) |

Room exposes `Flow` → `combine(...).stateIn(WhileSubscribed)` → `collectAsStateWithLifecycle()` →
`UiState`. Persistence is what makes the reactive state pipeline real.

## Concept → file map (talk fidelity)

| Talk topic | Home | Live edit |
|---|---|---|
| `remember` → rotation loss → `rememberSaveable` | expandable row / search field | #1 |
| `LazyColumn` keys, item-state-on-leave, observable-field class | `tasks/list`, `Task` | #2 |
| `UiState` (immutable, derived, sealed, `ImmutableList`) | `ProfileUiState`, `TasksListUiState` | — |
| `MutableStateFlow`/`asStateFlow`, `update{copy}` | `TaskEditViewModel` | — |
| `combine` + `stateIn(WhileSubscribed)` | `TasksListViewModel`, `ProfileViewModel` | — |
| `collectAsStateWithLifecycle`, don't pass VM down | all `*Screen` | — |
| `SavedStateHandle` (nav arg by key) | `TaskEditViewModel` | — |
| state holder + `rememberX` factory | `rememberValidatedFieldState` | — |
| `derivedStateOf` (scroll FAB) | `tasks/list` | — |
| `snapshotFlow` (search debounce) | `tasks/list` | — |
| `produceState` (avatar decode) | `profile/components` | — |
| `rememberUpdatedState` (one-shot timeout → latest callback) | `feature/landing` `LandingScreen` | green→red |
| one-time events (Channel → `LaunchedEffect`); replay bug | delete-undo, profile-save | #3 |
| `StateRestorationTester` survival test | `androidTest` / Robolectric | green→red |
| ViewModel `UiState` logic test (Turbine) | `test/` | — |

## Recomposition & stability — shown via IDE tooling (no app code)

- **"How many times did it recompose?"** → Android Studio **Layout Inspector → Recomposition Counts**
  (Recompositions / Skips columns) + recomposition highlighter, on a debug build. No app code.
- **"Why isn't it skipping?"** → opt-in **Compose compiler reports** (`composeCompiler { reportsDestination /
  metricsDestination }`, gated behind a Gradle property) — shows `stable`/`unstable` params; pairs with the
  `List` → `ImmutableList` moment (slide 15). Build-time flag, not app code.
- **Decision:** no in-app recomposition-counter / highlighter helper composables — the IDE tooling covers it.

## Build roadmap

0. **Scaffold** — Gradle + catalog, Hilt app, edge-to-edge MainActivity, base M3 theme. ✅ this phase
1. Design system — theme/components, scaffold + insets
2. Data layer — Room + DataStore + repositories (Flow) + Hilt modules + seed
3. Navigation shell — type-safe routes, bottom nav
4. Tasks (CRUD + list state topics)
5. Profile (UiState, combine+stateIn, SavedStateHandle, produceState, state holder)
6. Settings (DataStore prefs)
7. Testing — VM unit (Turbine) + StateRestorationTester (Robolectric)
8. Polish — motion, empty/loading/error, a11y, dark theme, static analysis
9. Walkthrough doc — section → files → slide → live edit (talk script)

## Run

```bash
./gradlew :app:assembleDebug        # build
./gradlew :app:installDebug         # install on device/emulator
./gradlew test                      # JVM unit tests
```
