package com.example.news.model.remote

data class Response (
    val status: String?,
    val totalResults: Long?,
    val articles: List<ArticleRemote>?
)
