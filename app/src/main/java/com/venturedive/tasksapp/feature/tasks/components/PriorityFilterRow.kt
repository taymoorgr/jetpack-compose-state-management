package com.venturedive.tasksapp.feature.tasks.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.venturedive.tasksapp.R
import com.venturedive.tasksapp.core.designsystem.theme.priorityColors
import com.venturedive.tasksapp.core.designsystem.theme.spacing
import com.venturedive.tasksapp.feature.tasks.PriorityCounts
import com.venturedive.tasksapp.feature.tasks.PriorityFilter

// Stateless filter row (UDF): filter + counts come down, taps go up via onSelect; holds no state.
@Composable
fun PriorityFilterRow(
    counts: PriorityCounts,
    selected: PriorityFilter,
    onSelect: (PriorityFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        PriorityFilter.entries.forEach { filter ->
            PriorityFilterChip(
                label = filter.label(),
                count = counts.forFilter(filter),
                container = filter.containerColor(),
                content = filter.contentColor(),
                selected = filter == selected,
                onClick = { onSelect(filter) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityFilterChip(
    label: String,
    count: Int,
    container: Color,
    content: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = container,
        contentColor = content,
        border = if (selected) BorderStroke(2.dp, content) else null,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = spacing.sm,
                vertical = spacing.md,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = count.toString(), style = MaterialTheme.typography.titleLarge)
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PriorityFilter.label(): String = when (this) {
    PriorityFilter.ALL -> stringResource(R.string.filter_all)
    PriorityFilter.LOW -> stringResource(R.string.priority_low)
    PriorityFilter.MEDIUM -> stringResource(R.string.priority_medium)
    PriorityFilter.HIGH -> stringResource(R.string.priority_high)
}

@Composable
private fun PriorityFilter.containerColor(): Color = when (this) {
    PriorityFilter.ALL -> MaterialTheme.colorScheme.primaryContainer
    PriorityFilter.LOW -> priorityColors.lowContainer
    PriorityFilter.MEDIUM -> priorityColors.mediumContainer
    PriorityFilter.HIGH -> priorityColors.highContainer
}

@Composable
private fun PriorityFilter.contentColor(): Color = when (this) {
    PriorityFilter.ALL -> MaterialTheme.colorScheme.onPrimaryContainer
    PriorityFilter.LOW -> priorityColors.onLowContainer
    PriorityFilter.MEDIUM -> priorityColors.onMediumContainer
    PriorityFilter.HIGH -> priorityColors.onHighContainer
}
