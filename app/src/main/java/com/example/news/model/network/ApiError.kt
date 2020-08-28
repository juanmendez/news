package com.example.news.model.network

class ApiError {
    private val statusCode = 0
    private val message: String? = null
    fun status(): Int {
        return statusCode
    }

    fun message(): String? {
        return message
    }
}