package com.example.news.model.network

import com.example.news.model.Article

// abstracts Api functionality so that it can be mocked in Unit Tests
interface ArticlesApiService {
    suspend fun getArticles(query: String): List<Article>
}