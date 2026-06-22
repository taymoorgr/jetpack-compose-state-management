package com.venturedive.tasksapp.feature.profile

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.venturedive.tasksapp.domain.model.Profile
import com.venturedive.tasksapp.testing.FakeProfileRepository
import com.venturedive.tasksapp.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository =
        FakeProfileRepository(Profile(1, "Ada Lovelace", "ada@example.com", "Engineer"))

    private fun viewModel() = ProfileViewModel(repository)

    @Test
    fun loadsProfileIntoState() = runTest {
        viewModel().uiState.test {
            val state = awaitLoaded()
            assertEquals("Ada Lovelace", state.name)
            assertEquals("ada@example.com", state.email)
            assertTrue(state.canSave)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun blankName_disablesSaveAndSetsError() = runTest {
        val vm = viewModel()
        vm.uiState.test {
            awaitLoaded()
            vm.onNameChange("")
            val state = awaitItem()
            assertFalse(state.canSave)
            assertNotNull(state.validation.nameError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invalidEmail_setsEmailError() = runTest {
        val vm = viewModel()
        vm.uiState.test {
            awaitLoaded()
            vm.onEmailChange("not-an-email")
            assertNotNull(awaitItem().validation.emailError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun save_persistsAndEmitsSavedEvent() = runTest {
        val vm = viewModel()
        vm.uiState.test {
            awaitLoaded()
            cancelAndIgnoreRemainingEvents()
        }
        vm.onNameChange("Grace Hopper")
        vm.events.test {
            vm.onSave()
            assertTrue(awaitItem() is ProfileEvent.Saved)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals("Grace Hopper", repository.current()?.name)
    }
}

private suspend fun ReceiveTurbine<ProfileUiState>.awaitLoaded(): ProfileUiState {
    var state = awaitItem()
    if (state.isLoading) state = awaitItem()
    return state
}
