package com.venturedive.tasksapp.feature.tasks

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.venturedive.tasksapp.core.designsystem.theme.TasksAppTheme
import com.venturedive.tasksapp.domain.model.Priority
import com.venturedive.tasksapp.domain.model.Task
import com.venturedive.tasksapp.feature.tasks.components.TaskRow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class TaskRowRestorationTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val description = "This description only shows when the row is expanded"

    @Test
    fun expandedState_survivesActivityRecreation() {
        val restorationTester = StateRestorationTester(composeRule)

        restorationTester.setContent {
            TasksAppTheme {
                TaskRow(
                    task = Task(
                        id = 1,
                        title = "Demo task",
                        description = description,
                        priority = Priority.MEDIUM,
                        createdAt = 1
                    ),
                    onToggleComplete = {},
                    onEdit = {},
                    modifier = Modifier.testTag("row")
                )
            }
        }

        composeRule.onNodeWithText(description).assertDoesNotExist()

        composeRule.onNodeWithTag("row").performClick()
        composeRule.onNodeWithText(description).assertIsDisplayed()

        restorationTester.emulateSavedInstanceStateRestore()

        composeRule.onNodeWithText(description).assertIsDisplayed()
    }
}
