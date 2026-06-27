package com.venturedive.tasksapp.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.venturedive.tasksapp.core.designsystem.theme.TasksAppTheme
import com.venturedive.tasksapp.core.designsystem.theme.spacing

@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(
            horizontal = spacing.md,
            vertical = spacing.sm
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun SectionHeaderPreview() {
    TasksAppTheme {
        SectionHeader(text = "In progress")
    }
}
