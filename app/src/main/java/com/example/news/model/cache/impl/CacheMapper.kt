package com.example.news.model.cache.impl

import com.example.news.model.Article
import com.example.news.util.DateUtil
import com.example.news.util.EntityMapper

/**
 * Maps between cache entity model [ArticleEntity] and domain model [Article].
 * Maps both ways from cache entity model to domain model and also from domain model to the cache
 * entity model. This is because we read from the cache and we also write into the cache.
 */
class CacheMapper(
    private val dateUtil: DateUtil
) : EntityMapper<ArticleEntity, Article> {

    /**
     * Maps a list of cache entity model [ArticleEntity] into a list of domain model [Article].
     * Used when reading from the cache.
     */
    fun toDomain(articleEntityList: List<ArticleEntity>): List<Article> {
        val articleList: ArrayList<Article> = ArrayList()
        for (articleEntity in articleEntityList) {
            articleList.add(toDomain(articleEntity))
        }
        return articleList
    }

    /**
     * Maps a list of domain model [Article] into a list of cache entity model [ArticleEntity].
     * Used when writting to the cache.
     */
    fun toEntity(articleList: List<Article>): List<ArticleEntity> {
        val articleEntityList: ArrayList<ArticleEntity> = ArrayList()
        for (article in articleList) {
            articleEntityList.add(toEntity(article))
        }
        return articleEntityList
    }

    override fun toDomain(entityModel: ArticleEntity): Article {
        return Article(
            id = entityModel.id,
            query = entityModel.query,
            sourceId = entityModel.sourceId,
            sourceName = entityModel.sourceName,
            author = entityModel.author,
            title = entityModel.title,
            description = entityModel.description,
            url = entityModel.url,
            imageUrl = entityModel.imageUrl,
            publishedDate = dateUtil.timeToDate(entityModel.publishedDate),
            content = entityModel.content
        )
    }

    override fun toEntity(domainModel: Article): ArticleEntity {
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
            content = domainModel.content
        )
    }
}
