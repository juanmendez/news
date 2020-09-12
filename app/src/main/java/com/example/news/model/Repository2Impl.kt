package com.example.news.model

import com.example.news.model.cache.CacheService
import com.example.news.model.network.ApiService2
import com.example.news.model.network.impl.ArticlesResponse
import com.example.news.model.network.impl.NetworkMapper
import com.example.news.util.network.ApiResponse
import com.example.news.util.network.NetworkBoundResource
import com.example.news.util.network.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository2Impl(
    private val apiService2: ApiService2,
    private val cacheService: CacheService
) : Repository2 {

    companion object {
        // needs to be stored in the cache alongside with the top headlines articles
        const val TOP_HEADLINES = "Top Headlines"
    }

    override fun getArticles(query: String): Flow<Resource<List<Article>>> {
        return object : NetworkBoundResource<List<Article>, ArticlesResponse>() {

            override fun shouldFetchFromNetwork(data: List<Article>?): Boolean {
                // we always want the latest articles
                return true
            }

            override suspend fun fetchFromNetwork(): ApiResponse<ArticlesResponse> {
                return ApiResponse.create(apiService2.getArticles(query))
            }

            override suspend fun saveNetworkResponseToCache(item: ArticlesResponse) {
                item.articles?.let { networkArticles ->
                    val articles =
                        NetworkMapper.networkArticleListToArticleList(query, networkArticles)
                    cacheService.insertArticles(articles)
                }
            }

            override fun loadFromCache(): Flow<List<Article>> {
                return flow {
                    emit(cacheService.getArticles(query))
                }
            }

        }.asFlow()
    }

    override fun getHeadlines(): Flow<Resource<List<Article>>> {
        return object : NetworkBoundResource<List<Article>, ArticlesResponse>() {

            override fun shouldFetchFromNetwork(data: List<Article>?): Boolean {
                // we always want the latest articles
                return true
            }

            override suspend fun fetchFromNetwork(): ApiResponse<ArticlesResponse> {
                return ApiResponse.create(apiService2.getHeadlines())
            }

            override suspend fun saveNetworkResponseToCache(item: ArticlesResponse) {
                item.articles?.let { networkArticles ->
                    val articles =
                        NetworkMapper.networkArticleListToArticleList(TOP_HEADLINES, networkArticles)
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
}
