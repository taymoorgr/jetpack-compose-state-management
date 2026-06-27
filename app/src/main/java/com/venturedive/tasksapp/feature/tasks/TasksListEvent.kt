package com.venturedive.tasksapp.feature.tasks

import com.venturedive.tasksapp.domain.model.Task

sealed interface TasksListEvent {
    data class ShowUndoDelete(val task: Task) : TasksListEvent
}