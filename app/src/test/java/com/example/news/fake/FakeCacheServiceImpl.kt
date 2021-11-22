package com.example.news.fake

import com.example.news.model.Article
import com.example.news.model.cache.CacheService

const val FORCE_NEW_ARTICLE_EXCEPTION = "FORCE_NEW_ARTICLE_EXCEPTION"
const val FORCE_GENERAL_FAILURE = "FORCE_GENERAL_FAILURE"
const val FORCE_GET_CACHE_ARTICLES_EXCEPTION = "FORCE_GET_CACHE_ARTICLES_EXCEPTION"

/**
 * Fake cache service implementation used in Unit Tests
 * A mock needs to be told what to reply with when a certain method is invoked during test, while
 * a fake is an actual implementation of the functionality (usually an interface) and allows for
 * specific behavior.
 * Here this fake implementation of the CacheService allows to test the case where an exception is
 * thrown when calling insertArticle and we do that by calling insertArticle with the article.id
 * parameter equal to FORCE_NEW_ARTICLE_EXCEPTION.
 * We also constructor inject a hash map with the fake cached articles thus faking the articles
 * database (in the real CacheService we delegate to DAO).
 */
class FakeCacheServiceImpl
constructor(
    private val fakeCacheArticlesData: HashMap<String, Article>,
) : CacheService {
    override suspend fun insertArticle(article: Article): Long {

        // we need to test if the DAO throws an exception
        // we fake DAO throwing an exception by inserting
        // an article with the id FORCE_NEW_ARTICLE_EXCEPTION
        if (article.id == FORCE_NEW_ARTICLE_EXCEPTION) {
            throw Exception("Something went wrong inserting the article in cache.")
        }

        // we need to test if a DAO insert fails, which would return -1
        // we fake a DAO insert failure by inserting
        // an article with the id FORCE_GENERAL_FAILURE
        if (article.id == FORCE_GENERAL_FAILURE) {
            return -1 // fail
        }

        // insert article in fake database
        fakeCacheArticlesData[article.id] = article

        // this mocks an article being successfully inserted
        // in the database, which would return 1
        return 1
    }

    override suspend fun insertArticles(articles: List<Article>): LongArray {
        val results = LongArray(articles.size)
        for ((index, article) in articles.withIndex()) {
            results[index] = 1
            fakeCacheArticlesData[article.id] = article
        }
        return results
    }

    override suspend fun getArticles(query: String): List<Article> {

        // we need to test the case where an exception is thrown,
        // to that effect we will fake it by sending a query with
        // the value FORCE_GET_ARTICLES_EXCEPTION
        if (query == FORCE_GET_CACHE_ARTICLES_EXCEPTION) {
            throw Exception("Something went getting the cached articles.")
        }

        val results: ArrayList<Article> = ArrayList()

        // search in the fake database (HashMap)
        // to match what a database search would do
        for (article in fakeCacheArticlesData.values) {
            if (article.query.contains(query)) {
                results.add(article)
            }
        }
        return results
    }

    override suspend fun getArticlesCount(query: String): Int {
        var count = 0
        for (article in fakeCacheArticlesData.values) {
            if (article.query.contains(query)) {
                count++
            }
        }
        return count
    }

    override suspend fun deleteArticles(query: String): Int {
        var deletedArticlesCount = 0
        val iterator = fakeCacheArticlesData.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (fakeCacheArticlesData[key]?.query?.contains(query) == true) {
                iterator.remove()
                deletedArticlesCount++
            }
        }
        return deletedArticlesCount
    }

    override suspend fun deleteAllArticles(): Int {
        val deletedArticlesCount = fakeCacheArticlesData.size
        fakeCacheArticlesData.clear()
        return deletedArticlesCount
    }
}
