package com.example.news.network


import com.example.news.data.cache.CacheService
import com.example.news.network.api.ApiService3
import com.example.news.network.api.impl.NetworkMapper
import com.example.news.network.api.impl.data.ArticlesResponse
import com.example.news.network.mvi.ArticleListViewState
import com.example.news.network.mvi.DataState
import com.example.news.network.util.ApiResponse
import com.example.news.network.util.NetworkBoundResource3
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * MVI Architecture: the Repository returns a flow of ViewState wrapped in DataState
 * (to add a loading state and an error state) in response to a StateEvent sent from
 * the View to the ViewModel.
 */
class Repository3Impl(
    private val apiService3: ApiService3,
    private val cacheService: CacheService
) : Repository3 {
    @ExperimentalCoroutinesApi
    @FlowPreview
    override suspend fun getArticles(
        query: String,
        page: Int
    ): Flow<DataState<ArticleListViewState>> {
        return object :
            NetworkBoundResource3<List<com.example.news.data.Article>, ArticlesResponse, ArticleListViewState>() {

            override fun shouldFetchFromNetwork(data: List<com.example.news.data.Article>?): Boolean {
                // we always want the latest articles
                return true
            }

            override suspend fun fetchFromNetwork(): ApiResponse<ArticlesResponse> {
                return ApiResponse.Companion.create(
                    apiService3.getArticles(
                        query,
                        page
                    )
                )
            }

            override suspend fun processCache(data: List<com.example.news.data.Article>): ArticleListViewState {
                return ArticleListViewState(
                    articles = data,
                    query = query,
                    page = page
                )
            }

            override suspend fun saveNetworkResponseToCache(data: ArticlesResponse) {
                // save the page to cache
                data.articles?.let { networkArticles ->
                    cacheService.insertArticles(
                        NetworkMapper.toDomain(
                            query,
                            networkArticles
                        )
                    )
                }
            }

            override fun loadFromCache(): Flow<List<com.example.news.data.Article>> {
                return flow {
                    // always emit ALL articles from cache
                    emit(cacheService.getArticles(query))
                }
            }
        }.asFlow()
    }

    override suspend fun deleteArticles(query: String) {
        cacheService.deleteArticles(query)
    }
}