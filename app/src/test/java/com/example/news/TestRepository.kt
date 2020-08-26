package com.example.news

import com.example.news.model.RepositoryImpl
import com.example.news.model.cache.ArticlesDaoService
import com.example.news.model.network.ArticlesApiService
import com.example.news.util.TAG
import com.example.news.util.log
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert
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

    private inline fun <reified T : Exception> assertThrows(runnable: () -> Any?) {
        try {
            runnable.invoke()
        } catch (e: Throwable) {
            if (e is T) {
                return
            }
            Assert.fail("expected ${T::class.qualifiedName} but caught " +
                    "${e::class.qualifiedName} instead")
        }
        Assert.fail("expected ${T::class.qualifiedName}")
    }

    @Test
    fun getCachedArticles_success_confirmCacheUpdate() = runBlocking {
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

        log(this@TestRepository.TAG, "call Repository's getCachedArticles")
        repository.getCachedArticles(query)

        // verify cache has articles
        val newCacheCount = articlesDaoService.getArticlesCount(query)
        log(this@TestRepository.TAG, "updated cache count = $newCacheCount")
        assert(newCacheCount > 0)
    }

    @Test
    fun getCachedArticles_failure_cacheInsert() = runBlocking {
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

        assertThrows<Exception> {
            log(this@TestRepository.TAG, "call Repository's getCachedArticles and fail")
            repository.getCachedArticles(FORCE_GET_ARTICLES_EXCEPTION)
        }
    }

    @Test
    fun getArticles_success_confirmCacheUpdate() = runBlocking {
        val query = "technology"

        // verify cache has articles
        val cachedCount = articlesDaoService.getArticlesCount(query)
        log(this@TestRepository.TAG, "cached count = $cachedCount")
        assert(cachedCount > 0)

        log(this@TestRepository.TAG, "call Repository's getArticles")
        val stream = repository.getArticles(query)
        stream.collect {
            log(this@TestRepository.TAG, "articles displayed = ${it.size}")
            assert(it.isNotEmpty())
        }

        // verify cache was updated with new articles
        val newCacheCount = articlesDaoService.getArticlesCount(query)
        log(this@TestRepository.TAG, "updated cache count = $newCacheCount")
        assert(newCacheCount > 0)
    }
}
