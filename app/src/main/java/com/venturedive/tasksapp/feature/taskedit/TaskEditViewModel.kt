package com.venturedive.tasksapp.feature.taskedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venturedive.tasksapp.data.repository.TaskRepository
import com.venturedive.tasksapp.domain.model.Priority
import com.venturedive.tasksapp.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TASK_ID_ARG = "taskId"

@HiltViewModel
class TaskEditViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long? = savedStateHandle.get<Long>(TASK_ID_ARG)

    private val _uiState = MutableStateFlow(TaskEditUiState(taskId = taskId))
    val uiState = _uiState.asStateFlow()

    private val eventChannel = Channel<TaskEditEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    init {
        taskId?.let { loadTask(it) }
    }

    private fun loadTask(taskId: Long) {
        viewModelScope.launch {
            repository.observeTask(taskId).first()?.let { task ->
                _uiState.update {
                    it.copy(
                        taskId = task.id,
                        title = task.title,
                        description = task.description,
                        priority = task.priority,
                        isCompleted = task.isCompleted,
                        createdAt = task.createdAt
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onPriorityChange(priority: Priority) = _uiState.update { it.copy(priority = priority) }

    fun onSave() {
        val current = _uiState.value
        if (!current.canSave) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            if (current.taskId == null) {
                repository.addTask(
                    current.title.trim(),
                    current.description.trim(),
                    current.priority
                )
            } else {
                repository.updateTask(
                    Task(
                        id = current.taskId,
                        title = current.title.trim(),
                        description = current.description.trim(),
                        isCompleted = current.isCompleted,
                        priority = current.priority,
                        createdAt = current.createdAt
                    )
                )
            }
            eventChannel.send(TaskEditEvent.Saved)
        }
    }
}
