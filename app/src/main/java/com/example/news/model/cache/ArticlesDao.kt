package com.example.news.model.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticlesDao {

    @Insert
    suspend fun insertArticle(article: ArticleCacheEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<ArticleCacheEntity>): LongArray

    @Query("SELECT * FROM articles WHERE `query` LIKE :query ORDER BY published_date DESC")
    suspend fun getArticles(query: String?): List<ArticleCacheEntity>

    @Query("SELECT COUNT(*) FROM articles WHERE `query` LIKE :query")
    suspend fun getArticlesCount(query: String?): Int

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles(): Int
}
