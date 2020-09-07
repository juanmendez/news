package com.example.news.mock

import com.example.news.model.Article
import com.example.news.model.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

const val FORCE_GET_REPO_ARTICLES_EXCEPTION = "FORCE_GET_REPO_ARTICLES_EXCEPTION"
const val REPO_ARTICLES_EXCEPTION_MESSAGE = "Failed getting articles from Repository."

class FakeRepositoryImpl constructor(
    private val cacheArticlesData: HashMap<String, Article>,
    private val networkArticlesData: HashMap<String, Article>
) : Repository {

    override suspend fun getCachedArticles(query: String): List<Article> {
        val articles: ArrayList<Article> = ArrayList()
        for (article in cacheArticlesData) {
            articles.add(article.value)
        }
        return articles
    }

    override suspend fun getArticles(query: String): Flow<List<Article>> {

        // we need to test the case where an exception is thrown,
        // to that effect we will fake it by sending a query with
        // the value FORCE_GET_REPO_ARTICLES_EXCEPTION
        if (query == FORCE_GET_REPO_ARTICLES_EXCEPTION) {
            throw Exception(REPO_ARTICLES_EXCEPTION_MESSAGE)
        }

        return flow {
            val articles: ArrayList<Article> = ArrayList()
            for (article in cacheArticlesData) {
                articles.add(article.value)
            }
            emit(articles)

            // fake network update delay
            delay(1000)

            for (article in networkArticlesData) {
                articles.add(article.value)
            }
            emit(articles)
        }
    }
}
