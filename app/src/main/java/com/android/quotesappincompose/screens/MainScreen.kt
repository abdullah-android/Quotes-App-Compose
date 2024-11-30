@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.quotesappincompose.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.quotesappincompose.ADD_QUOTES_SCREEN
import com.android.quotesappincompose.CircleProgressWithBackgroundColor
import com.android.quotesappincompose.CircleProgressWithOutBackgroundColor
import com.android.quotesappincompose.QuotesViewModel
import com.android.quotesappincompose.UPDATE_QUOTES_SCREEN
import com.android.quotesappincompose.data.COPY_AUTHOR
import com.android.quotesappincompose.data.COPY_QUOTE
import com.android.quotesappincompose.data.DELETE
import com.android.quotesappincompose.data.EDIT
import com.android.quotesappincompose.data.QuotesModel
import com.android.quotesappincompose.ui.theme.PurpleGrey40
import com.android.quotesappincompose.ui.theme.SearchBarColorLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class DropDownItem(
    val text: String
)

fun DropDownItemsList() = listOf(
    DropDownItem(text = EDIT),
    DropDownItem(text = DELETE),
    DropDownItem(text = COPY_QUOTE),
    DropDownItem(text = COPY_AUTHOR)
)

fun CopyTextToClipBoard(
    textToCopy: String,
    context: Context
) {
    val clipboardManager = context.getSystemService(ClipboardManager::class.java)!!

    val clipData = ClipData.newPlainText("Copied Text", textToCopy)
    clipboardManager.setPrimaryClip(clipData)
    Toast.makeText(context, "Text Copied", Toast.LENGTH_SHORT).show()
}


@Composable
fun MainScreen(
    navHostController: NavHostController, viewModel: QuotesViewModel = viewModel()
) {
    val quotesList by viewModel.quotesList.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // val isSearching by viewModel.isSearching.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val lazyListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
                 TopAppBar(
                     title = {
                         Text(text = "Quotology Compose")
                     },
                     scrollBehavior = scrollBehavior
                 )
        },
        bottomBar = {
            Material3SearchBar(
                viewModel = viewModel,
                navHostController = navHostController,
                context = context
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navHostController.navigate(ADD_QUOTES_SCREEN)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
            ) {
                items(quotesList) { item ->
                    QuotesListUI(
                        item = item,
                        navHostController = navHostController,
                        viewModel,
                        context = context,
                        key = item.key
                    )
                }
            }

            if (pullToRefreshState.isRefreshing) {
                LaunchedEffect(true) {
                    scope.launch {
                        viewModel.isRefreshing = true
                        delay(2000L)
                        viewModel.isRefreshing = false
                    }
                }
            }

            LaunchedEffect(viewModel.isRefreshing) {
                if (viewModel.isRefreshing) {
                    scope.launch {
                        pullToRefreshState.startRefresh()
                        delay(2000L)
                        viewModel.RefreshQuotes()
                    }
                } else {
                    pullToRefreshState.endRefresh()
                }
            }

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )

            if (viewModel.isFetchingDatainLoading.value) {
                CircleProgressWithBackgroundColor()
            }
            if (!viewModel.isDataDeleted.value) {
                CircleProgressWithOutBackgroundColor()
            }
        }
    }
}

@Composable
fun QuotesListUI(
    item: QuotesModel,
    navHostController: NavHostController,
    viewModel: QuotesViewModel,
    context: Context,
    key: String
) {
    var isContextMenuVisible by remember { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    var itemHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    var isDialogOpen by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(10.dp).onSizeChanged {
            itemHeight = with(density) { it.height.toDp() }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(15.dp)
                .indication(interactionSource = interactionSource, LocalIndication.current)
                .pointerInput(true) {
                detectTapGestures(
                    onLongPress = {
                        isContextMenuVisible = true
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    },
                    onPress = {
                        val press = PressInteraction.Press(it)
                        interactionSource.emit(press)
                        tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(press))
                    }
                )
            }
        ) {
            Text(
                text = item.quote,
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            )
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = item.author,
                style = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        // Drop Down Menu for Edit and Delete the Quotes
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            DropDownItemsList().forEach {dropDownItem ->
                DropdownMenuItem(
                    text = {
                        Text(text = dropDownItem.text)
                    },
                    onClick = {
                        isContextMenuVisible = false
                        // when Click on EDIT item in menu
                        if (dropDownItem.text == EDIT) {
                            navHostController.navigate(UPDATE_QUOTES_SCREEN(
                                quote = item.quote,
                                author = item.author,
                                key = item.key,
                            ))
                        }
                        // when Click on DELETE item in menu
                        if (dropDownItem.text == DELETE) {
                            isDialogOpen = true
                        }
                        // when Click on COPY_QUOTE item in menu
                        if (dropDownItem.text == COPY_QUOTE) {
                            CopyTextToClipBoard(item.quote, context)
                        }
                        // when Click on COPY_AUTHOR item in menu
                        if (dropDownItem.text == COPY_AUTHOR) {
                            CopyTextToClipBoard(item.author, context)
                        }
                    }
                )
            }
        } // end of drop Down Menu
        if (isDialogOpen) {
            Dialog(
                onDismiss = {
                    isDialogOpen = false
                },
                dismissText = "Cancel",
                onConfirm = {
                    isDialogOpen = false
                    viewModel.DeleteQuotes(
                        key = key,
                        context = context
                    )
                },
                confirmText = "Delete",
                dialogTitle = " ${item.author}",
                dialogText = "Are You Sure You Want to Delete this Quote?",
                icon = Icons.Default.Delete
            )
        }
    }
}

@Composable
fun Dialog(
    onDismiss: () -> Unit,
    dismissText: String,
    onConfirm: () -> Unit,
    confirmText: String,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector
) {
    androidx.compose.material3.AlertDialog(
        icon = {
            Icon(imageVector = icon, contentDescription = "Top Icon in the Dialog")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(text = dismissText)
            }
        }
    )
}

@Composable
fun Material3SearchBar(
    viewModel: QuotesViewModel,
    navHostController: NavHostController,
    context: Context
) {
    var searchText by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val searchQuotesList by viewModel.searchQuotesList.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsState()

    SearchBar(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        colors = SearchBarDefaults.colors(
            containerColor = if (isSystemInDarkTheme()) PurpleGrey40 else SearchBarColorLight,
            inputFieldColors = TextFieldDefaults.textFieldColors(
                focusedTextColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            )
        ),
        query = searchText,
        onQueryChange = {
            searchText = it
            viewModel.onSearchTextChange(searchText)
        },
        onSearch = {
            active = false
        },
        active = active,
        onActiveChange = { active = it },
        leadingIcon = { // leadingIcon is show in the Start side of search Bar
            if (!active) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            } else {
                IconButton(onClick = {
                    active = false
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back Icon",
                        tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
            }
        },
        trailingIcon = { // trailingIcon is show in the end side of search Bar
            if (active) {

            }
        },
        placeholder = {
            Text(
                text = "Search",
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(it)
                    .background(color = if (isSystemInDarkTheme()) Color.Unspecified else Color.White)
            ) {
                items(searchQuotesList) { item ->
                    QuotesListUI(
                        item = item,
                        navHostController = navHostController,
                        viewModel = viewModel,
                        context = context,
                        key = item.key
                    )
                }
            }
            if (isSearching) {
                CircleProgressWithBackgroundColor()
            }
        }
    }
}