package com.venturedive.tasksapp.feature.taskedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venturedive.tasksapp.core.designsystem.component.TasksAppCard
import com.venturedive.tasksapp.core.designsystem.theme.spacing
import com.venturedive.tasksapp.core.ui.containerColor
import com.venturedive.tasksapp.core.ui.contentColor
import com.venturedive.tasksapp.core.ui.label
import com.venturedive.tasksapp.domain.model.Priority

@Composable
fun TaskEditScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskEditViewModel = hiltViewModel(),
) {
    // collectAsStateWithLifecycle: observe StateFlow, stop when not started.
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffectSaved(viewModel = viewModel, onDone = onDone)

    TaskEditContent(
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPriorityChange = viewModel::onPriorityChange,
        onSave = viewModel::onSave,
        onBack = onDone,
        modifier = modifier,
    )
}

@Composable
private fun LaunchedEffectSaved(viewModel: TaskEditViewModel, onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                TaskEditEvent.Saved -> onDone()
            }
        }
    }
}

// Stateless Content: state down, events up - UI is f(state).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditContent(
    state: TaskEditUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit task" else "New task") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            TasksAppCard {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    singleLine = true,
                    isError = state.title.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = state.description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Priority", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Priority.entries.forEach { priority ->
                            FilterChip(
                                selected = state.priority == priority,
                                onClick = { onPriorityChange(priority) },
                                label = { Text(priority.label()) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = priority.containerColor(),
                                    selectedLabelColor = priority.contentColor(),
                                ),
                            )
                        }
                    }
                }
            }
            Button(
                onClick = onSave,
                enabled = state.canSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("Save")
                }
            }
        }
    }
}
