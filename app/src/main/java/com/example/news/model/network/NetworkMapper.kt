package com.example.news.model.network

import com.example.news.model.Article
import java.util.*
import kotlin.collections.ArrayList

object NetworkMapper {

    fun networkArticleListToArticleList(query: String, remoteArticles: List<ArticleNetwork>): List<Article> {
        val list: ArrayList<Article> = ArrayList()
        for (remoteArticle in remoteArticles) {
            list.add(mapFromNetwork(query, remoteArticle))
        }
        return list
    }

    private fun mapFromNetwork(query: String, articleNetwork: ArticleNetwork): Article {
        return Article(
            id = UUID.randomUUID().toString(),
            query = query,
            sourceId = articleNetwork.source?.id ?: "",
            sourceName = articleNetwork.source?.name ?: "",
            author = articleNetwork.author ?: "",
            title = articleNetwork.title ?: "",
            description = articleNetwork.description ?: "",
            url = articleNetwork.url ?: "",
            imageUrl = articleNetwork.urlToImage ?: "",
            publishedDate = articleNetwork.publishedAt ?: "",
            content = articleNetwork.content ?: ""
        )
    }
}
