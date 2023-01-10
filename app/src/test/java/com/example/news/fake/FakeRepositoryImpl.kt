package com.example.news.fake

import com.example.news.model.Article
import com.example.news.model.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

const val FORCE_GET_REPO_ARTICLES_EXCEPTION = "FORCE_GET_REPO_ARTICLES_EXCEPTION"
const val REPO_ARTICLES_EXCEPTION_MESSAGE = "Failed getting articles from Repository."

/**
 * Fake repository implementation used in Unit Tests
 * A mock needs to be told what to reply with when a certain method is invoked during test, while
 * a fake is an actual implementation of the functionality (usually an interface) and allows for
 * specific behavior.
 * Here this fake implementation of the Repository allows to test the case where an exception is
 * thrown when calling getArticles and we do that by calling getArticles with the query parameter
 * equal to FORCE_GET_REPO_ARTICLES_EXCEPTION.
 * We also constructor inject hash maps with the fake cached and network (remote cached) articles.
 */
class FakeRepositoryImpl constructor(
    private val fakeCacheArticlesData: HashMap<String, com.example.news.model.Article>,
    private val fakeNetworkArticlesData: HashMap<String, com.example.news.model.Article>
) : com.example.news.model.Repository {

    override suspend fun getArticles(query: String): Flow<List<com.example.news.model.Article>> {

        // we need to test the case where an exception is thrown,
        // to that effect we will fake it by sending a query with
        // the value FORCE_GET_REPO_ARTICLES_EXCEPTION
        if (query == FORCE_GET_REPO_ARTICLES_EXCEPTION) {
            throw Exception(REPO_ARTICLES_EXCEPTION_MESSAGE)
        }

        return flow {
            val articles: ArrayList<com.example.news.model.Article> = ArrayList()
            for (article in fakeCacheArticlesData) {
                articles.add(article.value)
            }
            emit(articles)

            // fake network update delay
            delay(1000)

            for (article in fakeNetworkArticlesData) {
                articles.add(article.value)
            }
            emit(articles)
        }
    }
}
