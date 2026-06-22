package com.venturedive.tasksapp.feature.taskedit

sealed interface TaskEditEvent {
    data object Saved : TaskEditEvent
}