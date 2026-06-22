package com.venturedive.tasksapp.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.venturedive.tasksapp.core.designsystem.theme.priorityColors
import com.venturedive.tasksapp.domain.model.Priority

@Composable
@ReadOnlyComposable
fun Priority.containerColor(): Color = when (this) {
    Priority.LOW -> priorityColors.lowContainer
    Priority.MEDIUM -> priorityColors.mediumContainer
    Priority.HIGH -> priorityColors.highContainer
}

@Composable
@ReadOnlyComposable
fun Priority.contentColor(): Color = when (this) {
    Priority.LOW -> priorityColors.onLowContainer
    Priority.MEDIUM -> priorityColors.onMediumContainer
    Priority.HIGH -> priorityColors.onHighContainer
}
