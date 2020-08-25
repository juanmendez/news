package com.example.news.model.cache

import com.example.news.model.Article
import com.example.news.util.DateUtil
import com.example.news.util.EntityMapper

class CacheMapper
constructor(
    private val dateUtil: DateUtil
) : EntityMapper<ArticleCacheEntity, Article> {

    fun entityListToArticleList(entities: List<ArticleCacheEntity>): List<Article> {
        val list: ArrayList<Article> = ArrayList()
        for (entity in entities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun articleListToEntityList(notes: List<Article>): List<ArticleCacheEntity> {
        val entities: ArrayList<ArticleCacheEntity> = ArrayList()
        for (note in notes) {
            entities.add(mapToEntity(note))
        }
        return entities
    }

    override fun mapFromEntity(entity: ArticleCacheEntity): Article {
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

    override fun mapToEntity(article: Article): ArticleCacheEntity {
        return ArticleCacheEntity(
            id = article.id,
            query = article.query,
            sourceId = article.sourceId,
            sourceName = article.sourceName,
            author = article.author,
            title = article.title,
            description = article.description,
            url = article.url,
            imageUrl = article.imageUrl,
            publishedDate = dateUtil.dateToTime(article.publishedDate),
            content = article.content)
    }
}
