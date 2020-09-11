package com.example.news.model.network.impl

import com.example.news.model.network.ArticlesApiService2
import retrofit2.Response

class ArticlesApiService2Impl(
    private val articlesApi: ArticlesApi
) : ArticlesApiService2 {

    companion object {
        const val PAGE_SIZE = 50
        const val PAGE = 1
        const val COUNTRY = "us"
    }

    override suspend fun getArticles(query: String): Response<ArticlesResponse> {
        return articlesApi.getArticles(query, PAGE, PAGE_SIZE)
    }

    override suspend fun getHeadlines(): Response<ArticlesResponse> {
        return articlesApi.getArticles(COUNTRY, PAGE, PAGE_SIZE
        )
    }
}
