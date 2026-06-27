package com.venturedive.tasksapp.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.venturedive.tasksapp.data.local.db.ProfileDao
import com.venturedive.tasksapp.data.local.db.SeedData
import com.venturedive.tasksapp.data.local.db.TaskDao
import com.venturedive.tasksapp.data.local.db.TasksAppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        taskDao: Provider<TaskDao>,
        profileDao: Provider<ProfileDao>,
        @ApplicationScope scope: CoroutineScope
    ): TasksAppDatabase =
        Room.databaseBuilder(context, TasksAppDatabase::class.java, "tasksApp.db")
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        scope.launch {
                            taskDao.get().upsertAll(SeedData.tasks(System.currentTimeMillis()))
                            profileDao.get().upsert(SeedData.profile())
                        }
                    }
                }
            )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideTaskDao(database: TasksAppDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideProfileDao(database: TasksAppDatabase): ProfileDao = database.profileDao()
}
