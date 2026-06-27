package com.venturedive.tasksapp.domain.model

enum class SortOrder { CREATED_DESC, TITLE_ASC, PRIORITY_DESC }

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class UserPreferences(
    val sortOrder: SortOrder = SortOrder.CREATED_DESC,
    val hideCompleted: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
