package com.example.news

import com.example.news.model.RepositoryImpl
import com.example.news.model.cache.ArticlesDaoService
import com.example.news.model.network.ArticlesApiService
import com.example.news.util.TAG
import com.example.news.util.log
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

@InternalCoroutinesApi
class TestRepository {

    // this is the system in test
    private val repository: RepositoryImpl

    private val dependencyContainer: DependencyContainer
    private val articleFactory: ArticleFactory
    private val articlesDaoService: ArticlesDaoService
    private val articlesApiService: ArticlesApiService

    init {
        // init fake dependencies
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()

        // fake article factory
        articleFactory = dependencyContainer.articleFactory

        // fake data sources
        articlesDaoService = dependencyContainer.articlesDaoService
        articlesApiService = dependencyContainer.articlesApiService

        // init system in test
        repository = RepositoryImpl(articlesApiService, articlesDaoService)
    }

    @Test
    fun getArticles_success_confirmCacheUpdate() = runBlocking {
        // we call coroutines, so we block the thread
        // alternatively we could have called CoroutineScope(IO).launch

        val query = "technology"

        val cachedCount = articlesDaoService.getArticlesCount(query)
        log(this@TestRepository.TAG, "cached count = $cachedCount")
        if (cachedCount > 0) {

            // empty cache
            articlesDaoService.deleteAllArticles()

            // verify it's empty
            val emptyCount = articlesDaoService.getArticlesCount(query)
            log(this@TestRepository.TAG, "empty cache count = $emptyCount")
            assert(emptyCount == 0)
        }

        log(this@TestRepository.TAG, "call Repository's getArticles")
        repository.getArticles(query)

        // verify cache has new articles
        val newCacheCount = articlesDaoService.getArticlesCount(query)
        log(this@TestRepository.TAG, "updated cache count = $newCacheCount")
        assert(newCacheCount > 0)
    }
}
