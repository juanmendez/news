package com.example.news.model.cache.impl

import com.example.news.model.Article
import com.example.news.model.cache.ArticlesCacheService

// This ArticlesCacheService implementation uses DAO/Room
class ArticlesCacheServiceImpl
constructor(
    private val articlesDao: ArticlesDao,
    private val mapper: CacheMapper
) : ArticlesCacheService {

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

    override suspend fun deleteAllArticles(): Int {
        return articlesDao.deleteAllArticles()
    }
}
