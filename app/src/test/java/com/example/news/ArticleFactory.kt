package com.example.news

import com.example.news.model.Article
import java.util.*

class ArticleFactory {

    fun createArticle(
        id: String? = null,  // if the id is null we'll generate one
        query: String,
        sourceId: String,
        sourceName: String,
        author: String,
        title: String,
        description: String,
        url: String,
        imageUrl: String,
        publishedDate: String,
        content: String
    ): Article {
        return Article(
            id = id ?: UUID.randomUUID().toString(),
            query = query,
            sourceId = sourceId,
            sourceName = sourceName,
            author = author,
            title = title,
            description = description,
            url = url,
            imageUrl = imageUrl,
            publishedDate = publishedDate,
            content = content
        )
    }

    fun createArticlesList(numArticles: Int): List<Article> {
        val list: ArrayList<Article> = ArrayList()
        for (i in 0 until numArticles) {
            list.add(
                createArticle(
                    id = UUID.randomUUID().toString(),
                    query = UUID.randomUUID().toString(),
                    sourceId = UUID.randomUUID().toString(),
                    sourceName = UUID.randomUUID().toString(),
                    author = UUID.randomUUID().toString(),
                    title = UUID.randomUUID().toString(),
                    description = UUID.randomUUID().toString(),
                    url = UUID.randomUUID().toString(),
                    imageUrl = UUID.randomUUID().toString(),
                    publishedDate = UUID.randomUUID().toString(),
                    content = UUID.randomUUID().toString()
                )
            )
        }
        return list
    }
}
