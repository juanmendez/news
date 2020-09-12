package com.example.news.model.network.impl

import com.example.news.model.network.ApiService2
import retrofit2.Response

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
