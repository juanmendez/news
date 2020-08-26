package com.example.news.model

import com.example.news.model.cache.ArticlesDaoService
import com.example.news.model.network.ArticlesApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositoryImpl(
    private val articlesApiService: ArticlesApiService,
    private val articlesDaoService: ArticlesDaoService
) : Repository {

    override suspend fun getCachedArticles(query: String): List<Article> {

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

    override suspend fun getArticles(query: String): Flow<List<Article>> = flow {
        val cachedArticlesCount = articlesDaoService.getArticlesCount(query)
        if (cachedArticlesCount > 0) {
            // if there are cached data emit them
            emit(articlesDaoService.getArticles(query))
        }
        // fetch updated data from network
        articlesApiService.getArticles(query)?.let {
            // save them to cache
            articlesDaoService.insertArticles(it)
            // emit them
            emit(articlesDaoService.getArticles(query))
        }
    }
}
