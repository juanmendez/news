package com.example.news.model.network.impl

import com.example.news.model.network.ApiService3
import retrofit2.Response

class ApiService3Impl(
    private val api: Api
) : ApiService3 {
    override suspend fun getArticles(query: String): Response<ArticlesResponse> {
        return api.getArticles(query, 1)
    }
}
