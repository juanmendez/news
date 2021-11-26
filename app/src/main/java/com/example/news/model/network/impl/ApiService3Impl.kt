package com.example.news.model.network.impl

import com.example.news.model.network.ApiService3
import com.example.news.model.network.impl.data.ArticlesResponse
import retrofit2.Response

/**
 * Implementation of the [ApiService3]
 */
class ApiService3Impl(
    private val api: Api
) : ApiService3 {

    override suspend fun getArticles(query: String, page: Int): Response<ArticlesResponse> {
        return api.getArticles(query, page)
    }
}
