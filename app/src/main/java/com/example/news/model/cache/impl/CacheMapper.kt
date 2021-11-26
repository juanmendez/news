package com.example.news.model.cache.impl

import com.example.news.model.Article
import com.example.news.util.DateUtil
import com.example.news.util.EntityMapper

/**
 * Maps between cache entity model [ArticleEntity] and domain model [Article].
 * Maps both ways from cache entity model to domain model and also from domain model to the cache
 * entity model. This is because we read from the cache and we also write into the cache.
 */
class CacheMapper
constructor(
    private val dateUtil: DateUtil
) : EntityMapper<ArticleEntity, Article> {

    /**
     * Maps a list of cache entity model [ArticleEntity] into a list of domain model [Article].
     * Used when reading from the cache.
     */
    fun articleEntityListToArticleList(articleEntityList: List<ArticleEntity>): List<Article> {
        val articleList: ArrayList<Article> = ArrayList()
        for (articleEntity in articleEntityList) {
            articleList.add(mapFromEntity(articleEntity))
        }
        return articleList
    }

    /**
     * Maps a list of domain model [Article] into a list of cache entity model [ArticleEntity].
     * Used when writting to the cache.
     */
    fun articleListToArticleEntityList(articleList: List<Article>): List<ArticleEntity> {
        val articleEntityList: ArrayList<ArticleEntity> = ArrayList()
        for (article in articleList) {
            articleEntityList.add(mapToEntity(article))
        }
        return articleEntityList
    }

    override fun mapFromEntity(entity: ArticleEntity): Article {
        return Article(
            id = entity.id,
            query = entity.query,
            sourceId = entity.sourceId,
            sourceName = entity.sourceName,
            author = entity.author,
            title = entity.title,
            description = entity.description,
            url = entity.url,
            imageUrl = entity.imageUrl,
            publishedDate = dateUtil.timeToDate(entity.publishedDate),
            content = entity.content
        )
    }

    override fun mapToEntity(domainModel: Article): ArticleEntity {
        return ArticleEntity(
            id = domainModel.id,
            query = domainModel.query,
            sourceId = domainModel.sourceId,
            sourceName = domainModel.sourceName,
            author = domainModel.author,
            title = domainModel.title,
            description = domainModel.description,
            url = domainModel.url,
            imageUrl = domainModel.imageUrl,
            publishedDate = dateUtil.dateToTime(domainModel.publishedDate),
            content = domainModel.content)
    }
}
