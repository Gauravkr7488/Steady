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

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.steady.constant.Routes
import com.example.steady.ui.component.dialog.NewTagDialog
import com.example.steady.viewmodel.TagViewModel
import com.steady.db.Tag


@Composable
fun TagListScreen(
    navController: NavController,
    tagViewModel: TagViewModel
) {
    var tagList: List<Tag> by remember { mutableStateOf(listOf()) }
    var showCrateNewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tagList = tagViewModel.getTagList()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCrateNewDialog = true },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NewLabel,
                    contentDescription = "Create new tag"
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(1.dp),
                contentPadding = PaddingValues(bottom = 150.dp)
            ) {
                items(tagList, key = { it.id }) { tag ->
                    TagCard(
                        tag,
                        onClick = {
                            tagViewModel.tag = tag
                            navController.navigate(Routes.TAG)
                        }
                    )
                }
            }
            if (showCrateNewDialog) {
                NewTagDialog(
                    allTags = tagList,
                    onCreateNew = { tagViewModel.saveTag(it) }
                ) {
                    showCrateNewDialog = false
                }
            }
        }
    }
}

@Composable
fun TagCard(tag: Tag, onClick: (id: Long) -> Unit) {
    val cardColor = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    )
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = cardColor
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onClick(tag.id)
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(tag.name)
        }
    }
}