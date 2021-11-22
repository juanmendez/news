package com.example.news.model.network

import com.example.news.model.network.impl.ArticlesResponse
import retrofit2.Response

/**
 * Interface abstracting the api service functionality
 */
interface ApiService3 {
    suspend fun getArticles(query: String): Response<ArticlesResponse>
}
