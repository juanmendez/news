package com.example.news.model

import com.example.news.mvi.ArticleListViewState
import com.example.news.mvi.DataState
import kotlinx.coroutines.flow.Flow

/**
 * Interface abstracting the repository functionality.
 *
 * The Repository should expose suspend functions, or return Channel/Flow objects, depending on
 * the nature of the API. The actual coroutines are then set up in the ViewModel. LiveData gets
 * introduced by the ViewModel, not the Repository.
 *
 * MVI Architecture: the Repository returns a flow of ViewState wrapped in DataState (to add
 * a loading state and an error state) in response to a StateEvent sent from the View to the
 * ViewModel.
 */
interface Repository3 {

    /**
     * Retrieves a list of [Article] matching a given [query]
     * @param query the matching query
     * @return the [Flow] of [DataState] of [ArticleListViewState]
     */
    suspend fun getArticles(query: String): Flow<DataState<ArticleListViewState>>
}
