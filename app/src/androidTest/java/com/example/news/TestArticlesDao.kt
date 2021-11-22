package com.example.news

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.news.model.cache.impl.ArticleEntity
import com.example.news.model.cache.impl.ArticlesDao
import com.example.news.model.cache.impl.ArticlesDatabase
import com.example.news.util.TAG
import com.example.news.util.isUnitTest
import com.example.news.util.log
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestArticlesDao {

    // this is the system in test
    private lateinit var articlesDao: ArticlesDao

    private lateinit var database: ArticlesDatabase

    init {
        isUnitTest = false
    }

    @Before
    fun setup() {

        // init dependencies
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val databaseBuilder = Room.inMemoryDatabaseBuilder(
            context,
            ArticlesDatabase::class.java
        )
        database = databaseBuilder.build()

        // init system in test
        articlesDao = database.articlesDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertArticle_success() = runBlocking {
        val query = "technology"
        val articleEntity = ArticleEntity(
            id = "123",
            query = query,
            sourceId = "456",
            sourceName = "Popular Mechanics",
            author = "John Smith",
            title = "Self-Repairing Robots",
            description = "The End of Humankind",
            url = "https://www.google.com",
            imageUrl = "",
            publishedDate = 0,
            content = "This is the beginning of the end for humanity."
        )

        val id: Long = articlesDao.insertArticle(articleEntity)
        log(this@TestArticlesDao.TAG, "call DAO's insertArticle, returns $id")
        assert(id != -1L)
        log(this@TestArticlesDao.TAG, "call DAO's getArticles")
        val articles: List<ArticleEntity> = articlesDao.getArticles(query)
        assert(!articles.isNullOrEmpty())
        assert(articles.size == 1)
        log(this@TestArticlesDao.TAG, "inserted articles: ${articles.size}")
    }
}
