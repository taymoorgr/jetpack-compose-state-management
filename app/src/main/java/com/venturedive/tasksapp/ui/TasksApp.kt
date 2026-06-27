package com.venturedive.tasksapp.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.venturedive.tasksapp.feature.profile.ProfileScreen
import com.venturedive.tasksapp.feature.settings.SettingsScreen
import com.venturedive.tasksapp.feature.taskedit.TaskEditScreen
import com.venturedive.tasksapp.feature.tasks.TasksScreen
import com.venturedive.tasksapp.navigation.NavBarDestination
import com.venturedive.tasksapp.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentNavBarDestination = NavBarDestination.entries.firstOrNull { destination ->
        backStackEntry?.destination?.hierarchy?.any {
            it.hasRoute(destination.route::class)
        } == true
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (currentNavBarDestination != null) {
                CenterAlignedTopAppBar(
                    title = { Text(currentNavBarDestination.label) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            if (currentNavBarDestination != null) {
                TasksAppNavigationBar(
                    navBarDestination = currentNavBarDestination,
                    onNavigate = navController::setNavBarDestination
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.TasksRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Route.TasksRoute> {
                TasksScreen(
                    onAddTask = { navController.navigate(Route.TaskEditRoute()) },
                    onEditTask = { id -> navController.navigate(Route.TaskEditRoute(taskId = id)) }
                )
            }
            composable<Route.ProfileRoute> { ProfileScreen() }
            composable<Route.SettingsRoute> { SettingsScreen() }
            composable<Route.TaskEditRoute> {
                TaskEditScreen(onDone = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun TasksAppNavigationBar(
    navBarDestination: NavBarDestination,
    onNavigate: (NavBarDestination) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavBarDestination.entries.forEach { destination ->
            val selected = destination == navBarDestination
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) }
            )
        }
    }
}

private fun NavController.setNavBarDestination(destination: NavBarDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
