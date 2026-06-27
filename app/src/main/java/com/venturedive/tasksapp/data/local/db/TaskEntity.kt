package com.venturedive.tasksapp.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.venturedive.tasksapp.domain.model.Priority

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val createdAt: Long
)
