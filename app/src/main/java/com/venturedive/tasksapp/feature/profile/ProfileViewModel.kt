package com.venturedive.tasksapp.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venturedive.tasksapp.data.repository.ProfileRepository
import com.venturedive.tasksapp.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Private MutableStateFlow, exposed read-only via asStateFlow; updated immutably with update { copy(...) }.
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // One-time events via Channel (not state) so they don't replay on rotation.
    private val eventChannel = Channel<ProfileEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private var loaded: Profile? = null

    init {
        viewModelScope.launch {
            val profile = repository.observeProfile().filterNotNull().first()
            loaded = profile
            _uiState.update {
                it.copy(
                    name = profile.name,
                    email = profile.email,
                    bio = profile.bio,
                    avatarUri = profile.avatarUri,
                    isLoading = false,
                    validation = validate(profile.name, profile.email),
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
        val base = loaded
        if (!state.canSave || base == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.updateProfile(
                base.copy(
                    name = state.name.trim(),
                    email = state.email.trim(),
                    bio = state.bio.trim(),
                    avatarUri = state.avatarUri,
                ),
            )
            _uiState.update { it.copy(isSaving = false) }
            eventChannel.send(ProfileEvent.Saved)
        }
    }
}

private fun validate(name: String, email: String) = ProfileValidation(
    nameError = if (name.isBlank()) NameError.Blank else null,
    emailError = if (email.isNotBlank() && !email.contains("@")) EmailError.Invalid else null,
)
