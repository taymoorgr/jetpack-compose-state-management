package com.venturedive.tasksapp.feature.landing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class LandingScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun invokesOnTimeoutAfterDelay() {
        var called = false
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            LandingScreen(onTimeout = { called = true })
        }

        assertFalse(called)
        composeRule.mainClock.advanceTimeBy(2_000)
        composeRule.waitForIdle()
        assertTrue(called)
    }

    @Test
    fun callsLatestOnTimeout_whenCallbackChangesBeforeTimeout() {
        var result = "none"
        val onTimeoutState = mutableStateOf({ result = "stale" })

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            val onTimeout by onTimeoutState
            LandingScreen(onTimeout = onTimeout)
        }

        composeRule.mainClock.advanceTimeBy(500)
        onTimeoutState.value = { result = "latest" }
        composeRule.mainClock.advanceTimeBy(2_000)
        composeRule.waitForIdle()

        // rememberUpdatedState makes the one-shot effect fire the LATEST callback (without it → "stale", red).
        assertEquals("latest", result)
    }
}
