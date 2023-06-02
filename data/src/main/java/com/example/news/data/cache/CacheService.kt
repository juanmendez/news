package com.example.news.data.cache

import com.example.news.data.Article

/**
 * Interface abstracting the cache service functionality
 * This interface used the domain model, [Article], for data. The conversion to and from the cache
 * entity model is implementation specific and therefore needs to be done in the cache service
 * implementation.
 */
interface CacheService {

    /**
     * Inserts an articles into the cache
     * @param article the [Article] to be inserted into the cache
     * @return the primary key of the article in the cache
     */
    suspend fun insertArticle(article: com.example.news.data.Article): Long

    /**
     * Inserts a list of articles into the cache
     * @param articles the list of [Article] to be inserted into the cache
     * @return an array of primary keys for the articles inserted into the cache
     */
    suspend fun insertArticles(articles: List<com.example.news.data.Article>): LongArray

    /**
     * Retrieves the cached articles matching a given query string
     * @param query the query string to match
     * @return the list of cached [Article] matching the query string
     */
    suspend fun getArticles(query: String): List<com.example.news.data.Article>

    /**
     * Retrieves the count of cached articles matching a given query string
     * @param query the query string to match
     * @return the count of cached articles as an [Int] matching the given query string
     */
    suspend fun getArticlesCount(query: String): Int

    /**
     * Deletes all cached articles matching a given query string
     * @param query the query string to match
     * @return the count of cached articles deleted
     */
    suspend fun deleteArticles(query: String): Int

    /**
     * Deletes all cached articles
     */
    suspend fun deleteAllArticles(): Int
}
