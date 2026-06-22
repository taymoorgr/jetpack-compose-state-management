package com.venturedive.tasksapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavBarDestination(
    val route: Route,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    TASKS(Route.TasksRoute, "Tasks", Icons.Filled.Checklist, Icons.Outlined.Checklist),
    PROFILE(Route.ProfileRoute, "Profile", Icons.Filled.Person, Icons.Outlined.Person),
    SETTINGS(Route.SettingsRoute, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings),
}
