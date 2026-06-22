package com.venturedive.tasksapp.core.di

import com.venturedive.tasksapp.data.repository.PreferencesRepository
import com.venturedive.tasksapp.data.repository.PreferencesRepositoryImpl
import com.venturedive.tasksapp.data.repository.ProfileRepository
import com.venturedive.tasksapp.data.repository.ProfileRepositoryImpl
import com.venturedive.tasksapp.data.repository.TaskRepository
import com.venturedive.tasksapp.data.repository.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository
}
