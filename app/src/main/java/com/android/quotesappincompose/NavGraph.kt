package com.android.quotesappincompose

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.quotesappincompose.screens.AddQuotesScreen
import com.android.quotesappincompose.screens.MainScreen
import com.android.quotesappincompose.screens.UpdateQuotesScreen

@Composable
fun NavGraph(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = MAIN_SCREEN
    ) {
        composable<MAIN_SCREEN> {
            MainScreen(navHostController)
        }
        composable<ADD_QUOTES_SCREEN> {
            AddQuotesScreen(navHostController)
        }
        composable<UPDATE_QUOTES_SCREEN> {
            UpdateQuotesScreen(navHostController, viewModel(), it)
        }
    }
}