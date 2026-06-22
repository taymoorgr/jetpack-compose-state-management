package com.venturedive.tasksapp.feature.tasks

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.venturedive.tasksapp.domain.model.Priority
import com.venturedive.tasksapp.domain.model.Task
import com.venturedive.tasksapp.feature.taskedit.TaskEditEvent
import com.venturedive.tasksapp.feature.taskedit.TaskEditViewModel
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
class TaskEditViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeTaskRepository()

    @Test
    fun addMode_savePersistsNewTaskAndEmitsSaved() = runTest {
        repository.setTasks(emptyList())
        val viewModel = TaskEditViewModel(repository, SavedStateHandle())

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isEditing)
            assertFalse(state.canSave)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.onTitleChange("Write tests")
        viewModel.events.test {
            viewModel.onSave()
            assertTrue(awaitItem() is TaskEditEvent.Saved)
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(repository.tasksNow().any { it.title == "Write tests" })
    }

    @Test
    fun editMode_loadsExistingTaskFromSavedStateHandle() = runTest {
        repository.setTasks(
            listOf(
                Task(
                    id = 7,
                    title = "Existing",
                    description = "Some details",
                    priority = Priority.HIGH,
                    createdAt = 1
                )
            ),
        )
        // SavedStateHandle exposes the nav arg by key "taskId"; the ViewModel reads it to load edit state.
        val viewModel = TaskEditViewModel(repository, SavedStateHandle(mapOf("taskId" to 7L)))

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.title.isEmpty()) state = awaitItem()
            assertTrue(state.isEditing)
            assertEquals("Existing", state.title)
            assertEquals(Priority.HIGH, state.priority)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
