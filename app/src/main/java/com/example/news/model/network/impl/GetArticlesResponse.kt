package com.example.news.model.network.impl

data class GetArticlesResponse (
    val status: String?,
    val totalResults: Long?,
    val articles: List<ArticleNetwork>?
)
