package com.venturedive.tasksapp.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class PriorityColors(
    val lowContainer: Color,
    val onLowContainer: Color,
    val mediumContainer: Color,
    val onMediumContainer: Color,
    val highContainer: Color,
    val onHighContainer: Color
)

val LightPriorityColors = PriorityColors(
    lowContainer = Color(0xFFD6F2DD),
    onLowContainer = Color(0xFF134D2E),
    mediumContainer = Color(0xFFFCEFC7),
    onMediumContainer = Color(0xFF6A4E12),
    highContainer = Color(0xFFFBD9DE),
    onHighContainer = Color(0xFF7C1D2B)
)

val DarkPriorityColors = PriorityColors(
    lowContainer = Color(0xFF21402D),
    onLowContainer = Color(0xFFB6E6C4),
    mediumContainer = Color(0xFF40361A),
    onMediumContainer = Color(0xFFF4D79A),
    highContainer = Color(0xFF48232C),
    onHighContainer = Color(0xFFF6B7C1)
)

val LocalPriorityColors = staticCompositionLocalOf { LightPriorityColors }

val priorityColors: PriorityColors
    @Composable
    @ReadOnlyComposable
    get() = LocalPriorityColors.current
