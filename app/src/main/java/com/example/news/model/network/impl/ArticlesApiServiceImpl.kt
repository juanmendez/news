package com.example.news.model.network.impl

import com.example.news.model.Article
import com.example.news.model.network.ArticlesApiService
import retrofit2.Retrofit

// This ArticlesApiService implementation uses Retrofit
class ArticlesApiServiceImpl(
    private val retrofit: Retrofit,
    private val articlesApi: ArticlesApi
) : ArticlesApiService {

    companion object {
        const val PAGE_SIZE = 50
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
                articlesApi.getTopHeadlines(COUNTRY, PAGE, PAGE_SIZE)
            }
            else -> {
                articlesApi.getArticles(query, PAGE, PAGE_SIZE)
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
