package com.venturedive.tasksapp.feature.tasks

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.Saver

// @Immutable: ids is only ever replaced, never mutated, so composables taking it can skip.
@Immutable
data class TaskSelection(val ids: Set<Long> = emptySet()) {
    val count: Int get() = ids.size
    val isActive: Boolean get() = ids.isNotEmpty()

    fun toggle(id: Long): TaskSelection =
        copy(ids = if (id in ids) ids - id else ids + id)

    fun clear(): TaskSelection = TaskSelection()

    companion object {
        // Custom Saver: maps selection to a LongArray so rememberSaveable persists it across recreation.
        val Saver: Saver<TaskSelection, LongArray> = Saver(
            save = { it.ids.toLongArray() },
            restore = { TaskSelection(it.toSet()) },
        )
    }
}
