package com.venturedive.tasksapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.venturedive.tasksapp.domain.model.SortOrder
import com.venturedive.tasksapp.domain.model.ThemeMode
import com.venturedive.tasksapp.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

interface PreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setSortOrder(order: SortOrder)
    suspend fun setHideCompleted(hide: Boolean)
    suspend fun setThemeMode(mode: ThemeMode)
}

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    private object Keys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    override val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
        .map { prefs ->
            UserPreferences(
                sortOrder = prefs[Keys.SORT_ORDER]?.let(SortOrder::valueOf)
                    ?: SortOrder.CREATED_DESC,
                hideCompleted = prefs[Keys.HIDE_COMPLETED] ?: false,
                themeMode = prefs[Keys.THEME_MODE]?.let(ThemeMode::valueOf) ?: ThemeMode.SYSTEM
            )
        }

    override suspend fun setSortOrder(order: SortOrder) {
        dataStore.edit { it[Keys.SORT_ORDER] = order.name }
    }

    override suspend fun setHideCompleted(hide: Boolean) {
        dataStore.edit { it[Keys.HIDE_COMPLETED] = hide }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }
}
