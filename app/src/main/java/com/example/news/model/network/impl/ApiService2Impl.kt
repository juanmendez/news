package com.example.news.model.network.impl

import com.example.news.model.network.ApiService2
import com.example.news.model.network.impl.data.ArticlesResponse
import retrofit2.Response

/**
 * Implementation of the [ApiService2]
 */
class ApiService2Impl(
    private val api: Api
) : ApiService2 {

    companion object {
        const val COUNTRY = "us"
    }

    override suspend fun getArticles(query: String, page: Int): Response<ArticlesResponse> {
        return api.getArticles(query, page)
    }

    override suspend fun getTopHeadlines(page: Int): Response<ArticlesResponse> {
        return api.getTopHeadlines(COUNTRY, page)
    }
}
