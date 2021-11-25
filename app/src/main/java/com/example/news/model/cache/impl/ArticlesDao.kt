package com.example.news.model.cache.impl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object interface defining the database interactions (at compile time Room will
 * generate an implementation of this interface)
 */
@Dao
interface ArticlesDao {

    /**
     * Inserts an [ArticleEntity] into the database
     * @param article the [ArticleEntity] to be inserted into the database
     * @return the primary key value for the inserted item, -1 for failure
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticle(article: ArticleEntity): Long

    /**
     * Inserts a list of [ArticleEntity] into the database
     * @param articles the [ArticleEntity] list to be inserted into the database
     * @return the array of primary key values for the inserted list
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<ArticleEntity>): LongArray

    /**
     * Retrieves a list of [ArticleEntity] matching a given [query] ordered by published date
     * descending
     * @param query the matching predicate
     * @return the list of matching [ArticleEntity]
     */
    @Query("SELECT * FROM articles WHERE `query` LIKE :query ORDER BY published_date DESC")
    suspend fun getArticles(query: String?): List<ArticleEntity>

    /**
     * Retrieves the count of [ArticleEntity] matching a given [query]
     * @param query the matching predicate
     * @return the count as an [Int]
     */
    @Query("SELECT COUNT(*) FROM articles WHERE `query` LIKE :query")
    suspend fun getArticlesCount(query: String?): Int

    /**
     * Deletes from the database all the [ArticleEntity] matching a given [query]
     * @param query the matching predicate
     * @return the count of deleted [ArticleEntity] as an [Int]
     */
    @Query("DELETE FROM articles WHERE `query` LIKE :query")
    suspend fun deleteArticles(query: String?): Int

    /**
     * Deletes all the articles from the database
     * @return the count of deleted [ArticleEntity] as an [Int]
     */
    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles(): Int
}
