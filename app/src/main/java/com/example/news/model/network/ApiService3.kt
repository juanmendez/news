package com.example.news.model.network

import com.example.news.model.network.impl.ArticlesResponse
import retrofit2.Response

/**
 * Interface abstracting the api service functionality
 */
interface ApiService3 {

    /**
     * Retrieves the [ArticlesResponse] wrapped in [Response] matching a given [query]
     * @param query the matching predicate
     * @return the matching [ArticlesResponse] wrapped in [Response]
     */
    suspend fun getArticles(query: String): Response<ArticlesResponse>
}
