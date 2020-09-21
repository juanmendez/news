package com.example.news.model

import com.example.news.mvi.ArticleListViewState
import com.example.news.mvi.DataState
import kotlinx.coroutines.flow.Flow

/**
 * MVI Architecture: the Repository returns a flow of ViewState wrapped in DataState
 * (to add a loading state and an error state) in response to a StateEvent sent from
 * the View to the ViewModel.
 */
interface Repository3 {
    suspend fun getArticles(query: String): Flow<DataState<ArticleListViewState>>
}
