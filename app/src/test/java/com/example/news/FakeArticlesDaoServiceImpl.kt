package com.example.news

import com.example.news.model.Article
import com.example.news.model.cache.ArticlesDaoService

const val FORCE_NEW_ARTICLE_EXCEPTION = "FORCE_NEW_ARTICLE_EXCEPTION"
const val FORCE_GENERAL_FAILURE = "FORCE_GENERAL_FAILURE"
const val FORCE_GET_ARTICLES_EXCEPTION = "FORCE_GET_ARTICLES_EXCEPTION"

// in the real implementation we delegate to DAO,
// here we fake the database by using a HashMap
class FakeArticlesDaoServiceImpl
constructor(
    private val articlesData: HashMap<String, Article>,
) : ArticlesDaoService {
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
        articlesData[article.id] = article

        // this mocks an article being successfully inserted
        // in the database, which would return 1
        return 1
    }

    override suspend fun insertArticles(articles: List<Article>): LongArray {
        val results = LongArray(articles.size)
        for ((index, article) in articles.withIndex()) {
            results[index] = 1
            articlesData[article.id] = article
        }
        return results
    }

    override suspend fun getArticles(query: String): List<Article> {

        // we need to test if an exception is thrown
        // we will fake it by sending a query with
        // the value FORCE_GET_ARTICLES_EXCEPTION
        if (query == FORCE_GET_ARTICLES_EXCEPTION) {
            throw Exception("Something went getting the cached articles.")
        }

        val results: ArrayList<Article> = ArrayList()

        // search in the fake database (HashMap)
        // to match what a database search would do
        for (article in articlesData.values) {
            if (article.query.contains(query)) {
                results.add(article)
            }
        }
        return results
    }

    override suspend fun getArticlesCount(query: String): Int {
        var count = 0
        for (article in articlesData.values) {
            if (article.query.contains(query)) {
                count++
            }
        }
        return count
    }

    override suspend fun deleteAllArticles(): Int {
        val deletedArticlesCount = articlesData.size
        articlesData.clear()
        return deletedArticlesCount
    }
}
