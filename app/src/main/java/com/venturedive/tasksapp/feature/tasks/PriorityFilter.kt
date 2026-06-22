package com.venturedive.tasksapp.feature.tasks

import com.venturedive.tasksapp.domain.model.Priority

enum class PriorityFilter { ALL, LOW, MEDIUM, HIGH }

val PriorityFilter.priority: Priority?
    get() = when (this) {
        PriorityFilter.ALL -> null
        PriorityFilter.LOW -> Priority.LOW
        PriorityFilter.MEDIUM -> Priority.MEDIUM
        PriorityFilter.HIGH -> Priority.HIGH
    }
