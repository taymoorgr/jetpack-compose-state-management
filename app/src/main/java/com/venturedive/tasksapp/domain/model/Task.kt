package com.venturedive.tasksapp.domain.model

enum class Priority { LOW, MEDIUM, HIGH }

data class Task(
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val createdAt: Long = 0L
)
