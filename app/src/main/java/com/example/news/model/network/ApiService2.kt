package com.example.news.model.network

import com.example.news.model.network.impl.ArticlesResponse
import retrofit2.Response

interface ApiService2 {
    suspend fun getArticles(query: String, page: Int): Response<ArticlesResponse>
    suspend fun getTopHeadlines(page: Int): Response<ArticlesResponse>
}
