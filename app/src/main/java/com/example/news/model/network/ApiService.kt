package com.example.news.model.network

import com.example.news.model.Article

/**
 * Interface abstracting the api service functionality
 */
interface ApiService {

    /**
     * Retrieves the list of articles matching a given query
     * @param query the matching query
     * @return the matching list of [Article]
     */
    suspend fun getArticles(query: String): List<Article>
}
