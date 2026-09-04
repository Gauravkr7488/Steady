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
@file:Suppress("AssignedValueIsNeverRead")

package com.example.steady.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.example.steady.SharedViewModel
import com.example.steady.constant.Routes
import com.example.steady.ui.component.button.RoundedOutlineButtonTidy
import com.example.steady.ui.component.dialog.TidyDialog
import com.example.steady.ui.component.menu.OutlinedMenuItem
import com.steady.db.Tag
import com.steady.db.Txn
import kotlinx.coroutines.launch

@Composable
fun AddScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableLongStateOf(0) }
    var txnTagList by remember { mutableStateOf(emptyList<Tag>()) }
    var allTags by remember { mutableStateOf(emptyList<Tag>()) }
    var newTags by remember { mutableStateOf(emptyList<Tag>()) }
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var spentFlag by remember { mutableStateOf(false) }
    var id by remember { mutableLongStateOf(-1) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
        allTags = sharedViewModel.getAllTags()
        spentFlag = sharedViewModel.spentFlag
        id = sharedViewModel.id
        if (id != -1L) {
            val currentTxn = sharedViewModel.getTxnById(id) ?: return@LaunchedEffect
            title = currentTxn.title
            amount = currentTxn.amount
            txnTagList = sharedViewModel.getTags(id)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        val txn = Txn(
                            id = id,
                            title = title,
                            amount = if (spentFlag) -amount else amount,
                            createdAt = System.currentTimeMillis()
                        )
                        val txnId = sharedViewModel.save(txn)
                        txnTagList.forEach {
                            var tagId = it.id
                            if (tagId == 0L) tagId = sharedViewModel.saveTag(it.name)
                            sharedViewModel.addTag(tagId, txnId)
                        }
                        newTags.forEach {
                            if (!txnTagList.contains(it)) sharedViewModel.saveTag(it.name)
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
                .padding(horizontal = 16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        keyboardController?.hide()
                    })
                },
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
                        amount = if (newValue != "") newValue.toLong() else 0
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                label = {
                    val text = if (spentFlag) "Amount Spent" else "Amount Received"
                    Text(text)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()

            )
            TagMenu(
                txnTags = txnTagList,
                availableTagList = allTags + newTags,
                onAdd = { txnTagList = it },
                onCreateNew = {
                    newTags += Tag(0, it)
                },
                allTags = allTags + newTags
            )
        }
    }
}


@Composable
fun TagMenu(
    txnTags: List<Tag>,
    availableTagList: List<Tag>,
    allTags: List<Tag>,
    onAdd: (List<Tag>) -> Unit,
    onCreateNew: (String) -> Unit
) {
    var showViewDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showCrateNewDialog by remember { mutableStateOf(false) }
    OutlinedMenuItem(
        menuName = "Tag",
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        RoundedOutlineButtonTidy(
            text = if (txnTags.isEmpty()) "Add" else "View",
            onClick = { if (txnTags.isEmpty()) showAddDialog = true else showViewDialog = true }
        )
    }
    if (showViewDialog) {
        TidyDialog(
            title = "Tags",
            onDismissRequest = { showViewDialog = false },
            buttons = {
                TextButton(onClick = { showViewDialog = false }) {
                    Text("Close")
                }
                TextButton(onClick = { showAddDialog = true }) {
                    Text("Add")
                }
            }
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .padding(bottom = 5.dp),
            ) {
                items(
                    items = txnTags
                ) {
                    Row { Text(it.name) }
                }
            }
        }
    }
    if (showCrateNewDialog) {
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        var text by remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
        val isInvalid = allTags.any {
            it.name == text
        }
        TidyDialog(
            title = "Create New Tag",
            buttons = {
                TextButton(onClick = {
                    showCrateNewDialog = false
                }) {
                    Text("Cancel")
                }

                TextButton(onClick = {
                    if (!isInvalid) {
                        onCreateNew(text)
                        showCrateNewDialog = false
                    }
                }) {
                    Text("Create")
                }
            },
            onDismissRequest = { showCrateNewDialog = false },
        ) {
            val labelText = if (isInvalid) "duplicate name" else "tag name"
            OutlinedTextField(
                label = { Text(labelText) },
                value = text,
                onValueChange = { text = it },
                isError = isInvalid,
                modifier = Modifier
                    .focusRequester(focusRequester)
            )
        }
    }
    if (showAddDialog) {
        var list: List<Tag> by remember { mutableStateOf(emptyList()) }
        TidyDialog(
            title = "Select Tags",
            onDismissRequest = { showAddDialog = false },
            buttons = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
                if (list.isEmpty()) {
                    TextButton(onClick = {
                        showAddDialog = false
                        showCrateNewDialog = true
                    }) {
                        Text("Create New")
                    }
                } else {
                    TextButton(onClick = {
                        onAdd(list)
                        showAddDialog = false
                    }) {
                        Text("Add")
                    }
                }
            }
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(availableTagList, key = { it.name }) { tag ->
                    val cardColor = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    val border = if (list.contains(tag)) BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    ) else null
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        border = border,
                        colors = cardColor,
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    if (list.contains(tag)) {
                                        list -= tag
                                    } else {
                                        list += tag
                                    }
                                })
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(tag.name)
                        }
                    }
                }
            }
        }
    }
}