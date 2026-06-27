package com.venturedive.tasksapp.feature.profile

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileUiState(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val avatarUri: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val validation: ProfileValidation = ProfileValidation()
) {
    val canSave: Boolean get() = !isLoading && !isSaving && validation.isValid
}

enum class NameError { Blank }

enum class EmailError { Invalid }

@Immutable
data class ProfileValidation(
    val nameError: NameError? = null,
    val emailError: EmailError? = null
) {
    val isValid: Boolean get() = nameError == null && emailError == null
}

