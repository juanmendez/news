package com.example.news.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * The domain model for storing the article data.
 * This model is used in the Repository and subsequent upper layers.
 * Below the repository we have entity models: a network entity model for the api service, and a
 * cache entity model for the cache service.
 */
@Parcelize
data class Article (
    val id: String,
    val query: String,
    val sourceId: String,
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val publishedDate: String,
    val content: String
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (id != other.id) return false
        if (query != other.query) return false
        if (sourceId != other.sourceId) return false
        if (sourceName != other.sourceName) return false
        if (author != other.author) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (url != other.url) return false
        if (imageUrl != other.imageUrl) return false
        if (publishedDate != other.publishedDate) return false
        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + query.hashCode()
        result = 31 * result + sourceId.hashCode()
        result = 31 * result + sourceName.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + publishedDate.hashCode()
        result = 31 * result + content.hashCode()
        return result
    }
}
