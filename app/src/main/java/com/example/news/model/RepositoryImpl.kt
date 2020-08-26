package com.example.news.model

import com.example.news.model.cache.ArticlesDaoService
import com.example.news.model.network.ArticlesApiService

class RepositoryImpl(
    private val articlesApiService: ArticlesApiService,
    private val articlesDaoService: ArticlesDaoService
) : Repository {

    override suspend fun getArticles(query: String): List<Article> {

        // uncomment to see the UI handling the Repo throwing an exception
        //throw Exception()

        val cachedArticlesCount = articlesDaoService.getArticlesCount(query)
        if (cachedArticlesCount == 0) {
            // fetch articles from network
            articlesApiService.getArticles(query)?.let {
                // cache them
                articlesDaoService.insertArticles(it)
                // return them
                return articlesDaoService.getArticles(query)
            }
        } else {
            // return cached articles
            return articlesDaoService.getArticles(query)
        }
    }
}
