package com.venturedive.tasksapp.feature.tasks

import com.venturedive.tasksapp.domain.model.Task

// One-time events (consumed once), modeled as events not state.
sealed interface TasksListEvent {
    data class ShowUndoDelete(val task: Task) : TasksListEvent
}