package com.venturedive.tasksapp.feature.tasks.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.venturedive.tasksapp.core.designsystem.theme.spacing
import com.venturedive.tasksapp.core.ui.containerColor
import com.venturedive.tasksapp.core.ui.contentColor
import com.venturedive.tasksapp.core.ui.label
import com.venturedive.tasksapp.domain.model.Priority
import com.venturedive.tasksapp.domain.model.Task

private val CompletedGreen = Color(0xFF4CAF50)
private val EditIconContainer = Color(0xFFFFE0B2)
private val EditIconColor = Color(0xFFE65100)

// State categories: completion is business state (hoisted, toggled via callback); `expanded` is
// local UI-element state in rememberSaveable that follows its item via the stable LazyColumn key.
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskRow(
    task: Task,
    onToggleComplete: (Boolean) -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    selectionActive: Boolean = false,
    onToggleSelect: () -> Unit = {},
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val darkTheme = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = when {
            selected -> MaterialTheme.colorScheme.primaryContainer
            darkTheme -> MaterialTheme.colorScheme.surface
            else -> MaterialTheme.colorScheme.surfaceContainerLowest
        },
        tonalElevation = if (darkTheme) 1.dp else 0.dp,
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { if (selectionActive) onToggleSelect() else expanded = !expanded },
                onLongClick = onToggleSelect,
            ),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(task.priority.containerColor()),
            )
            Column(modifier = Modifier.padding(spacing.sm)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selectionActive) {
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (selected) {
                                    Icons.Rounded.CheckCircle
                                } else {
                                    Icons.Rounded.RadioButtonUnchecked
                                },
                                contentDescription = null,
                                tint = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                },
                            )
                        }
                    } else {
                        IconToggleButton(
                            checked = task.isCompleted,
                            onCheckedChange = onToggleComplete,
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) {
                                    Icons.Rounded.CheckCircle
                                } else {
                                    Icons.Rounded.RadioButtonUnchecked
                                },
                                contentDescription =
                                    if (task.isCompleted) "Mark incomplete" else "Mark complete",
                                tint = if (task.isCompleted) CompletedGreen else MaterialTheme.colorScheme.outline,
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = spacing.sm),
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        )
                        PriorityPill(task.priority)
                    }
                    if (!selectionActive) {
                        Surface(
                            onClick = onEdit,
                            shape = CircleShape,
                            color = EditIconContainer,
                            contentColor = EditIconColor,
                            modifier = Modifier.size(30.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Rounded.Edit,
                                    contentDescription = "Edit task",
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = expanded && !selectionActive) {
                    Text(
                        text = task.description.ifBlank { "No description" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            start = spacing.xl,
                            end = spacing.sm,
                            bottom = spacing.sm,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun PriorityPill(priority: Priority) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = priority.containerColor(),
        contentColor = priority.contentColor(),
    ) {
        Text(
            text = priority.label(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(
                horizontal = spacing.sm,
                vertical = 2.dp,
            ),
        )
    }
}
