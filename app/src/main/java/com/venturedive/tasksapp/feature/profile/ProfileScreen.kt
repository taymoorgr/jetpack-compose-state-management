package com.venturedive.tasksapp.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venturedive.tasksapp.R
import com.venturedive.tasksapp.core.designsystem.component.LoadingState
import com.venturedive.tasksapp.core.designsystem.component.TasksAppCard
import com.venturedive.tasksapp.core.designsystem.component.ValidatedTextField
import com.venturedive.tasksapp.core.designsystem.theme.spacing
import com.venturedive.tasksapp.feature.profile.components.ProfileAvatar

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val savedMessage = stringResource(R.string.profile_saved)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileEvent.Saved -> snackbarHostState.showSnackbar(savedMessage)
            }
        }
    }

    ProfileContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onBioChange = viewModel::onBioChange,
        onAvatarChange = viewModel::onAvatarChange,
        onSave = viewModel::onSave,
        modifier = modifier
    )
}

@Composable
fun ProfileContent(
    state: ProfileUiState,
    snackbarHostState: SnackbarHostState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onAvatarChange: (String?) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingState(modifier = Modifier.padding(innerPadding))
        } else {
            val nameError: String? = when (state.validation.nameError) {
                NameError.Blank -> stringResource(R.string.error_name_blank)
                null -> null
            }
            val emailError: String? = when (state.validation.emailError) {
                EmailError.Invalid -> stringResource(R.string.error_email_invalid)
                null -> null
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(
                    name = state.name,
                    email = state.email,
                    avatarUri = state.avatarUri,
                    onAvatarChange = onAvatarChange
                )
                TasksAppCard {
                    Text(
                        text = "Account details",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ValidatedTextField(
                        value = state.name,
                        onValueChange = onNameChange,
                        label = "Name",
                        error = nameError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ValidatedTextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        label = "Email",
                        error = emailError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.bio,
                        onValueChange = onBioChange,
                        label = { Text("Bio") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Button(
                    onClick = onSave,
                    enabled = state.canSave,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    email: String,
    avatarUri: String?,
    onAvatarChange: (String?) -> Unit
) {
    TasksAppCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                ProfileAvatar(
                    avatarUri = avatarUri,
                    onAvatarChange = onAvatarChange
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.PhotoCamera,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
