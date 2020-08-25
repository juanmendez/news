package com.example.news.model.cache

import com.example.news.model.Article

// abstracts Room functionality so that it can be mocked in Unit Tests
interface ArticlesDaoService {
    suspend fun insertArticle(article: Article): Long

    suspend fun insertArticles(articles: List<Article>): LongArray

    suspend fun getArticles(query: String): List<Article>

    suspend fun getArticlesCount(query: String): Int
}