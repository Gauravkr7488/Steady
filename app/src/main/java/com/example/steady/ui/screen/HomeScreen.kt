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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.steady.SharedViewModel
import com.example.steady.constant.Routes
import com.example.steady.ui.component.card.TxnCard
import com.steady.db.Tag
import com.steady.db.Txn

@Composable
fun HomeScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var txnList by remember { mutableStateOf(emptyList<Txn>()) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.TAG_LIST) },
                    modifier = Modifier.size(80.dp)

                ) {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = ""
                    )
                }

                FloatingActionButton(
                    onClick = { navController.navigate(Routes.ADD) },
                    modifier = Modifier.size(80.dp)

                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Create new Task"
                    )
                }
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(start = 5.dp, end = 5.dp)
        ) {
            var data: List<Pair<Txn, List<Tag>>> by remember { mutableStateOf(emptyList()) }
            LaunchedEffect(Unit) {
                txnList = sharedViewModel.getAllTxns()
                data = txnList.map { it to sharedViewModel.getTags(it.id) }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                items(data, key = { it.first.id }) {
                    TxnCard(it.first, it.second)
                }
            }
        }
    }
}