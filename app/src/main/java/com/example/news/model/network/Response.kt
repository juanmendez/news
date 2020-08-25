package com.example.news.model.network

data class Response (
    val status: String?,
    val totalResults: Long?,
    val articles: List<ArticleNetwork>?
)
