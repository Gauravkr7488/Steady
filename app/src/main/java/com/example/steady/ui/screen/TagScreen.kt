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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.steady.Utils
import com.example.steady.ui.component.card.TxnCard
import com.example.steady.viewmodel.TagViewModel
import com.steady.db.Tag
import com.steady.db.Txn
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun TagScreen(
    navController: NavController,
    tagViewModel: TagViewModel

){
    var tag: Tag by remember { mutableStateOf(Tag(0, "")) }
    var txnList: List<Txn> by remember { mutableStateOf(listOf()) }
    LaunchedEffect(Unit) {
        tag = tagViewModel.tag
        txnList = tagViewModel.getTxnList(tag.id)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(start = 5.dp, end = 5.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                val group = txnList
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
        }
    }
}