package com.example.news.model.network.impl

import com.example.news.model.network.ApiService2
import retrofit2.Response

class ApiService2Impl(
    private val api: Api
) : ApiService2 {

    companion object {
        const val PAGE = 1
        const val COUNTRY = "us"
    }

    override suspend fun getArticles(query: String): Response<ArticlesResponse> {
        return api.getArticles(query, PAGE)
    }

    override suspend fun getHeadlines(): Response<ArticlesResponse> {
        return api.getArticles(COUNTRY, PAGE)
    }
}
