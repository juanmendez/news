package com.example.news.model

import com.example.news.model.cache.ArticlesCacheService
import com.example.news.model.network.ArticlesApiService2
import com.example.news.model.network.impl.ArticlesResponse
import com.example.news.model.network.impl.NetworkMapper
import com.example.news.util.network.ApiResponse
import com.example.news.util.network.NetworkBoundResource
import com.example.news.util.network.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository2Impl(
    private val articlesApiService2: ArticlesApiService2,
    private val articlesCacheService: ArticlesCacheService
) : Repository2 {
    override fun getArticles(query: String): Flow<Resource<List<Article>>> {
        return object : NetworkBoundResource<List<Article>, ArticlesResponse>() {

            override suspend fun saveNetworkResult(item: ArticlesResponse) {
                item.articles?.let { networkArticles ->
                    val articles =
                        NetworkMapper.networkArticleListToArticleList(query, networkArticles)
                    articlesCacheService.insertArticles(articles)
                }
            }

            override fun shouldFetch(data: List<Article>?): Boolean {
                return true
            }

            override fun loadFromDb(): Flow<List<Article>> {
                return flow {
                    emit(articlesCacheService.getArticles(query))
                }
            }

            override suspend fun fetchFromNetwork(): ApiResponse<ArticlesResponse> {
                return ApiResponse.create(articlesApiService2.getArticles(query))
            }
        }.asFlow()
    }
}
