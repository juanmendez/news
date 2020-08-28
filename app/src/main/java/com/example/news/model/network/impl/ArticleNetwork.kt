package com.example.news.model.network.impl

data class ArticleNetwork (
    val source: Source?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArticleNetwork

        if (source?.id != other.source?.id) return false
        if (source?.name != other.source?.name) return false
        if (author != other.author) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (url != other.url) return false
        if (urlToImage != other.urlToImage) return false
        if (publishedAt != other.publishedAt) return false
        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        var result = source?.id.hashCode()
        result = 31 * result + source?.name.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + urlToImage.hashCode()
        result = 31 * result + publishedAt.hashCode()
        result = 31 * result + content.hashCode()
        return result
    }
}
