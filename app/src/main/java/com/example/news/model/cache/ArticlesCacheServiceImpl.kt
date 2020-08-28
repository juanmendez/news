package com.example.news.model.cache

import com.example.news.model.Article
import com.example.news.util.DateUtil
import java.text.SimpleDateFormat

// This ArticlesCacheService implementation uses DAO/Room
class ArticlesCacheServiceImpl
constructor(
    private val articlesDao: ArticlesDao
) : ArticlesCacheService {

    // https://developer.android.com/reference/java/text/SimpleDateFormat
    // "2020-08-24T14:38:37Z"
    // "August 08, 2020"
    private val mapper: CacheMapper by lazy {
        CacheMapper(
            dateUtil = DateUtil(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                SimpleDateFormat("MMMM dd',' YYYY")
            )
        )
    }

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