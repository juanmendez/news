package com.example.news.model

import com.example.news.model.cache.CacheService
import com.example.news.model.network.ApiService2
import com.example.news.model.network.impl.ArticlesResponse
import com.example.news.model.network.impl.NetworkMapper
import com.example.news.util.TOP_HEADLINES
import com.example.news.util.ApiResponse
import com.example.news.util.NetworkBoundResource2
import com.example.news.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * In this application the cache is the data source. The network articles are just saved in the
 * cache.
 *
 * The data flows as follows:
 * UI -> ViewModel -> Repository -> ApiService -> [ArticleNetwork] -> NetworkMapper -> [Article]
 * -> Repository -> CacheService -> [ArticleEntity] -> DAO -> Room -> DAO -> [ArticleEntity]
 * -> CacheService -> [Article] -> Repository -> ViewModel -> UI
 *
 * LSP (Liskov Substitution Principle): Objects should be replaceable with subtype instances.
 * DIP (Dependency Inversion Principle): depend on abstractions, not on concretions.
 * Here the dependencies, api service and cache service, are not instantiated (concretion), instead
 * we inject interface-abstracted dependencies. This avoids hard-dependencies and allows faking
 * dependencies when testing in isolation.
 */
class Repository2Impl(
    private val apiService2: ApiService2,
    private val cacheService: CacheService
) : Repository2 {

    @FlowPreview
    @ExperimentalCoroutinesApi
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

            override suspend fun saveNetworkResponseToCache(data: ArticlesResponse) {
                // save the page to cache
                data.articles?.let { networkArticles ->
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

    @FlowPreview
    @ExperimentalCoroutinesApi
    override suspend fun getTopHeadlines(page: Int): Flow<Resource<List<Article>>> {
        return object : NetworkBoundResource2<List<Article>, ArticlesResponse>() {

            override fun shouldFetchFromNetwork(data: List<Article>?): Boolean {
                // we always want the latest articles
                return true
            }

            override suspend fun fetchFromNetwork(): ApiResponse<ArticlesResponse> {
                return ApiResponse.create(apiService2.getTopHeadlines(page))
            }

            override suspend fun saveNetworkResponseToCache(data: ArticlesResponse) {
                data.articles?.let { networkArticles ->
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
