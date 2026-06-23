package com.venturedive.tasksapp.feature.tasks

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.Saver

// @Immutable: ids is only ever replaced (copy()), never mutated in place, so the promise holds -
// this keeps composables that take a TaskSelection (TasksContent, TaskList) skippable.
@Immutable
data class TaskSelection(val ids: Set<Long> = emptySet()) {
    val count: Int get() = ids.size
    val isActive: Boolean get() = ids.isNotEmpty()

    fun toggle(id: Long): TaskSelection =
        copy(ids = if (id in ids) ids - id else ids + id)

    fun clear(): TaskSelection = TaskSelection()

    companion object {
        // Custom Saver: serializes the selection to a Bundle-saveable LongArray so rememberSaveable
        // can persist this non-primitive UI state across config changes and process death.
        val Saver: Saver<TaskSelection, LongArray> = Saver(
            save = { it.ids.toLongArray() },
            restore = { TaskSelection(it.toSet()) },
        )
    }
}
