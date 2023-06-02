package com.example.news.network.api.impl

import com.example.news.data.Article
import com.example.news.network.api.impl.data.ArticleNetwork

/**
 * Maps the network entity model [ArticleNetwork] to the domain model [Article].
 * Note that the CacheMapper maps both ways from cache model to domain model and also from domain
 * model to the cache entity model. This is because we read from the cache and we also write into
 * the cache.
 * However we only read from the network, we do not write to it, so we only need to map from the
 * network entity model to the domain model.
 */
object NetworkMapper {

    /**
     * Maps a list of articles from the network entity model [ArticleNetwork] to the domain model
     * [Article]
     * @param query the query that was used to fetch the articles from the network, added to the
     * [Article] domain model
     * @param remoteArticles the list of [ArticleNetwork]
     * @return the list of [Article]
     */
    fun toDomain(query: String, remoteArticles: List<ArticleNetwork>): List<com.example.news.data.Article> {
        val list: ArrayList<com.example.news.data.Article> = ArrayList()
        for (remoteArticle in remoteArticles) {
            list.add(toDomain(query, remoteArticle))
        }
        return list
    }

    private fun toDomain(query: String, articleNetwork: ArticleNetwork): com.example.news.data.Article {
        return com.example.news.data.Article(
            // this makes primary key unique, so we will not have
            // any duplicate articles inserted into the database
            id = articleNetwork.hashCode().toString(),
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
