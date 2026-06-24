package com.venturedive.tasksapp.feature.tasks

import androidx.compose.runtime.Immutable
import com.venturedive.tasksapp.domain.model.SortOrder
import com.venturedive.tasksapp.domain.model.Task
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// Immutable UiState (@Immutable + ImmutableList): a stable snapshot Compose can skip recomposing.
@Immutable
data class TasksListUiState(
    val tasks: ImmutableList<Task> = persistentListOf(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: PriorityFilter = PriorityFilter.ALL,
    val counts: PriorityCounts = PriorityCounts(),
    val sortOrder: SortOrder = SortOrder.CREATED_DESC,
    val hideCompleted: Boolean = false,
) {
    // Derived property: computed from source fields, never stored.
    val isEmpty: Boolean get() = !isLoading && tasks.isEmpty()
}

// Per-filter counts for the chips: derived in the ViewModel, never stored.
@Immutable
data class PriorityCounts(
    val all: Int = 0,
    val low: Int = 0,
    val medium: Int = 0,
    val high: Int = 0,
) {
    fun forFilter(filter: PriorityFilter): Int = when (filter) {
        PriorityFilter.ALL -> all
        PriorityFilter.LOW -> low
        PriorityFilter.MEDIUM -> medium
        PriorityFilter.HIGH -> high
    }
}
