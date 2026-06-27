package com.venturedive.tasksapp.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venturedive.tasksapp.data.repository.PreferencesRepository
import com.venturedive.tasksapp.domain.model.SortOrder
import com.venturedive.tasksapp.domain.model.ThemeMode
import com.venturedive.tasksapp.domain.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences())

    fun onSortOrderChange(order: SortOrder) {
        viewModelScope.launch { preferencesRepository.setSortOrder(order) }
    }

    fun onHideCompletedChange(hide: Boolean) {
        viewModelScope.launch { preferencesRepository.setHideCompleted(hide) }
    }

    fun onThemeModeChange(mode: ThemeMode) {
        viewModelScope.launch { preferencesRepository.setThemeMode(mode) }
    }
}
