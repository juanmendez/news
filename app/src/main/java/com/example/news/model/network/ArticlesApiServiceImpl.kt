package com.example.news.model.network

import com.example.news.model.Article
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// This ArticlesApiService implementation uses Retrofit
class ArticlesApiServiceImpl : ArticlesApiService {

    companion object {

        // https://newsapi.org
        // claudiu.colteu@gmail.com / abcd1234
        const val API_KEY = "7da5d9626af74c1eab78e5e8aee72b0d"

        // Singleton Retrofit API instance
        val INSTANCE: ArticlesRetrofitApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://newsapi.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(ArticlesRetrofitApi::class.java)
        }
    }

    override suspend fun getArticles(query: String): List<Article> {
        val networkArticles = INSTANCE.getArticles(query, API_KEY).articles
        networkArticles?.let {
            return NetworkMapper.networkArticleListToArticleList(query, networkArticles)
        } ?: run {
            throw Exception()
        }
    }
}
