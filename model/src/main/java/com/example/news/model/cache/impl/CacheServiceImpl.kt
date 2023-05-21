package com.example.news.model.cache.impl

import com.example.news.model.Article
import com.example.news.model.cache.CacheService

/**
 * Implementation of the cache service interface using DAO and Room
 * Note that this implementation uses the CacheMapper to map from the domain model [Article] into
 * the cache entity model [ArticleEntity] when writing to the database and map from the cache
 * entity model [ArticleEntity] to the domain model [Article] when reading from the database.
 * This way the repository and subsequent upper layers only use the domain model and the cache
 * entity model is only used by the cache service.
 */
class CacheServiceImpl
constructor(
    private val articlesDao: ArticlesDao,
    private val mapper: CacheMapper
) : CacheService {

    override suspend fun insertArticle(article: Article): Long {
        return articlesDao.insertArticle(mapper.toEntity(article))
    }

    override suspend fun insertArticles(articles: List<Article>): LongArray {
        return articlesDao.insertArticles(mapper.toEntity(articles))
    }

    override suspend fun getArticles(query: String): List<Article> {
        return mapper.toDomain(articlesDao.getArticles(query))
    }

    override suspend fun getArticlesCount(query: String): Int {
        return articlesDao.getArticlesCount(query)
    }

    override suspend fun deleteArticles(query: String): Int {
        return articlesDao.deleteArticles(query)
    }

    override suspend fun deleteAllArticles(): Int {
        return articlesDao.deleteAllArticles()
    }
}
