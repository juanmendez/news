package com.example.news.model

import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getCachedArticles(query: String): List<Article>
    suspend fun getArticles(query: String): Flow<List<Article>>
}
