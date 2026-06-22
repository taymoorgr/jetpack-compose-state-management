package com.venturedive.tasksapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.venturedive.tasksapp.core.designsystem.theme.TasksAppTheme
import com.venturedive.tasksapp.domain.model.ThemeMode
import com.venturedive.tasksapp.feature.landing.LandingScreen
import com.venturedive.tasksapp.ui.AppViewModel
import com.venturedive.tasksapp.ui.TasksApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = hiltViewModel()
            val themeMode by appViewModel.themeMode.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val view = LocalView.current
            LaunchedEffect(darkTheme) {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            TasksAppTheme(darkTheme = darkTheme) {
                // rememberSaveable survives configuration changes, so the splash stays dismissed.
                var showLanding by rememberSaveable { mutableStateOf(true) }
                if (showLanding) {
                    LandingScreen(onTimeout = { showLanding = false })
                } else {
                    TasksApp()
                }
            }
        }
    }
}
