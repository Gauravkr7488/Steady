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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.steady.Utils
import com.example.steady.ui.component.button.OutlinedDropDownButton
import com.example.steady.ui.component.card.TxnCard
import com.example.steady.ui.component.dialog.TidyDialog
import com.example.steady.viewmodel.TagViewModel
import com.steady.db.Tag
import com.steady.db.Txn

@Composable
fun TagScreen(
    tagViewModel: TagViewModel

) {
    var tag: Tag by remember { mutableStateOf(Tag(0, "")) }
    var txnList: List<Txn> by remember { mutableStateOf(listOf()) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var typeFilter by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        tag = tagViewModel.tag
        txnList = tagViewModel.getTxnList(tag.id)
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showFilterDialog = true },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter"
                )

            }
        },
        modifier = Modifier.fillMaxSize()
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(start = 5.dp, end = 5.dp)
        ) {
            val filteredTxnList: List<Txn> = when (typeFilter) {
                "RECEIVED" -> txnList.filter { it.amount > 0L }
                "SPENT" -> txnList.filter { it.amount < 0L }
                else -> txnList
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                var total = 0L
                filteredTxnList.forEach { if (!it.suspendedStatus) total += it.amount }
                Text("Total")
                Spacer(Modifier.weight(1f))
                Text(total.toString(), style = MaterialTheme.typography.displayMedium)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(1.dp),
                contentPadding = PaddingValues(bottom = 150.dp)
            ) {
                val group = filteredTxnList
                    .sortedByDescending { it.createdAt }
                    .groupBy { Utils.formatDayHeader(it.createdAt) }
                group.forEach { (label, txnData) ->
                    item(key = label) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(txnData, key = { it.id }) {
                        TxnCard(it, listOf())
                    }
                }
            }
            if (showFilterDialog) {
                TidyDialog(
                    title = "Filter",
                    onDismissRequest = { showFilterDialog = false },
                    buttons = {
                        TextButton(onClick = {
                            showFilterDialog = false
                        }) {
                            Text("Close")
                        }
                        TextButton(onClick = {

                        }) {
                            Text("Ok")
                        }
                    }
                ) {
                    val typeList = listOf(
                        "All" to "ALL",
                        "Received" to "RECEIVED",
                        "Spent" to "SPENT"
                    )
                    FilterMenuItems(
                        menuName = "Type",
                        dropDownButtonLabel = if (typeFilter == "") "All" else typeList.first { it.second == typeFilter }.first,
                        menuItems = typeList
                    ) {
                        typeFilter = it
                        showFilterDialog = false
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterMenuItems(
    menuName: String,
    dropDownButtonLabel: String,
    menuItems: List<Pair<String, String>>,
    onFilterChange: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = menuName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(4.dp)
        )
        Box {
            var showDropDownMenu by remember { mutableStateOf(false) }
            OutlinedDropDownButton(
                label = dropDownButtonLabel,
                onClick = { showDropDownMenu = true },
            )
            DropdownMenu(
                onDismissRequest = { showDropDownMenu = false },
                expanded = showDropDownMenu
            ) {
                menuItems.forEach { (label, filter) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = { onFilterChange(filter) }
                    )
                }
            }
        }
    }
}