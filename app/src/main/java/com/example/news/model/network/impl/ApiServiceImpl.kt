package com.example.news.model.network.impl

import com.example.news.model.Article
import com.example.news.model.network.ApiService
import retrofit2.Retrofit

/**
 * Implementation of the [ApiService]
 * The network response is parsed into data. Errors are handled by propagating exceptions to the
 * upper layers: api service -> repository -> view model.
 * In the end the ViewModel catches the exceptions and notifies the UI about the error.
 */
class ApiServiceImpl(
    private val retrofit: Retrofit,
    private val api: Api
) : ApiService {

    companion object {
        const val PAGE = 1
        const val COUNTRY = "us"
    }

    // executed inside an IO-scoped coroutine, will throw an Exception in case of network error,
    // the Exception is propagated through the Repository to the ViewModel (calling the Repository
    // via coroutines) and handled in the ViewModel
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
