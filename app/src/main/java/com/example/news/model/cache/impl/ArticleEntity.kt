package com.example.news.model.cache.impl

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey var id: String,
    var query: String,
    @ColumnInfo(name = "source_id") var sourceId: String,
    @ColumnInfo(name = "source_name") var sourceName: String,
    var author: String,
    var title: String,
    var description: String,
    var url: String,
    @ColumnInfo(name = "image_url") var imageUrl: String,
    @ColumnInfo(name = "published_date") var publishedDate: Long,
    var content: String
)
