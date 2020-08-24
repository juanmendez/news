package com.example.news.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.news.model.local.ArticleLocal


@Dao
interface ArticlesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: ArticleLocal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticles(articles: List<ArticleLocal>)

    @Query("SELECT * FROM articles WHERE `query` LIKE :query ORDER BY published_date DESC")
    fun getArticles(query: String?): List<ArticleLocal>

    @Query("SELECT COUNT(*) FROM articles WHERE `query` LIKE :query")
    fun getArticlesCount(query: String?): Int
}
