package com.example.news.model.network.impl.data

/**
 * Network entity model for storing the API error
 */
data class ApiError(
    val status: String? = null,
    val code: String? = null,
    val message: String? = null
)
