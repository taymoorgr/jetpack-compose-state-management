package com.venturedive.tasksapp.feature.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venturedive.tasksapp.data.repository.PreferencesRepository
import com.venturedive.tasksapp.data.repository.TaskRepository
import com.venturedive.tasksapp.domain.model.Priority
import com.venturedive.tasksapp.domain.model.SortOrder
import com.venturedive.tasksapp.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksListViewModel @Inject constructor(
    preferencesRepository: PreferencesRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    private val priorityFilter = MutableStateFlow(PriorityFilter.ALL)

    private val eventChannel = Channel<TasksListEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    val uiState: StateFlow<TasksListUiState> = combine(
        taskRepository.observeTasks(),
        preferencesRepository.userPreferences,
        searchQuery,
        priorityFilter
    ) { tasks, preferences, query, filter ->
        val filteredTasks = tasks
            .filter { !preferences.hideCompleted || !it.isCompleted }
            .filter { query.isBlank() || it.title.contains(query, ignoreCase = true) }
        val counts = PriorityCounts(
            all = filteredTasks.size,
            low = filteredTasks.count { it.priority == Priority.LOW },
            medium = filteredTasks.count { it.priority == Priority.MEDIUM },
            high = filteredTasks.count { it.priority == Priority.HIGH }
        )
        val tasksToDisplay = filteredTasks
            .filter { filter == PriorityFilter.ALL || it.priority == filter.priority }
            .sortedWith(comparatorFor(preferences.sortOrder))
        TasksListUiState(
            tasks = tasksToDisplay.toImmutableList(),
            isLoading = false,
            searchQuery = query,
            selectedFilter = filter,
            counts = counts,
            sortOrder = preferences.sortOrder,
            hideCompleted = preferences.hideCompleted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TasksListUiState(isLoading = true)
    )

    fun onSearchChange(query: String) {
        searchQuery.value = query
    }

    fun onFilterChange(filter: PriorityFilter) {
        priorityFilter.value = filter
    }

    fun onToggleComplete(task: Task, completed: Boolean) {
        viewModelScope.launch { taskRepository.setCompleted(task.id, completed) }
    }

    fun onDelete(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task.id)
            eventChannel.send(TasksListEvent.ShowUndoDelete(task))
        }
    }

    fun onUndoDelete(task: Task) {
        viewModelScope.launch { taskRepository.restoreTask(task) }
    }

    fun onBulkComplete(ids: Set<Long>) {
        viewModelScope.launch { ids.forEach { taskRepository.setCompleted(it, true) } }
    }

    fun onBulkDelete(ids: Set<Long>) {
        viewModelScope.launch { ids.forEach { taskRepository.deleteTask(it) } }
    }
}

private fun comparatorFor(order: SortOrder): Comparator<Task> = when (order) {
    SortOrder.CREATED_DESC -> compareByDescending { it.createdAt }
    SortOrder.TITLE_ASC -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.title }
    SortOrder.PRIORITY_DESC ->
        compareByDescending<Task> { it.priority.ordinal }.thenByDescending { it.createdAt }
}
