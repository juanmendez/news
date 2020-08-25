package com.example.news.model.network

import com.example.news.model.Article
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArticlesApiServiceImpl : ArticlesApiService {

    override suspend fun getArticles(query: String): List<Article> {
        val networkArticles = instance.getArticles(query, API_KEY).articles
        networkArticles?.let {
            return NetworkMapper.networkArticleListToArticleList(query, networkArticles)
        } ?: run {
            return ArrayList()
        }
    }

    companion object {
        const val API_KEY = "7da5d9626af74c1eab78e5e8aee72b0d"
        val instance: ArticlesApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://newsapi.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(ArticlesApi::class.java)
        }
    }
}
