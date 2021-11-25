package com.example.news.model.cache.impl

import com.example.news.model.Article
import com.example.news.util.DateUtil
import com.example.news.util.EntityMapper

/**
 * Maps between cache entity model [ArticleEntity] and domain model [Article].
 * Maps both ways from cache model to domain model and also from domain model to the cache entity
 * model. This is because we read from the cache and we also write into the cache.
 */
class CacheMapper
constructor(
    private val dateUtil: DateUtil
) : EntityMapper<ArticleEntity, Article> {

    fun entityListToArticleList(entities: List<ArticleEntity>): List<Article> {
        val list: ArrayList<Article> = ArrayList()
        for (entity in entities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun articleListToEntityList(notes: List<Article>): List<ArticleEntity> {
        val entities: ArrayList<ArticleEntity> = ArrayList()
        for (note in notes) {
            entities.add(mapToEntity(note))
        }
        return entities
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

    override fun mapToEntity(article: Article): ArticleEntity {
        return ArticleEntity(
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
