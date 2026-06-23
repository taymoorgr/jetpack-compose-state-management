package com.venturedive.tasksapp.feature.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venturedive.tasksapp.R
import com.venturedive.tasksapp.core.designsystem.component.EmptyState
import com.venturedive.tasksapp.core.designsystem.component.LoadingState
import com.venturedive.tasksapp.core.designsystem.theme.spacing
import com.venturedive.tasksapp.domain.model.Task
import com.venturedive.tasksapp.feature.tasks.components.PriorityFilterRow
import com.venturedive.tasksapp.feature.tasks.components.SelectionBar
import com.venturedive.tasksapp.feature.tasks.components.TaskRow
import com.venturedive.tasksapp.feature.tasks.components.TaskSearchField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

// collectAsStateWithLifecycle: collect UiState at the edge; consume one-time events here too.
@Composable
fun TasksScreen(
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasksListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val deletedMessage = stringResource(R.string.task_deleted)
    val undoLabel = stringResource(R.string.action_undo)

    // Selection is transient UI state, so it's held here via rememberSaveable + the custom Saver
    // (TaskSelection.Saver) - not in the ViewModel.
    var selection by rememberSaveable(stateSaver = TaskSelection.Saver) {
        mutableStateOf(
            TaskSelection()
        )
    }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TasksListEvent.ShowUndoDelete -> {
                    val result = snackbarHostState.showSnackbar(
                        message = deletedMessage,
                        actionLabel = undoLabel,
                        withDismissAction = true,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onUndoDelete(event.task)
                    }
                }
            }
        }
    }

    TasksContent(
        state = state,
        snackbarHostState = snackbarHostState,
        selection = selection,
        onSearchChange = viewModel::onSearchChange,
        onFilterChange = viewModel::onFilterChange,
        onToggleComplete = viewModel::onToggleComplete,
        onDelete = viewModel::onDelete,
        onToggleSelect = { id -> selection = selection.toggle(id) },
        onClearSelection = { selection = selection.clear() },
        onBulkComplete = {
            viewModel.onBulkComplete(selection.ids)
            selection = selection.clear()
        },
        onBulkDeleteRequest = { confirmDelete = true },
        onAddTask = onAddTask,
        onEditTask = onEditTask,
        modifier = modifier,
    )

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.bulk_delete_title)) },
            text = { Text(stringResource(R.string.bulk_delete_message, selection.count)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onBulkDelete(selection.ids)
                        selection = selection.clear()
                        confirmDelete = false
                    },
                ) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

// Stateless Content (UDF): state down, events up - no ViewModel passed down.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksContent(
    state: TasksListUiState,
    snackbarHostState: SnackbarHostState,
    selection: TaskSelection,
    onSearchChange: (String) -> Unit,
    onFilterChange: (PriorityFilter) -> Unit,
    onToggleComplete: (Task, Boolean) -> Unit,
    onDelete: (Task) -> Unit,
    onToggleSelect: (Long) -> Unit,
    onClearSelection: () -> Unit,
    onBulkComplete: () -> Unit,
    onBulkDeleteRequest: () -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    // derivedStateOf: readers recompose only when the boolean flips, not on every scroll tick.
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                AnimatedVisibility(visible = showScrollToTop) {
                    SmallFloatingActionButton(
                        onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    ) {
                        Icon(Icons.Rounded.ArrowUpward, contentDescription = "Scroll to top")
                    }
                }
                FloatingActionButton(onClick = onAddTask) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add task")
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (selection.isActive) {
                SelectionBar(
                    count = selection.count,
                    onComplete = onBulkComplete,
                    onDelete = onBulkDeleteRequest,
                    onClose = onClearSelection,
                    modifier = Modifier.padding(
                        horizontal = spacing.md,
                        vertical = spacing.sm,
                    ),
                )
            } else {
                PriorityFilterRow(
                    counts = state.counts,
                    selected = state.selectedFilter,
                    onSelect = onFilterChange,
                    modifier = Modifier.padding(
                        horizontal = spacing.md,
                        vertical = spacing.sm,
                    ),
                )
                TaskSearchField(
                    onSearch = onSearchChange,
                    modifier = Modifier.padding(
                        horizontal = spacing.md,
                        vertical = spacing.sm,
                    ),
                )
            }

            when {
                state.isLoading -> LoadingState()
                state.isEmpty -> EmptyState(
                    icon = Icons.Rounded.Checklist,
                    title = "No tasks",
                    description = "Tap + to add your first task.",
                )

                else -> TaskList(
                    tasks = state.tasks,
                    listState = listState,
                    selection = selection,
                    onToggleComplete = onToggleComplete,
                    onDelete = onDelete,
                    onToggleSelect = onToggleSelect,
                    onEditTask = onEditTask,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskList(
    tasks: ImmutableList<Task>,
    listState: LazyListState,
    selection: TaskSelection,
    onToggleComplete: (Task, Boolean) -> Unit,
    onDelete: (Task) -> Unit,
    onToggleSelect: (Long) -> Unit,
    onEditTask: (Long) -> Unit,
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            start = spacing.md,
            end = spacing.md,
            top = spacing.sm,
            bottom = 96.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
        modifier = Modifier.fillMaxSize(),
    ) {
        // Stable key = item identity (not position): preserves per-item state across reorder.
        items(items = tasks, key = { it.id }) { task ->
            val dismissState = rememberSwipeToDismissBoxState()
            LaunchedEffect(dismissState.currentValue) {
                if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                    onDelete(task)
                    // Stable key means an undone row reuses this state holder; reset it so the
                    // restored "dismissed" value doesn't immediately re-fire delete.
                    dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                }
            }
            SwipeToDismissBox(
                state = dismissState,
                modifier = Modifier.animateItem(),
                enableDismissFromStartToEnd = false,
                enableDismissFromEndToStart = !selection.isActive,
                backgroundContent = {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = spacing.lg),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                    }
                },
            ) {
                TaskRow(
                    task = task,
                    onToggleComplete = { completed -> onToggleComplete(task, completed) },
                    onEdit = { onEditTask(task.id) },
                    selected = task.id in selection.ids,
                    selectionActive = selection.isActive,
                    onToggleSelect = { onToggleSelect(task.id) },
                )
            }
        }
    }
}
