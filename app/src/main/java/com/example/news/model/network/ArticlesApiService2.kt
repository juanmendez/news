package com.example.news.model.network

import com.example.news.model.network.impl.ArticlesResponse
import retrofit2.Response

interface ArticlesApiService2 {
    suspend fun getArticles(query: String): Response<ArticlesResponse>
    suspend fun getHeadlines(): Response<ArticlesResponse>
}
