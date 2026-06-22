package com.venturedive.tasksapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {

    @Serializable
    data object TasksRoute : Route

    @Serializable
    data object ProfileRoute : Route

    @Serializable
    data object SettingsRoute : Route

    @Serializable
    data class TaskEditRoute(val taskId: Long? = null) : Route
}