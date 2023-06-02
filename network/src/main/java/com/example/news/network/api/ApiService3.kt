package com.example.news.network.api

import com.example.news.network.api.impl.data.ArticlesResponse
import retrofit2.Response

/**
 * Interface abstracting the api service functionality
 */
interface ApiService3 {

    /**
     * Retrieves the list of articles matching a given query
     * @param query the matching query
     * @param page the matching page
     * @return the matching [ArticlesResponse] wrapped in [Response]
     */
    suspend fun getArticles(query: String, page: Int): Response<ArticlesResponse>
}
