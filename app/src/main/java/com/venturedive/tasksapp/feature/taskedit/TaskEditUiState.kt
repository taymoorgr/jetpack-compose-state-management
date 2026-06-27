package com.venturedive.tasksapp.feature.taskedit

import androidx.compose.runtime.Immutable
import com.venturedive.tasksapp.domain.model.Priority

@Immutable
data class TaskEditUiState(
    val taskId: Long? = null,
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long = 0L,
    val isSaving: Boolean = false
) {
    val isEditing: Boolean get() = taskId != null

    val canSave: Boolean get() = title.isNotBlank() && !isSaving
}