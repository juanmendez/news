package com.example.news.model.network.impl

import com.example.news.model.Article
import com.example.news.model.network.ApiService
import retrofit2.Retrofit

// This ApiService implementation uses Retrofit
class ApiServiceImpl(
    private val retrofit: Retrofit,
    private val api: Api
) : ApiService {

    companion object {
        const val PAGE = 1
        const val COUNTRY = "us"
    }

    // executed inside an IO-scoped coroutine, will throw an Exception in case of network error,
    // the Exception is propagated through the Repository to the ViewModel that calls the Repo
    // via coroutines and handled in the ViewModel
    override suspend fun getArticles(query: String): List<Article> {
        val networkResponse = when (query) {
            "Top Headlines" -> {
                // synchronous Retrofit call
                api.getTopHeadlines(COUNTRY, PAGE)
            }
            else -> {
                api.getArticles(query, PAGE)
            }
        }
        val successResponse = checkResponseThrowError(retrofit, networkResponse)
        val networkArticles = successResponse.body()?.articles
        networkArticles?.let {
            return NetworkMapper.networkArticleListToArticleList(query, networkArticles)
        } ?: run {
            throw Exception()
        }
    }
}
