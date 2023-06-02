package com.example.news.network

import com.example.news.data.Article
import kotlinx.coroutines.flow.Flow

/**
 * Interface abstracting the repository functionality.
 *
 * The Repository should expose suspend functions, or return Channel/Flow objects, depending on
 * the nature of the API. The actual coroutines are then set up in the ViewModel. LiveData gets
 * introduced by the ViewModel, not the Repository.
 *
 * This repository interface returns a flow of data via its methods.
 */
interface Repository {

    /**
     * Retrieves a list of [Article] matching a given [query]
     * @param query the matching query
     * @return the [Flow] of [Resource] of the list of matching [Article]
     */
    suspend fun getArticles(query: String): Flow<List<Article>>
}