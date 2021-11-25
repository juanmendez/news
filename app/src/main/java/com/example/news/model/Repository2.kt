package com.example.news.model

import com.example.news.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface abstracting the repository functionality.
 *
 * The Repository should expose suspend functions, or return Channel/Flow objects, depending on
 * the nature of the API. The actual coroutines are then set up in the ViewModel. LiveData gets
 * introduced by the ViewModel, not the Repository.
 */
interface Repository2 {

    /**
     * Retrieves a list of [Article] matching a given [query] and [page]
     * @param query the matching query
     * @param page the matching page
     * @return the [Flow] of [Resource] of the list of matching [Article]
     */
    suspend fun getArticles(query: String, page: Int): Flow<Resource<List<Article>>>

    /**
     * Retrieves a list of [Article] matching "Top Headlines" and [page]
     * @param page the matching page
     * @return the [Flow] of [Resource] of the list of matching [Article]
     */
    suspend fun getTopHeadlines(page: Int): Flow<Resource<List<Article>>>

    /**
     * Deletes all articles matching a given [query]
     * @param query the matching query
     */
    suspend fun deleteArticles(query: String)

    /**
     * Deletes all articles
     */
    suspend fun deleteAllArticles()
}
