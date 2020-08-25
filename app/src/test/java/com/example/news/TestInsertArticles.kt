package com.example.news

import com.example.news.model.cache.ArticlesDaoService
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.*

@InternalCoroutinesApi
class TestInsertArticles {
    private val dependencyContainer: DependencyContainer
    private val articlesDaoService: ArticlesDaoService
    private val articleFactory: ArticleFactory

    init {
        // init fake dependencies
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()

        // fake data sources
        articlesDaoService = dependencyContainer.articlesDaoService

        // fake article factory
        articleFactory = dependencyContainer.articleFactory
    }

    @Test
    fun insertArticles_success_confirmCacheUpdate() = runBlocking {
        // we call coroutines, so we block the thread
        // alternatively we could have called CoroutineScope(IO).launch

        val article = articleFactory.createArticle(
            id = UUID.randomUUID().toString(),
            query = "technology",
            sourceId = "",
            sourceName = "IGN",
            author = "John Doe",
            title = "PlayStation 5 Launch Date",
            description = "description",
            url = "url",
            imageUrl = "imageUrl",
            publishedDate = "publishedDate",
            content = "content"
        )

        val result = articlesDaoService.insertArticle(article)
        assert(result == 1L)

        val count = articlesDaoService.getArticlesCount("technology")
        assert(count > 0)
    }
}