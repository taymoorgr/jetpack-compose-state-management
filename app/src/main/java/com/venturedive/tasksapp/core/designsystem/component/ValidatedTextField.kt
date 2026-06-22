package com.venturedive.tasksapp.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import com.venturedive.tasksapp.core.designsystem.theme.TasksAppTheme

// UI-logic state holder: bundles field state + logic, created via the rememberValidatedFieldState factory.
@Stable
class ValidatedFieldState {
    var touched by mutableStateOf(false)
        private set

    fun onFocusChanged(focused: Boolean) {
        if (focused) touched = true
    }

    fun showError(error: String?): Boolean = touched && error != null
}

@Composable
fun rememberValidatedFieldState(): ValidatedFieldState = remember { ValidatedFieldState() }

@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    state: ValidatedFieldState = rememberValidatedFieldState(),
) {
    val showError = state.showError(error)
    val supporting: (@Composable () -> Unit)? =
        if (showError && error != null) {
            { Text(error) }
        } else {
            null
        }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = showError,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        supportingText = supporting,
        modifier = modifier.onFocusChanged { state.onFocusChanged(it.isFocused) },
    )
}

@Preview(showBackground = true)
@Composable
private fun ValidatedTextFieldPreview() {
    TasksAppTheme {
        ValidatedTextField(
            value = "",
            onValueChange = {},
            label = "Name",
            error = "Name can't be empty",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
