package com.example.news

import com.example.news.di.DependencyContainer
import com.example.news.mock.FORCE_GET_CACHE_ARTICLES_EXCEPTION
import com.example.news.mock.FORCE_GET_NETWORK_ARTICLES_EXCEPTION
import com.example.news.model.RepositoryImpl
import com.example.news.model.cache.CacheService
import com.example.news.model.network.ApiService
import com.example.news.util.TAG
import com.example.news.util.assertThrows
import com.example.news.util.log
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test

@InternalCoroutinesApi
class TestRepository {

    // this is the system in test
    private val repository: RepositoryImpl

    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val cacheService: CacheService
    private val apiService: ApiService

    init {
        // init fake dependencies
        dependencyContainer.build()

        // fake data sources
        cacheService = dependencyContainer.cacheService
        apiService = dependencyContainer.apiService

        // init system in test
        repository = RepositoryImpl(apiService, cacheService)
    }

    @Test
    fun getArticles_success() = runBlocking {
        val query = "technology"

        // verify cache has articles
        val cachedCount = cacheService.getArticlesCount(query)
        log(this@TestRepository.TAG, "cache count = $cachedCount")
        assert(cachedCount == 1)

        log(this@TestRepository.TAG, "call Repository's getArticles")
        val stream = repository.getArticles(query)
        var emitCount = 0
        stream.collect { articles ->
            assert(articles.isNotEmpty())
            emitCount++
            when (emitCount) {
                // verify cache data size emitted
                1 -> {
                    log(this@TestRepository.TAG, "cache articles displayed = ${articles.size}")
                    assert(articles.size == 1)
                }
                // verify network-refreshed cache data size emitted
                2 -> {
                    log(this@TestRepository.TAG, "network-refreshed cache articles displayed = ${articles.size}")
                    assert(articles.size == 2)
                }
            }
        }

        // verify cache was updated with new articles
        val newCacheCount = cacheService.getArticlesCount(query)
        log(this@TestRepository.TAG, "updated cache count = $newCacheCount")
        assert(newCacheCount == 2)
    }

    @Test
    fun getArticles_failure() = runBlocking {

        val stream = repository.getArticles(FORCE_GET_CACHE_ARTICLES_EXCEPTION)
        assertThrows<Exception> {
            log(this@TestRepository.TAG, "call Repository's getArticles and fail on cache")
            stream.collect {}
        }

        val stream2 = repository.getArticles(FORCE_GET_NETWORK_ARTICLES_EXCEPTION)
        assertThrows<Exception> {
            log(this@TestRepository.TAG, "call Repository's getArticles and fail on network")
            stream2.collect {}
        }
    }
}
