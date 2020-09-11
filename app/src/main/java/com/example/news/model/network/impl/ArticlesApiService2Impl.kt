package com.example.news.model.network.impl

import com.example.news.model.network.ArticlesApiService2
import retrofit2.Response

// This ArticlesApiService implementation uses Retrofit
class ArticlesApiService2Impl(
    private val articlesApi: ArticlesApi
) : ArticlesApiService2 {

    companion object {
        const val PAGE_SIZE = 50
        const val PAGE = 1
    }

    // executed inside an IO-scoped coroutine, will throw an Exception in case of network error,
    // the Exception is propagated through the Repository to the ViewModel that calls the Repo
    // via coroutines and handled in the ViewModel
    override suspend fun getArticles(query: String): Response<ArticlesResponse> {
        return articlesApi.getArticles(query, PAGE, PAGE_SIZE)
    }
}
