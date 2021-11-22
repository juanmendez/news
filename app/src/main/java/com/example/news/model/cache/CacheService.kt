package com.example.news.model.cache

import com.example.news.model.Article

/**
 * Interface abstracting the cache service functionality
 */
interface CacheService {
    suspend fun insertArticle(article: Article): Long

    suspend fun insertArticles(articles: List<Article>): LongArray

    suspend fun getArticles(query: String): List<Article>

    suspend fun getArticlesCount(query: String): Int

    suspend fun deleteArticles(query: String): Int

    suspend fun deleteAllArticles(): Int
}
