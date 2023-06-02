package com.example.news

import com.example.news.di.FakeDependencyContainer
import com.example.news.fake.FORCE_GET_CACHE_ARTICLES_EXCEPTION
import com.example.news.fake.FORCE_GET_NETWORK_ARTICLES_EXCEPTION
import com.example.news.network.RepositoryImpl
import com.example.news.data.cache.CacheService
import com.example.news.data.util.TAG
import com.example.news.util.assertThrows
import com.example.news.data.util.log
import com.example.news.network.api.ApiService
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

@InternalCoroutinesApi
class TestRepository {

    // this is the system in test
    private val repository: RepositoryImpl

    // fakes
    private val fakeDependencyContainer: FakeDependencyContainer = FakeDependencyContainer()
    private val fakeCacheService: CacheService
    private val fakeApiService: ApiService

    init {
        // init fake dependencies
        fakeDependencyContainer.build()

        // fake dependencies
        fakeCacheService = fakeDependencyContainer.fakeCacheService
        fakeApiService = fakeDependencyContainer.fakeApiService

        // init system in test
        repository = RepositoryImpl(fakeApiService, fakeCacheService)
    }

    @Test
    fun getArticles_success() = runBlocking {
        val query = "technology"

        // assert cache has articles
        val cachedCount = fakeCacheService.getArticlesCount(query)
        log(this@TestRepository.TAG, "cache count = $cachedCount")
        assert(cachedCount == 1)

        log(this@TestRepository.TAG, "call Repository's getArticles")
        val stream = repository.getArticles(query)
        var emitCount = 0
        stream.collect { articles ->
            assert(articles.isNotEmpty())
            emitCount++
            when (emitCount) {
                // assert cache data size emitted
                1 -> {
                    log(this@TestRepository.TAG, "cache articles displayed = ${articles.size}")
                    assert(articles.size == 1)
                }
                // assert network-refreshed cache data size emitted
                2 -> {
                    log(this@TestRepository.TAG, "network-refreshed cache articles displayed = ${articles.size}")
                    assert(articles.size == 2)
                }
            }
        }

        // assert cache was updated with new articles
        val newCacheCount = fakeCacheService.getArticlesCount(query)
        log(this@TestRepository.TAG, "updated cache count = $newCacheCount")
        assert(newCacheCount == 2)
    }

    @Test
    fun getArticles_failure() = runBlocking {

        // assert failure on cache hit
        val stream = repository.getArticles(FORCE_GET_CACHE_ARTICLES_EXCEPTION)
        assertThrows<Exception> {
            log(this@TestRepository.TAG, "call Repository's getArticles and fail on cache")
            stream.collect {}
        }

        // assert failure on network hit
        val stream2 = repository.getArticles(FORCE_GET_NETWORK_ARTICLES_EXCEPTION)
        assertThrows<Exception> {
            log(this@TestRepository.TAG, "call Repository's getArticles and fail on network")
            stream2.collect {}
        }
    }
}
