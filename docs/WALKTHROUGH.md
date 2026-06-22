# TasksApp — Live Demo Walkthrough

The presenter's script for the *Jetpack Compose — State Management* demo. Maps every deck topic to a
file and an on-screen action. Target **~15 minutes (flexible)**; a cut-list and an extended path are at
the end.

How to read each beat: **Do** (what to click/type) · **Say** (one-line cues — the "why") · **Files**
(what to have open). Deck slide numbers are in `[s##]`.

---

## 0 · Pre-flight (before you present)

- [ ] Emulator/device running; app installed: `./gradlew :app:installDebug`
- [ ] Rotation works on the device (auto-rotate on) — needed for the live edits.
- [ ] Android Studio open with these files pinned in tabs (left→right in demo order):
  `feature/tasks/components/TaskRow.kt`, `feature/tasks/TasksListScreen.kt`,
  `feature/tasks/TasksListViewModel.kt`, `feature/profile/ProfileViewModel.kt`,
  `feature/settings/SettingsViewModel.kt`, `feature/landing/LandingScreen.kt`.
- [ ] A terminal ready for the two test flips.
- [ ] Optional: **Layout Inspector** reachable (Running Devices ▸ inspector) for the recomposition beat.
- [ ] Optional: enable Compose compiler reports (for the stability beat) — see §6.
- [ ] Reset state if you've been rehearsing: wipe app data so the Tasks list shows the seed.

**One-sentence framing to open with:** *"`UI = f(state)` — so a state-management talk is really about
who owns each piece of state and how it flows; this app is built so every one of those decisions is
visible in real code."*

---

## 1 · The 15-minute minute-by-minute

### 0:00–1:30 — Launch & framing  `[s3, s4]`
- **Do:** Cold-launch the app → the **Landing splash** appears, then auto-advances to **Tasks**.
- **Say:** "`UI = f(state)`. An event changes state; Compose re-runs only the readers. Watch for three
  *categories* of state as we go — element, screen, and app/business."
- **Files:** `MainActivity.kt` (root: theme + the splash gate), `ui/TasksAppApp.kt` (the single-Scaffold
  shell + bottom nav + `NavHost`).

### 1:30–4:00 — Local state, keys, retention  `[s5, s7, s8, s9]`  ← **Live edits #1 & #2**
- **Do:** In Tasks, tap a row to **expand** its notes. Point out the checkbox (complete) vs the expand.
- **Say:** "Two different categories on one row: completion is **business state** (Room, via a callback);
  the expand is **UI-element state**, local to the row in `rememberSaveable`."
- **Do (Live Edit #1 — rotation/retention):** rotate the device → expanded row **survives** (it's
  `rememberSaveable`). Then in `TaskRow.kt` change `rememberSaveable` → `remember`, hot-reload/rerun,
  expand, rotate → **state lost**. Revert. *(exact steps in §3)*
- **Do (Live Edit #2 — keys):** in `TasksListScreen.kt` delete `key = { it.id }`; expand row #1, change
  **Sort order** in Settings (or scroll) → the expand "jumps" to the wrong row. Revert. *(§3)*
- **Files:** `feature/tasks/components/TaskRow.kt`, `feature/tasks/TasksListScreen.kt`.

### 4:00–6:00 — Hoisting, UDF, stateless content  `[s10, s11, s12, s13]`
- **Do:** Open `TasksListScreen.kt`; point at `TasksScreen` (collects state) vs `TasksContent`
  (stateless — `state` in, callbacks out). Then `ValidatedTextField.kt`.
- **Say:** "State flows down as a value, events flow up as callbacks — single source of truth. The
  `Content` is stateless, so it's previewable and testable. We never pass the ViewModel down."
- **Say (state holder):** "`ValidatedFieldState` is a tiny UI-logic **state holder** built with a
  `remember` factory — it tracks 'has this field been touched' so errors only show after you leave it."
- **Files:** `feature/tasks/TasksListScreen.kt`, `core/designsystem/component/ValidatedTextField.kt`.

### 6:00–9:00 — Screen-level state: two ViewModel variants + reactive fan-out  `[s14–s19]`
- **Do:** Open `TasksListViewModel.kt` — highlight `combine(tasks, prefs, searchQuery).stateIn(…,
  WhileSubscribed(5_000), …)`. Then `ProfileViewModel.kt` — highlight `MutableStateFlow` +
  `update { it.copy(...) }`.
- **Say:** "Two legitimate ways to produce screen state, both in the deck: Tasks **reacts** to several
  streams via `combine` + `stateIn(WhileSubscribed)`; Profile **owns** one `UiState` and republishes with
  `update{copy}`. Both expose a read-only `StateFlow`; the UI collects with
  `collectAsStateWithLifecycle`."
- **Do (reactive fan-out — the payoff):** go to **Settings**, flip **Theme** → whole app re-themes; change
  **Sort order** / toggle **Hide completed** → switch to **Tasks** → the list has already re-sorted/filtered.
- **Say:** "One source of truth (DataStore) fans out to three consumers — the Tasks pipeline, the app
  theme, and Settings itself. Change it once, everyone updates."
- **Say (restoration):** "Screen state that must survive process death uses small keys in
  `SavedStateHandle` — the Task editor reads its `taskId` that way, then rebuilds from durable data."
- **Files:** `feature/tasks/TasksListViewModel.kt`, `feature/profile/ProfileViewModel.kt`,
  `feature/settings/SettingsViewModel.kt`, `feature/taskedit/TaskEditViewModel.kt`.

### 9:00–11:30 — Deriving & transforming state  `[s20]` + `rememberUpdatedState`
- **Do (`derivedStateOf`):** scroll the Tasks list down → the **scroll-to-top FAB** appears. *(Optional:
  Layout Inspector recomposition counts — §6.)*
- **Say:** "`derivedStateOf` — the FAB-visibility boolean recomposes only when it flips, not on every
  scroll tick."
- **Do (`snapshotFlow`):** type in **Search** → results update after a pause.
- **Say:** "The field's text is local Compose state; `snapshotFlow` turns it into a Flow so we
  `debounce` — one search per pause, not per keystroke."
- **Do (`produceState`):** open **Profile**, pick an **avatar** → it appears.
- **Say:** "`produceState` decodes the image off the main thread and exposes the result as `State`."
- **Do (`rememberUpdatedState`):** open `LandingScreen.kt`.
- **Say:** "The splash runs a one-shot `LaunchedEffect(Unit)` + delay; `rememberUpdatedState` keeps it
  from restarting while still calling the latest callback. (It's a codelab API, not a deck slide — included
  to round out the effect family; here it's idiomatic-but-belt-and-suspenders, which is itself a fair point
  to make.)"
- **Files:** `feature/tasks/TasksListScreen.kt`, `feature/tasks/components/TaskSearchField.kt`,
  `feature/profile/components/ProfileAvatar.kt`, `feature/landing/LandingScreen.kt`.

### 11:30–13:00 — State vs. events  `[s21]`  ← **Live edit #3 (talk-through or stage)**
- **Do:** In Profile, edit the name and tap **Save** → a **"Profile saved"** snackbar fires once.
- **Say:** "Saving is an **event**, not state. We send it through a `Channel` consumed once in a
  `LaunchedEffect`. If we'd modeled it as `uiState.saved = true` and reacted to that, **rotation would
  re-read the state and fire the snackbar again** — the classic replay bug (deck `[s21]`)."
- **Do (optional live stage):** introduce the bug to prove it — steps in §3 (Live Edit #3).
- **Files:** `feature/profile/ProfileViewModel.kt` (`ProfileEvent`, the `Channel`),
  `feature/profile/ProfileScreen.kt` (the `LaunchedEffect` that collects events).

### 13:00–14:00 — Testing state  `[s22]`  ← **green→red flips**
- **Do:** Run `./gradlew :app:testDebugUnitTest --tests "*TaskRowRestorationTest"` → **green**. Swap
  `rememberSaveable`→`remember` in `TaskRow.kt`, rerun → **red**. Revert. *(§4)*
- **Say:** "`StateRestorationTester` tears down and restores the composition exactly like the system —
  the only way to *prove* a value persists. Swap to `remember` and the test catches it."
- **Say (mention):** "Same technique proves `rememberUpdatedState` in `LandingScreenTest` — latest
  callback wins; revert it and it fires the stale one."
- **Files:** `feature/tasks/TaskRowRestorationTest.kt`, `feature/landing/LandingScreenTest.kt`.

### 14:00–15:00 — Pitfalls recap & close  `[s23]`
- **Say:** Walk the five pitfalls (§5), each "…and here we do it right." Land on: *"Pick the lowest owner
  that satisfies every reader, writer, and lifetime — state down, events up."*
- **Do:** Open the floor. Keep §7 (Q&A backstop) handy.

---

## 2 · Demo state cheat-sheet
- Seed: 5 tasks (mixed priority, one completed), 1 profile (your name/email).
- If a live edit leaves the app weird: **revert the file, rerun** — every edit below is a one-liner.
- Rotation is your friend for retention beats; Settings is your friend for the reactive-fan-out beat.

---

## 3 · Live edits (exact changes + revert)

### Live Edit #1 — retention (`remember` vs `rememberSaveable`)
- **File:** `feature/tasks/components/TaskRow.kt`
- **Change:** `var expanded by rememberSaveable { mutableStateOf(false) }`
  → `var expanded by remember { mutableStateOf(false) }`
- **Show:** expand a row → rotate → **collapses** (state lost). With `rememberSaveable` it survives.
- **Revert:** change `remember` back to `rememberSaveable`.

### Live Edit #2 — lazy-list keys
- **File:** `feature/tasks/TasksListScreen.kt` (the `items(...)` in `TaskList`)
- **Change:** remove `key = { it.id }` from `items(items = state.tasks, key = { it.id })`.
- **Show:** expand the top row, then change **Sort order** in Settings (reorders the list) → the expanded
  state stays on the *position*, not the *task* — it's now on the wrong row.
- **Revert:** add `key = { it.id }` back.

### Live Edit #3 — one-time event modeled as state (optional stage)
- **Goal:** show the replay-on-rotation bug, then the `Channel` fix.
- **Stage the bug (temporary):**
  1. In `feature/profile/ProfileUiState.kt` add `val justSaved: Boolean = false`.
  2. In `feature/profile/ProfileViewModel.kt` `onSave()`, after persisting, also
     `_uiState.update { it.copy(justSaved = true) }` (instead of relying only on the event).
  3. In `feature/profile/ProfileScreen.kt` add `if (state.justSaved) LaunchedEffect(Unit) { snackbar… }`.
- **Show:** Save → snackbar; **rotate** → snackbar fires **again** (state replayed).
- **Fix / revert:** remove those three changes — the existing `ProfileEvent.Saved` `Channel` path is the
  correct version (fires exactly once).
- *(If short on time, skip staging and just narrate it against the correct code.)*

---

## 4 · Test flips (commands)

| Proves | Command | Flip that turns it red | Revert |
|---|---|---|---|
| `rememberSaveable` survival `[s22]` | `./gradlew :app:testDebugUnitTest --tests "*TaskRowRestorationTest"` | `TaskRow.kt`: `rememberSaveable`→`remember` | back to `rememberSaveable` |
| `rememberUpdatedState` latest-callback | `./gradlew :app:testDebugUnitTest --tests "*LandingScreenTest"` | `LandingScreen.kt`: `currentOnTimeout()`→`onTimeout()` | back to `currentOnTimeout()` |

Run the whole suite anytime: `./gradlew :app:testDebugUnitTest` (18 tests). First Robolectric run is
~90s (downloads its runtime), seconds after.

---

## 5 · Pitfalls recap `[s23]` — and where we avoid each

| Pitfall | We do it right in… |
|---|---|
| Mutating state in place | `update { copy() }` everywhere (`ProfileViewModel`, `TasksListViewModel`) |
| Passing the ViewModel down | `*Content` take `state` + lambdas; VM stays at the `*Screen` edge |
| Hoisting every flag to the VM | `TaskRow` expand is local `rememberSaveable`, not in any VM |
| One-time events as state | `Channel` events for save/undo, consumed once in `LaunchedEffect` |
| Storing derived state | `canSave`, `isEmpty`, FAB visibility are **computed** (`derivedStateOf` / `get()`) |

---

## 6 · Recomposition via tooling (no app code)

**Layout Inspector (the `derivedStateOf` beat):**
1. Run the debug app; open **Running Devices ▸ Layout Inspector**.
2. Enable **Show Recomposition Counts** (gear/settings); watch the **Recompositions / Skips** columns.
3. Scroll the Tasks list → the scroll-to-top FAB area's reader recomposes only when the boolean flips
   (thanks to `derivedStateOf`), not on every scroll frame.

**Compiler stability report (the `ImmutableList` / `[s15]` point), optional:**
- Add to `app/build.gradle.kts`: `composeCompiler { reportsDestination = layout.buildDirectory.dir("compose_reports"); metricsDestination = layout.buildDirectory.dir("compose_reports") }`, build, and open the
  `*-classes.txt` — show `TasksListUiState` is `stable`/skippable because it uses `ImmutableList`.
- Remove the block afterward to keep normal builds clean.

---

## 7 · Q&A backstop (likely probes → crisp answers)

- **"Why is the repository interface in `data`, not `domain`?"** — Google's recommended app architecture
  (and Now in Android) put the repo abstraction in the data layer; our `domain/` holds models, no use-case
  layer. Strict Clean Architecture would invert it (interface in `domain`); both are valid — we follow
  Google's. (One-line move if desired.)
- **"Two ViewModel patterns — why?"** — to show both deck variants: `combine`+`stateIn` (multi-stream,
  Tasks `[s17]`) and owned `MutableStateFlow`+`update{copy}` (single source, Profile `[s16]`).
- **"Is the ViewModel testable without Android?"** — yes; the VMs have **no** `android.*` / `navigation` /
  `compose` imports, emit error **types** (not strings), read nav args by key. 18 JVM tests prove it.
- **"Why `rememberUpdatedState` if the splash callback never changes?"** — honest answer: it's the
  idiomatic, future-proof pattern (matches the codelab); strictly it's a safety net here. It becomes
  mandatory in long-lived listener effects (e.g., save-on-`ON_PAUSE`).
- **"`WhileSubscribed(5_000)`?"** — keeps the upstream flow warm for 5s after the last collector so a
  rotation doesn't re-run the pipeline, but it stops when the UI is truly gone.
- **Toolchain:** AGP 9.2.1 / Kotlin 2.4.0 / KSP 2.3.9 (KSP lags one minor → `kotlin-metadata-jvm` on the
  KSP classpath lets Hilt/Room read 2.4.0 metadata). Robolectric pins `@Config(sdk=[37])` + the `v2`
  `createComposeRule`.
- **"`retain {}` / `rememberSerializable`?"** — deck mentions them; intentionally **not** built (too new for
  the stable Compose BOM) — we'd verify availability before adopting.

---

## 8 · Coverage matrix (concept → file → slide → shown how)

| Concept | File | Slide | Shown |
|---|---|---|---|
| `mutableStateOf` / `remember` / declarative UI | `TaskRow.kt` | s3, s7 | expand a row |
| `rememberSaveable` retention | `TaskRow.kt`, `TaskSearchField.kt` | s9 | rotate (Live Edit #1) |
| LazyColumn stable keys | `TasksListScreen.kt` | s8 | drop key (Live Edit #2) |
| Three state categories | `TaskRow` / `*UiState` / `data` | s5 | narrate on the row |
| Stateless vs stateful, UDF, don't-pass-VM | `*Screen` / `*Content` | s10, s12 | open the split |
| State holder + `remember` factory | `ValidatedTextField.kt` | s11 | error-on-touch |
| Immutable `UiState` + derived props | `TasksListUiState.kt`, `ProfileUiState.kt` | s15 | + compiler report |
| `combine` + `stateIn(WhileSubscribed)` | `TasksListViewModel.kt` | s17 | settings fan-out |
| `MutableStateFlow` + `update{copy}` | `ProfileViewModel.kt` | s16 | edit profile |
| `collectAsStateWithLifecycle` | all `*Screen` | s18 | (code) |
| `SavedStateHandle` (key) | `TaskEditViewModel.kt` | s19 | (code) |
| `derivedStateOf` | `TasksListScreen.kt` | s20 | scroll FAB + Inspector |
| `snapshotFlow` (debounce) | `TaskSearchField.kt` | s20 | search |
| `produceState` (async→State) | `ProfileAvatar.kt` | s20 | pick avatar |
| `rememberUpdatedState` | `LandingScreen.kt` | (codelab) | splash + test flip |
| One-time events (Channel) | `ProfileViewModel.kt`, `TasksListViewModel.kt` | s21 | save / undo (Live Edit #3) |
| `StateRestorationTester` | `TaskRowRestorationTest.kt` | s22 | green→red |
| VM logic tests (Turbine) | `*ViewModelTest.kt` | — | run suite |

---

## 9 · Flex notes

**If short (~10 min):** keep Live Edits #1 & #2, the Settings fan-out, and one test flip. Cut the Layout
Inspector deep-dive, the compiler report, Live Edit #3 staging (narrate instead), and the advanced tour
(just name `snapshotFlow`/`produceState` in passing).

**If extended (~25 min):** stage Live Edit #3 fully; run **both** test flips live; do the Layout Inspector
counts + the compiler stability report; walk an end-to-end CRUD (add → edit → swipe-delete → undo); show
the dark theme + edge-to-edge insets and the `DesignSystemGallery` preview.

**Order is the deck's order** — if you jump around, the coverage matrix (§8) is your map back.
