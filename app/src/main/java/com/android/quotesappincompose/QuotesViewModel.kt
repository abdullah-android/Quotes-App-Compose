package com.android.quotesappincompose

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.quotesappincompose.data.QUOTES_COL
import com.android.quotesappincompose.data.QuotesModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuotesViewModel : ViewModel() {

    private val fireStore = Firebase.firestore

    private var _quotesList = MutableStateFlow<List<QuotesModel>>(emptyList())
    private val _searchText = MutableStateFlow("")
    private var _isSearching = MutableStateFlow(false)

    // var quotesList = _quotesList.asStateFlow()

    var isRefreshing by mutableStateOf(false)

    // loading Variables
    var isFetchingDatainLoading = mutableStateOf(false)
    var isDataAdded = mutableStateOf(true)
    var isDataUpdated = mutableStateOf(true)
    var isDataDeleted = mutableStateOf(true)
    var isSearching = _isSearching.asStateFlow()

    val searchText = _searchText.asStateFlow()
    val quotesList = _quotesList.asStateFlow()

    var searchQuotesList = searchText
        .debounce(500L)
        .onEach { _isSearching.update { true } }
        .combine(_quotesList) { text, quoteList ->
            if (text == "") {
                quoteList
            } else {
                delay(500L)
                quoteList.filter {
                    it.DoesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _quotesList.value
        )

    init {
        GetQuotes()
        RefreshQuotes()
    }


    fun AddQuotes(
        quote: String,
        author: String,
        context: Context,
        onAdded: () -> Unit
    ) {
        val key = fireStore.collection(QUOTES_COL).document().id
        val quotesModel = QuotesModel(
            quote = quote,
            author = author,
            key = key,
        )
        isDataAdded.value = false
        fireStore.collection(QUOTES_COL)
            .document(key)
            .set(quotesModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Data Added Successfully", Toast.LENGTH_SHORT).show()
                    isDataAdded.value = true
                    onAdded()
                } else {
                    Toast.makeText(context, "Failed to Adding Data. Error is: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun GetQuotes() {
        isFetchingDatainLoading.value = true
        viewModelScope.launch {
            delay(2000L)
            fireStore.collection(QUOTES_COL)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        _quotesList.value = value.toObjects()
                        isFetchingDatainLoading.value = false
                    }
                }
        }
    }


    fun RefreshQuotes() {
        isFetchingDatainLoading.value = true
        viewModelScope.launch {
            delay(2000L)
            fireStore.collection(QUOTES_COL)
                .orderBy("key", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        _quotesList.value = value.toObjects()
                        isFetchingDatainLoading.value = false
                    }
                }
        }
    }

    fun UpdateQuotes(
        quote: String,
        author: String,
        key: String,
        context: Context,
        onUpdate: () -> Unit
    ) {
        val quotesModel = hashMapOf(
            "quote" to quote,
            "author" to author
        )
        isDataUpdated.value = false
        fireStore.collection(QUOTES_COL)
            .document(key)
            .update(quotesModel as Map<String, Any>)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Data Updated Successfully", Toast.LENGTH_SHORT).show()
                    isDataUpdated.value = true
                    onUpdate()
                } else {
                    Toast.makeText(context, "Failed to Update Data. Error is: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }


    fun DeleteQuotes(
        key: String,
        context: Context
    ) {
        isDataDeleted.value = false
        fireStore.collection(QUOTES_COL)
            .document(key)
            .delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Data Deleted Successfully", Toast.LENGTH_SHORT).show()
                    isDataDeleted.value = true
                } else {
                    Toast.makeText(context, "Failed to Delete the Data. Error is: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

}