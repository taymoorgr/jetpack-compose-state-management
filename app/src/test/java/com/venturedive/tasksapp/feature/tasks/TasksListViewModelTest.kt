package com.venturedive.tasksapp.feature.tasks

import app.cash.turbine.test
import com.venturedive.tasksapp.domain.model.Priority
import com.venturedive.tasksapp.domain.model.Task
import com.venturedive.tasksapp.testing.FakePreferencesRepository
import com.venturedive.tasksapp.testing.FakeTaskRepository
import com.venturedive.tasksapp.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TasksListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val taskRepository = FakeTaskRepository()
    private val preferencesRepository = FakePreferencesRepository()

    private fun viewModel() =
        TasksListViewModel(
            preferencesRepository = preferencesRepository,
            taskRepository = taskRepository
        )

    private fun task(
        id: Long,
        title: String,
        completed: Boolean = false,
        createdAt: Long = id,
        priority: Priority = Priority.MEDIUM,
    ) = Task(
        id = id,
        title = title,
        isCompleted = completed,
        createdAt = createdAt,
        priority = priority
    )

    @Test
    fun loadsTasksSortedByCreatedDescending() = runTest {
        taskRepository.setTasks(
            listOf(
                task(1, "Old", createdAt = 1),
                task(2, "New", createdAt = 2)
            )
        )
        viewModel().uiState.test {
            assertEquals(listOf(2L, 1L), awaitLoaded().tasks.map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteRemovesTask_thenUndoRestoresIt() = runTest {
        val gone = task(1, "Gone", createdAt = 1)
        val keep = task(2, "Keep", createdAt = 2)
        taskRepository.setTasks(listOf(gone, keep))
        val vm = viewModel()
        vm.uiState.test {
            assertEquals(listOf(2L, 1L), awaitLoaded().tasks.map { it.id })

            vm.onDelete(gone)
            assertEquals(listOf(2L), awaitItem().tasks.map { it.id })

            vm.onUndoDelete(gone)
            assertEquals(listOf(2L, 1L), awaitItem().tasks.map { it.id })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteEmitsUndoEvent() = runTest {
        val gone = task(1, "Gone")
        taskRepository.setTasks(listOf(gone))
        val vm = viewModel()
        vm.events.test {
            vm.onDelete(gone)
            val event = awaitItem()
            assertTrue(event is TasksListEvent.ShowUndoDelete)
            assertEquals(1L, (event as TasksListEvent.ShowUndoDelete).task.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchFiltersByTitle() = runTest {
        taskRepository.setTasks(listOf(task(1, "Buy milk"), task(2, "Call Sam")))
        val vm = viewModel()
        vm.uiState.test {
            assertEquals(2, awaitLoaded().tasks.size)
            vm.onSearchChange("milk")
            assertEquals(listOf(1L), awaitItem().tasks.map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleCompleteUpdatesTask() = runTest {
        taskRepository.setTasks(listOf(task(1, "A", completed = false)))
        val vm = viewModel()
        vm.uiState.test {
            assertFalse(awaitLoaded().tasks.first().isCompleted)
            vm.onToggleComplete(task(1, "A"), true)
            assertTrue(awaitItem().tasks.first().isCompleted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun hideCompleted_filtersOutCompletedTasks() = runTest {
        taskRepository.setTasks(
            listOf(task(1, "Active", completed = false), task(2, "Done", completed = true)),
        )
        val vm = viewModel()
        vm.uiState.test {
            assertEquals(2, awaitLoaded().tasks.size)
            // Changing the preference re-emits through the ViewModel's combine and re-filters the list.
            preferencesRepository.setHideCompleted(true)
            var state = awaitItem()
            while (state.tasks.size != 1) state = awaitItem()
            assertEquals(listOf(1L), state.tasks.map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun selectingPriorityFilter_showsOnlyThatPriority() = runTest {
        taskRepository.setTasks(
            listOf(
                task(1, "Low one", priority = Priority.LOW),
                task(2, "High one", priority = Priority.HIGH),
                task(3, "High two", priority = Priority.HIGH),
            ),
        )
        val vm = viewModel()
        vm.uiState.test {
            assertEquals(3, awaitLoaded().tasks.size)
            // Selecting a filter pushes a new value into combine, re-narrowing the visible list.
            vm.onFilterChange(PriorityFilter.HIGH)
            var state = awaitItem()
            while (state.selectedFilter != PriorityFilter.HIGH) state = awaitItem()
            assertEquals(listOf(3L, 2L), state.tasks.map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun counts_areDerivedPerPriority_independentOfSelection() = runTest {
        taskRepository.setTasks(
            listOf(
                task(1, "a", priority = Priority.LOW),
                task(2, "b", priority = Priority.HIGH),
                task(3, "c", priority = Priority.HIGH),
            ),
        )
        val vm = viewModel()
        vm.uiState.test {
            val counts = awaitLoaded().counts
            assertEquals(3, counts.all)
            assertEquals(1, counts.low)
            assertEquals(0, counts.medium)
            assertEquals(2, counts.high)
            // Counts describe the full matched set, so they stay stable when a filter is selected.
            vm.onFilterChange(PriorityFilter.LOW)
            var state = awaitItem()
            while (state.selectedFilter != PriorityFilter.LOW) state = awaitItem()
            assertEquals(3, state.counts.all)
            assertEquals(2, state.counts.high)
            assertEquals(listOf(1L), state.tasks.map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private suspend fun app.cash.turbine.ReceiveTurbine<TasksListUiState>.awaitLoaded(): TasksListUiState {
    var state = awaitItem()
    if (state.isLoading) state = awaitItem()
    return state
}
