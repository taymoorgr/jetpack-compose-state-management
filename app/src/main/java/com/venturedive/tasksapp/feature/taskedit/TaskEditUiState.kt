package com.venturedive.tasksapp.feature.taskedit

import com.venturedive.tasksapp.domain.model.Priority

// Immutable UiState - single source of truth for the screen.
data class TaskEditUiState(
    val taskId: Long? = null,
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long = 0L,
    val isSaving: Boolean = false,
) {
    val isEditing: Boolean get() = taskId != null

    // Derived state - computed, never stored.
    val canSave: Boolean get() = title.isNotBlank() && !isSaving
}