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

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.steady.BackupService
import com.example.steady.DbOperation
import com.example.steady.SharedViewModel
import com.example.steady.WorkService
import com.example.steady.constant.Routes
import com.example.steady.ui.component.BottomBar
import com.example.steady.viewmodel.BackupViewModel
import com.example.steady.viewmodel.TagViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(dbOperation: DbOperation) {
    val navController = rememberNavController()
    val sharedViewModel = viewModel<SharedViewModel>(
        factory = viewModelFactory {
            initializer {
                SharedViewModel(dbOperation)
            }
        }
    )
    val tagViewModel = viewModel<TagViewModel>(
        factory = viewModelFactory {
            initializer {
                TagViewModel(dbOperation)
            }
        }
    )
    val context = LocalContext.current
    val backupService = BackupService(
        dbOperation,
        context = context
    )
    val workService = WorkService(context)
    val backupViewModel = viewModel<BackupViewModel>(
        factory = viewModelFactory {
            initializer {
                BackupViewModel(
                    backupService = backupService,
                    workService = workService
                )
            }
        }
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val tabs = listOf(Routes.HOME, Routes.TAG_LIST, Routes.BACKUP)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val currentPage = tabs[pagerState.currentPage]
    val scope = rememberCoroutineScope()

    BackHandler(
        enabled = !pagerState.isScrollInProgress && pagerState.currentPage in 1..2
    ) {
        scope.launch {
            pagerState.scrollToPage(0)
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute == Routes.HOME) {
                BottomBar(currentPage, pagerState)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Routes.HOME) {
                Box {
                    HorizontalPager(
                        state = pagerState,
                        beyondViewportPageCount = 2, // keeps all 3 pages alive
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        when (page) {
                            0 -> HomeScreen(navController, sharedViewModel)
                            1 ->  TagListScreen(navController, tagViewModel)
                            2 -> BackupScreen(backupViewModel)
                        }
                    }
                }
            }

//            composable(Routes.HOME) {
//                HomeScreen(navController, sharedViewModel)
//            }

            composable(Routes.ADD) {
                AddScreen(navController, sharedViewModel)
            }

//            composable(Routes.TAG_LIST) {
//                TagListScreen(navController, tagViewModel)
//            }
            composable(Routes.TAG) {
                TagScreen(tagViewModel)
            }
//            composable(Routes.BACKUP) {
//                BackupScreen(backupViewModel)
//            }
        }
    }
}