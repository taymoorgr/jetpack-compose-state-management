package com.venturedive.tasksapp.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venturedive.tasksapp.data.repository.ProfileRepository
import com.venturedive.tasksapp.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events = _events.asSharedFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val profile = repository.observeProfile().filterNotNull().first()
            _uiState.update {
                it.copy(
                    id = profile.id,
                    name = profile.name,
                    email = profile.email,
                    bio = profile.bio,
                    avatarUri = profile.avatarUri,
                    isLoading = false,
                    validation = validate(profile.name, profile.email)
                )
            }
        }
    }

    fun onNameChange(value: String) =
        _uiState.update { it.copy(name = value, validation = validate(value, it.email)) }

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, validation = validate(it.name, value)) }

    fun onBioChange(value: String) = _uiState.update { it.copy(bio = value) }

    fun onAvatarChange(uri: String?) = _uiState.update { it.copy(avatarUri = uri) }

    fun onSave() {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.updateProfile(
                Profile(
                    id = _uiState.value.id,
                    name = state.name.trim(),
                    email = state.email.trim(),
                    bio = state.bio.trim(),
                    avatarUri = state.avatarUri
                )
            )
            _uiState.update { it.copy(isSaving = false) }
            _events.emit(ProfileEvent.Saved)
        }
    }
}

private fun validate(name: String, email: String) = ProfileValidation(
    nameError = if (name.isBlank()) NameError.Blank else null,
    emailError = if (email.isNotBlank() && !email.contains("@")) EmailError.Invalid else null
)
