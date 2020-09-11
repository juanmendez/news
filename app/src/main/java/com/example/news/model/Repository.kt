package com.example.news.model

import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getArticles(query: String): Flow<List<Article>>
}
