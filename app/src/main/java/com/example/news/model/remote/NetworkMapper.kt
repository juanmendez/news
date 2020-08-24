package com.example.news.model.remote

import com.example.news.model.Article
import java.util.*
import kotlin.collections.ArrayList

object NetworkMapper {

    fun remoteListToArticleList(query: String, remoteArticles: List<ArticleRemote>): List<Article> {
        val list: ArrayList<Article> = ArrayList()
        for (remoteArticle in remoteArticles) {
            list.add(mapFromRemote(query, remoteArticle))
        }
        return list
    }

    private fun mapFromRemote(query: String, articleRemote: ArticleRemote): Article {
        return Article(
            id = UUID.randomUUID().toString(),
            query = query,
            sourceId = articleRemote.source?.id ?: "",
            sourceName = articleRemote.source?.name ?: "",
            author = articleRemote.author ?: "",
            title = articleRemote.title ?: "",
            description = articleRemote.description ?: "",
            url = articleRemote.url ?: "",
            imageUrl = articleRemote.urlToImage ?: "",
            publishedDate = articleRemote.publishedAt ?: "",
            content = articleRemote.content ?: ""
        )
    }
}
