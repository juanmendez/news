package com.example.news.model.network.impl

data class ArticlesResponse(
    val status: String?,
    val totalResults: Long?,
    val articles: List<ArticleNetwork>?
)
