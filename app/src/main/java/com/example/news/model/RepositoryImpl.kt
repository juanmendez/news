package com.example.news.model

import com.example.news.model.cache.CacheService
import com.example.news.model.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// LSP (Liskov Substitution Principle): Objects should be replaceable with subtype instances
// In the constructor arguments we substitute objects with their subtypes.
// DIP (Dependency Inversion Principle): depend on abstractions, not on concretions.
// Constructor-inject interface-abstracted dependencies.
class RepositoryImpl(
    private val apiService: ApiService,
    private val cacheService: CacheService
) : Repository {

    // Dependencies are not instantiated (concretion), instead they are injected
    // through the constructor. This avoids hard-dependencies and allows faking
    // dependencies when testing in isolation.

    /**
     * Emits cached articles, then refreshes cache with new articles
     * fetched from the network and emits the updated cached articles
     */
    override suspend fun getArticles(query: String): Flow<List<Article>> = flow {

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
        apiService.getArticles(query)?.let {
            // save them to cache
            cacheService.insertArticles(it)
            // emit them
            emit(cacheService.getArticles(query))
        }
    }
}
