package com.example.news.network

import com.example.news.data.cache.CacheService
import com.example.news.network.api.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * In this application the cache is the data source. The network articles are just saved in the
 * cache.
 *
 * The data flows as follows:
 * UI -> ViewModel -> Repository -> ApiService -> ArticleNetwork -> NetworkMapper -> Article
 * -> Repository -> CacheService -> ArticleEntity -> DAO -> Room -> DAO -> ArticleEntity
 * -> CacheService -> Article -> Repository -> ViewModel -> UI
 *
 * LSP (Liskov Substitution Principle): Objects should be replaceable with subtype instances.
 * DIP (Dependency Inversion Principle): depend on abstractions, not on concretions.
 * Here the dependencies, api service and cache service, are not instantiated (concretion), instead
 * we inject interface-abstracted dependencies. This avoids hard-dependencies and allows faking
 * dependencies when testing in isolation.
 */
class RepositoryImpl(
    private val apiService: ApiService,
    private val cacheService: CacheService
) : Repository {

    /**
     * Emits cached articles, then refreshes cache with new articles
     * fetched from the network and emits the updated cached articles
     */
    override suspend fun getArticles(query: String): Flow<List<com.example.news.data.Article>> =
        flow {

            // uncomment to see the UI handling the Repo throwing an exception
            //throw Exception()

            // uncomment to see the UI progress bar (APIs and Room are too fast)
            //delay(2000)

            val cachedArticlesCount = cacheService.getArticlesCount(query)
            if (cachedArticlesCount > 0) {
                // if there are cached articles emit them
                emit(cacheService.getArticles(query))
            }
            // fetch new articles from network
            apiService.getArticles(query).let {
                // save them to cache
                cacheService.insertArticles(it)
                // emit them
                emit(cacheService.getArticles(query))
            }
        }
}