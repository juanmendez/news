package com.example.news.model.network

import com.example.news.model.network.impl.ArticlesResponse
import retrofit2.Response

// abstracts Api functionality so that it can be mocked in Unit Tests
interface ArticlesApiService2 {
    suspend fun getArticles(query: String): Response<ArticlesResponse>
}
