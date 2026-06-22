package com.venturedive.tasksapp.testing

import com.venturedive.tasksapp.data.repository.TaskRepository
import com.venturedive.tasksapp.domain.model.Priority
import com.venturedive.tasksapp.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeTaskRepository : TaskRepository {

    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())

    fun setTasks(tasks: List<Task>) {
        tasksFlow.value = tasks
    }

    fun tasksNow(): List<Task> = tasksFlow.value

    override fun observeTasks(): Flow<List<Task>> = tasksFlow

    override fun observeTask(id: Long): Flow<Task?> =
        tasksFlow.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun addTask(title: String, description: String, priority: Priority): Long {
        val id = (tasksFlow.value.maxOfOrNull { it.id } ?: 0L) + 1L
        tasksFlow.update {
            it + Task(
                id = id,
                title = title,
                description = description,
                priority = priority,
                createdAt = id
            )
        }
        return id
    }

    override suspend fun updateTask(task: Task) {
        tasksFlow.update { list -> list.map { if (it.id == task.id) task else it } }
    }

    override suspend fun setCompleted(id: Long, completed: Boolean) {
        tasksFlow.update { list -> list.map { if (it.id == id) it.copy(isCompleted = completed) else it } }
    }

    override suspend fun deleteTask(id: Long) {
        tasksFlow.update { list -> list.filterNot { it.id == id } }
    }

    override suspend fun restoreTask(task: Task) {
        tasksFlow.update { it + task }
    }
}
