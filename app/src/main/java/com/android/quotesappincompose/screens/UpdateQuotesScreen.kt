@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.quotesappincompose.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.android.quotesappincompose.CircleProgressWithOutBackgroundColor
import com.android.quotesappincompose.MAIN_SCREEN
import com.android.quotesappincompose.QuotesViewModel
import com.android.quotesappincompose.UPDATE_QUOTES_SCREEN


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UpdateQuotesScreen(
    navHostController: NavHostController, viewModel: QuotesViewModel = viewModel(),
    navBackStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // get the Passing Arguments
    val args = navBackStackEntry.toRoute<UPDATE_QUOTES_SCREEN>()

    var quote by remember { mutableStateOf(args.quote) }
    var author by remember { mutableStateOf(args.author) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Update Quote")
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (quote.isEmpty() || author.isEmpty()) {
                        Toast.makeText(context, "Inputs Can't be empty", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.UpdateQuotes(
                            quote = quote,
                            author = author,
                            key = args.key,
                            context = context,
                            onUpdate = {
                                navHostController.navigate(MAIN_SCREEN) {
                                    launchSingleTop = true
                                    popUpTo(MAIN_SCREEN) { inclusive = true }
                                }
                            }
                        )
                    }
                }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Icon that Update Quotes")
            }
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize().verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
                value = author,
                onValueChange = {
                    author = it
                },
                label = {
                    Text(text = "Author")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                )
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().height(150.dp).padding(top = 30.dp, start = 20.dp, end = 20.dp),
                value = quote,
                onValueChange = {
                    quote = it
                },
                label = {
                    Text(text = "Quote")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
        }
        if (!viewModel.isDataUpdated.value) {
            CircleProgressWithOutBackgroundColor()
        }
    }
}