package com.example.news.model.network

import com.example.news.model.Article

/**
 * Interface abstracting the api service functionality
 */
interface ApiService {
    suspend fun getArticles(query: String): List<Article>
}
