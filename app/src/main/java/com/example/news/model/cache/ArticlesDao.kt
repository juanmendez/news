package com.example.news.model.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticlesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticle(article: ArticleEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<ArticleEntity>): LongArray

    @Query("SELECT * FROM articles WHERE `query` LIKE :query ORDER BY published_date DESC")
    suspend fun getArticles(query: String?): List<ArticleEntity>

    @Query("SELECT COUNT(*) FROM articles WHERE `query` LIKE :query")
    suspend fun getArticlesCount(query: String?): Int

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles(): Int
}
