package com.venturedive.tasksapp.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venturedive.tasksapp.R
import com.venturedive.tasksapp.core.designsystem.component.SectionHeader
import com.venturedive.tasksapp.core.designsystem.component.TasksAppCard
import com.venturedive.tasksapp.core.designsystem.theme.spacing
import com.venturedive.tasksapp.domain.model.SortOrder
import com.venturedive.tasksapp.domain.model.ThemeMode
import com.venturedive.tasksapp.domain.model.UserPreferences

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    // collectAsStateWithLifecycle at the screen edge: state down, events up.
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    SettingsContent(
        preferences = preferences,
        onSortOrderChange = viewModel::onSortOrderChange,
        onHideCompletedChange = viewModel::onHideCompletedChange,
        onThemeModeChange = viewModel::onThemeModeChange,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    preferences: UserPreferences,
    onSortOrderChange: (SortOrder) -> Unit,
    onHideCompletedChange: (Boolean) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        SectionHeader(stringResource(R.string.settings_appearance))
        TasksAppCard(modifier = Modifier.padding(horizontal = spacing.md)) {
            PreferenceGroup(label = stringResource(R.string.settings_theme)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = preferences.themeMode == mode,
                            onClick = { onThemeModeChange(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index,
                                ThemeMode.entries.size
                            ),
                        ) { Text(mode.label()) }
                    }
                }
            }
        }

        SectionHeader(stringResource(R.string.settings_tasks))
        TasksAppCard(modifier = Modifier.padding(horizontal = spacing.md)) {
            PreferenceGroup(label = stringResource(R.string.settings_sort_order)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SortOrder.entries.forEachIndexed { index, order ->
                        SegmentedButton(
                            selected = preferences.sortOrder == order,
                            onClick = { onSortOrderChange(order) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index,
                                SortOrder.entries.size
                            ),
                        ) { Text(order.label()) }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHideCompletedChange(!preferences.hideCompleted) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.settings_hide_completed),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = preferences.hideCompleted,
                    onCheckedChange = onHideCompletedChange,
                )
            }
        }
    }
}

@Composable
private fun PreferenceGroup(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
    }
}

@Composable
private fun ThemeMode.label(): String = when (this) {
    ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
    ThemeMode.LIGHT -> stringResource(R.string.theme_light)
    ThemeMode.DARK -> stringResource(R.string.theme_dark)
}

@Composable
private fun SortOrder.label(): String = when (this) {
    SortOrder.CREATED_DESC -> stringResource(R.string.sort_newest)
    SortOrder.TITLE_ASC -> stringResource(R.string.sort_title)
    SortOrder.PRIORITY_DESC -> stringResource(R.string.sort_priority)
}
