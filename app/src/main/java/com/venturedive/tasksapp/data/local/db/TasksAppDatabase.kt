package com.venturedive.tasksapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.venturedive.tasksapp.domain.model.Priority

@Database(
    entities = [TaskEntity::class, ProfileEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class TasksAppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun profileDao(): ProfileDao
}

object SeedData {
    fun tasks(now: Long): List<TaskEntity> = listOf(
        TaskEntity(
            title = "Review onboarding flow updates",
            description = "Validate the latest onboarding screens and ensure all navigation paths work as expected.",
            priority = Priority.MEDIUM,
            createdAt = now,
        ),
        TaskEntity(
            title = "Prepare sprint planning notes",
            description = "Gather completed work items, pending tasks, and discussion points for the upcoming sprint meeting.",
            priority = Priority.LOW,
            createdAt = now - 60_000 * 1,
        ),
        TaskEntity(
            title = "Investigate login issue",
            description = "Analyze authentication failures reported by users and document the root cause.",
            priority = Priority.HIGH,
            createdAt = now - 60_000 * 2,
        ),
        TaskEntity(
            title = "Update API integration layer",
            description = "Refactor network requests to support the latest backend contract changes.",
            priority = Priority.HIGH,
            createdAt = now - 60_000 * 3,
        ),
        TaskEntity(
            title = "Clean up project documentation",
            description = "Remove outdated setup instructions and add missing details for new contributors.",
            priority = Priority.LOW,
            createdAt = now - 60_000 * 4,
        ),
        TaskEntity(
            title = "Optimize image loading performance",
            description = "Review image caching behavior and reduce unnecessary network requests across the app.",
            priority = Priority.MEDIUM,
            createdAt = now - 60_000 * 5,
        ),
        TaskEntity(
            title = "Design dashboard metrics view",
            description = "Create a layout that highlights key statistics and recent activity in a clear manner.",
            priority = Priority.MEDIUM,
            createdAt = now - 60_000 * 6,
        ),
        TaskEntity(
            title = "Verify release candidate build",
            description = "Run regression tests and confirm critical user journeys before deployment.",
            priority = Priority.HIGH,
            createdAt = now - 60_000 * 7,
        ),
        TaskEntity(
            title = "Refactor notification settings screen",
            description = "Simplify state handling and improve readability of the settings implementation.",
            priority = Priority.MEDIUM,
            createdAt = now - 60_000 * 8,
        ),
        TaskEntity(
            title = "Research offline data synchronization",
            description = "Evaluate possible approaches for syncing local changes when connectivity is restored.",
            priority = Priority.LOW,
            createdAt = now - 60_000 * 9,
        )
    )

    fun profile(): ProfileEntity = ProfileEntity(
        name = "Taymoor Ghazanfar",
        email = "taymoor.ghazanfar@venturedive.com",
        bio = "Android engineer at VentureDive. Presenting Jetpack Compose state management.",
    )
}
