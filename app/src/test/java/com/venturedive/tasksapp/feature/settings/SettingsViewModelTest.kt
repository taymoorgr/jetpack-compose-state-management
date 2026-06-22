package com.venturedive.tasksapp.feature.settings

import app.cash.turbine.test
import com.venturedive.tasksapp.domain.model.SortOrder
import com.venturedive.tasksapp.domain.model.ThemeMode
import com.venturedive.tasksapp.testing.FakePreferencesRepository
import com.venturedive.tasksapp.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val preferencesRepository = FakePreferencesRepository()

    private fun viewModel() = SettingsViewModel(preferencesRepository)

    @Test
    fun changingSortOrderPersists() = runTest {
        val vm = viewModel()
        vm.preferences.test {
            assertEquals(SortOrder.CREATED_DESC, awaitItem().sortOrder)
            vm.onSortOrderChange(SortOrder.TITLE_ASC)
            assertEquals(SortOrder.TITLE_ASC, awaitItem().sortOrder)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun togglingHideCompletedPersists() = runTest {
        val vm = viewModel()
        vm.preferences.test {
            assertFalse(awaitItem().hideCompleted)
            vm.onHideCompletedChange(true)
            assertTrue(awaitItem().hideCompleted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changingThemePersists() = runTest {
        val vm = viewModel()
        vm.preferences.test {
            assertEquals(ThemeMode.SYSTEM, awaitItem().themeMode)
            vm.onThemeModeChange(ThemeMode.DARK)
            assertEquals(ThemeMode.DARK, awaitItem().themeMode)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
