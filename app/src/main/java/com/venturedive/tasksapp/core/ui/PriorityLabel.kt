package com.venturedive.tasksapp.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.venturedive.tasksapp.R
import com.venturedive.tasksapp.domain.model.Priority

@Composable
fun Priority.label(): String = when (this) {
    Priority.LOW -> stringResource(R.string.priority_low)
    Priority.MEDIUM -> stringResource(R.string.priority_medium)
    Priority.HIGH -> stringResource(R.string.priority_high)
}
