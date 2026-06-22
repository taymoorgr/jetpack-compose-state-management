package com.venturedive.tasksapp.testing

import com.venturedive.tasksapp.data.repository.PreferencesRepository
import com.venturedive.tasksapp.domain.model.SortOrder
import com.venturedive.tasksapp.domain.model.ThemeMode
import com.venturedive.tasksapp.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakePreferencesRepository : PreferencesRepository {

    private val prefs = MutableStateFlow(UserPreferences())

    override val userPreferences: Flow<UserPreferences> = prefs

    override suspend fun setSortOrder(order: SortOrder) {
        prefs.update { it.copy(sortOrder = order) }
    }

    override suspend fun setHideCompleted(hide: Boolean) {
        prefs.update { it.copy(hideCompleted = hide) }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        prefs.update { it.copy(themeMode = mode) }
    }
}
