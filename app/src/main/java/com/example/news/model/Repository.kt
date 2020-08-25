package com.example.news.model

interface Repository {
    suspend fun getArticles(query: String): List<Article>
}
