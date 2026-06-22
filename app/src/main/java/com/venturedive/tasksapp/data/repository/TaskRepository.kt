package com.venturedive.tasksapp.data.repository

import com.venturedive.tasksapp.core.di.IoDispatcher
import com.venturedive.tasksapp.data.local.db.TaskDao
import com.venturedive.tasksapp.data.local.db.TaskEntity
import com.venturedive.tasksapp.domain.model.Priority
import com.venturedive.tasksapp.domain.model.Task
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Source of truth for tasks; exposes durable data as a [Flow] for ViewModels to collect. */
interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    fun observeTask(id: Long): Flow<Task?>
    suspend fun addTask(title: String, description: String, priority: Priority): Long
    suspend fun updateTask(task: Task)
    suspend fun setCompleted(id: Long, completed: Boolean)
    suspend fun deleteTask(id: Long)
    suspend fun restoreTask(task: Task)
}

class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) : TaskRepository {

    override fun observeTasks(): Flow<List<Task>> =
        dao.observeAll().map { entities -> entities.map(TaskEntity::toDomain) }

    override fun observeTask(id: Long): Flow<Task?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun addTask(title: String, description: String, priority: Priority): Long =
        withContext(io) {
            dao.insert(
                TaskEntity(
                    title = title,
                    description = description,
                    priority = priority,
                    createdAt = System.currentTimeMillis(),
                ),
            )
        }

    override suspend fun updateTask(task: Task) = withContext(io) { dao.update(task.toEntity()) }

    override suspend fun setCompleted(id: Long, completed: Boolean) =
        withContext(io) { dao.setCompleted(id, completed) }

    override suspend fun deleteTask(id: Long) = withContext(io) { dao.deleteById(id) }

    override suspend fun restoreTask(task: Task) {
        withContext(io) { dao.insert(task.toEntity()) }
    }
}

private fun TaskEntity.toDomain() = Task(id, title, description, isCompleted, priority, createdAt)
private fun Task.toEntity() = TaskEntity(id, title, description, isCompleted, priority, createdAt)
