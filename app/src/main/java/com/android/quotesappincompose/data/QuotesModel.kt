package com.android.quotesappincompose.data

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

data class QuotesModel(
    val quote: String = "",
    val author: String = "",
    val key: String = ""
) {
    constructor(): this ("", "", "")

    fun DoesMatchSearchQuery(query: String): Boolean {
            val matchingCombination = listOf(
                "$quote",
                "${quote.first()}",
                "$author",
                "${author.first()}"
            )
            return matchingCombination.any {
                it.contains(query, ignoreCase = true)
            }
    }

}
