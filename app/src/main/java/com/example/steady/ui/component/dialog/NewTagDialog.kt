package com.example.steady.ui.component.dialog

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.steady.db.Tag

@Composable
fun NewTagDialog(
    allTags: List<Tag>,
    onCreateNew: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var text by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    val isInvalid = allTags.any {
        it.name == text.trim()
    }

    TidyDialog(
        title = "Create New Tag",
        buttons = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text("Cancel")
            }

            TextButton(onClick = {
                if (!isInvalid) {
                    onCreateNew(text)
                    onDismiss()
                }
            }) {
                Text("Create")
            }
        },
        onDismissRequest = { onDismiss() },
    ) {
        val labelText = if (isInvalid) "duplicate name" else "tag name"
        OutlinedTextField(
            label = { Text(labelText) },
            value = text,
            onValueChange = { text = it },
            isError = isInvalid,
            singleLine = true,
            modifier = Modifier
                .focusRequester(focusRequester)
        )
    }
}