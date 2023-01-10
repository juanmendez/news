package com.example.news.model.network.impl.data

/**
 * Network entity model for storing the articles api response
 */
data class ArticlesResponse(
    val status: String?,
    val totalResults: Long?,
    val articles: List<ArticleNetwork>?
)
