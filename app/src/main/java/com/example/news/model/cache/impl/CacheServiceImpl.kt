package com.example.news.model.cache.impl

import com.example.news.model.Article
import com.example.news.model.cache.CacheService

/**
 * Implementation of the cache service using DAO and Room
 */
class CacheServiceImpl
constructor(
    private val articlesDao: ArticlesDao,
    private val mapper: CacheMapper
) : CacheService {

    override suspend fun insertArticle(article: Article): Long {
        return articlesDao.insertArticle(mapper.mapToEntity(article))
    }

    override suspend fun insertArticles(articles: List<Article>): LongArray {
        return articlesDao.insertArticles(mapper.articleListToEntityList(articles))
    }

    override suspend fun getArticles(query: String): List<Article> {
        return mapper.entityListToArticleList(articlesDao.getArticles(query))
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
