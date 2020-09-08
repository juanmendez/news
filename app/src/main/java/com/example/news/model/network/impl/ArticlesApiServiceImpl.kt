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

        // https://newsapi.org
        // claudiu.colteu@gmail.com / abcd1234
        const val API_KEY = "7da5d9626af74c1eab78e5e8aee72b0d"
        const val PAGE_SIZE = 50
        const val PAGE = 1
        const val COUNTRY = "us"

        // use this to see an error thrown to the UI
        //const val API_KEY = "bad-api-key"
    }

    override suspend fun getArticles(query: String): List<Article> {
        val response = handleError(retrofit,
            when (query) {
                "Top Headlines" -> {
                    // synchronous Retrofit call that will be executed inside an IO-scoped coroutine
                    articlesApi.getTopHeadlines(COUNTRY, PAGE, PAGE_SIZE, API_KEY)
                }
                else -> {
                    articlesApi.getArticles(query, PAGE, PAGE_SIZE, API_KEY)
                }
            }
        )
        val networkArticles = response.body()?.articles
        networkArticles?.let {
            return NetworkMapper.networkArticleListToArticleList(query, networkArticles)
        } ?: run {
            throw Exception()
        }
    }
}
