/*
 * Copyright (C) 2026  Gaurav Kumar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.example.steady.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.example.steady.SharedViewModel
import com.example.steady.constant.Routes
import com.steady.db.Tag
import kotlinx.coroutines.launch

@Composable
fun AddScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableLongStateOf(0) }
    var showCreateNewTag by remember { mutableStateOf(false) }
    var showAddTag by remember { mutableStateOf(false) }
    var txnTagList by remember { mutableStateOf(emptyList<Tag>()) }
    var availableTagList by remember { mutableStateOf(emptyList<Tag>()) }
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        val id = sharedViewModel.save(title, amount)
                        txnTagList.forEach {
                            sharedViewModel.addTag(it.id, id)
                        }
                        navController.navigate(
                            Routes.HOME,
                            navOptions = navOptions {
                                popUpTo(Routes.HOME) { inclusive = true }
                                launchSingleTop = true
                            }
                        )
                    }
                },
                modifier = Modifier.size(80.dp)

            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save"
                )
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)

        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                label = { Text(("Title")) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
            OutlinedTextField(
                value = if (amount == 0L) "" else amount.toString(),
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        amount = newValue.toLong()
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                label = { Text(("Amount")) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()

            )
            TextButton(onClick = {
                showAddTag = true
            }) {
                Text("Add Tag")
            }
            TextButton(onClick = {
                showCreateNewTag = true
            }) {
                Text("Create New")
            }
            Text("Tag count = ${txnTagList.size}")
            if (showCreateNewTag) {
                var text by remember { mutableStateOf("") }
                TidyDialog(
                    title = "Create New Tag",
                    buttons = {
                        TextButton(onClick = {
                            sharedViewModel.createNewTag(text)
                        }) {
                            Text("Create")
                        }
                        TextButton(onClick = {
                            showCreateNewTag = false
                        }) {
                            Text("Cancel")
                        }
                    },
                    onDismissRequest = { showCreateNewTag = false },
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it }
                    )
                }
            }
            if (showAddTag) {
                val list = mutableListOf<Tag>()
                LaunchedEffect(Unit) {
                    availableTagList = sharedViewModel.getAllTags()
                }
                TidyDialog(
                    title = "Add Tag",
                    onDismissRequest = { showAddTag = false },
                    buttons = {

                        TextButton(onClick = {
                            txnTagList = list
                            showAddTag = false
                        }) {
                            Text("Add")
                        }
                        TextButton(onClick = {
                            list.clear()
                            showAddTag = false
                        }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    LazyColumn {
                        items(availableTagList, key = { it.id }) { tag ->
                            Row(
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures(onTap = {
                                            list += tag
                                        })
                                    }) {
                                Text(tag.name)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TidyDialog(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    buttons: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit

) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(8.dp)
                )
                content()
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.Right
                ) {
                    buttons()
                }
            }
        }
    }
}