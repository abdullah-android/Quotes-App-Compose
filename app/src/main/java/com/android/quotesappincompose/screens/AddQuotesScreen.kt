@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.quotesappincompose.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.quotesappincompose.CircleProgressWithOutBackgroundColor
import com.android.quotesappincompose.MAIN_SCREEN
import com.android.quotesappincompose.QuotesViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddQuotesScreen(
    navHostController: NavHostController, viewModel: QuotesViewModel = viewModel()
) {
    var quote by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Add Quote")
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
                        viewModel.AddQuotes(
                            quote = quote,
                            author = author,
                            context = context,
                            onAdded = {
                                navHostController.navigate(MAIN_SCREEN) {
                                    launchSingleTop = true
                                    popUpTo(MAIN_SCREEN)//  { inclusive = true }
                                }
                            }
                        )
                        author = ""
                        quote = ""
                    }

                }) {
                Icon(imageVector = Icons.Default.Done, contentDescription = "Done Icon that add Quotes")
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
        if (!viewModel.isDataAdded.value) {
            CircleProgressWithOutBackgroundColor()
        }

    }
}