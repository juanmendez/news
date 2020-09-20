package com.example.news.model

import com.example.news.model.cache.CacheService
import com.example.news.model.network.ApiService2
import com.example.news.model.network.impl.ArticlesResponse
import com.example.news.model.network.impl.NetworkMapper
import com.example.news.util.TOP_HEADLINES
import com.example.news.util.ApiResponse
import com.example.news.util.NetworkBoundResource2
import com.example.news.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// LSP (Liskov Substitution Principle): Objects should be replaceable with subtype instances.
// In the constructor arguments we substitute objects with their subtypes.
// DIP (Dependency Inversion Principle): depend on abstractions, not on concretions.
// In the constructor we inject interface-abstracted dependencies.
class Repository2Impl(
    private val apiService2: ApiService2,
    private val cacheService: CacheService
) : Repository2 {

    // Dependencies are not instantiated (concretion), instead they are injected
    // through the constructor. This avoids hard-dependencies and allows faking
    // dependencies when testing in isolation.

    override suspend fun getArticles(query: String, page: Int): Flow<Resource<List<Article>>> {
        return object : NetworkBoundResource2<List<Article>, ArticlesResponse>() {

            override fun shouldFetchFromNetwork(data: List<Article>?): Boolean {
                // we always want the latest articles
                return true
            }

            override suspend fun fetchFromNetwork(): ApiResponse<ArticlesResponse> {
                // fetch the page from network
                return ApiResponse.create(apiService2.getArticles(query, page))
            }

            override suspend fun saveNetworkResponseToCache(item: ArticlesResponse) {
                // save the page to cache
                item.articles?.let { networkArticles ->
                    val articles =
                        NetworkMapper.networkArticleListToArticleList(query, networkArticles)
                    cacheService.insertArticles(articles)
                }
            }

            override fun loadFromCache(): Flow<List<Article>> {
                return flow {
                    // always emit ALL articles from cache
                    emit(cacheService.getArticles(query))
                }
            }

        }.asFlow()
    }

    override suspend fun getTopHeadlines(page: Int): Flow<Resource<List<Article>>> {
        return object : NetworkBoundResource2<List<Article>, ArticlesResponse>() {

            override fun shouldFetchFromNetwork(data: List<Article>?): Boolean {
                // we always want the latest articles
                return true
            }

            override suspend fun fetchFromNetwork(): ApiResponse<ArticlesResponse> {
                return ApiResponse.create(apiService2.getTopHeadlines(page))
            }

            override suspend fun saveNetworkResponseToCache(item: ArticlesResponse) {
                item.articles?.let { networkArticles ->
                    val articles =
                        NetworkMapper.networkArticleListToArticleList(
                            TOP_HEADLINES,
                            networkArticles
                        )
                    cacheService.insertArticles(articles)
                }
            }

            override fun loadFromCache(): Flow<List<Article>> {
                return flow {
                    emit(cacheService.getArticles(TOP_HEADLINES))
                }
            }

        }.asFlow()
    }

    override suspend fun deleteArticles(query: String) {
        cacheService.deleteArticles(query)
    }

    override suspend fun deleteAllArticles() {
        cacheService.deleteAllArticles()
    }
}
