package com.example.news.model

import com.example.news.model.network.ApiService3
import com.example.news.model.network.impl.ArticlesResponse
import com.example.news.model.network.impl.NetworkMapper
import com.example.news.mvi.ArticleListViewState
import com.example.news.mvi.DataState
import com.example.news.util.NetworkBoundResource3
import com.example.news.util.ApiResponse
import com.example.news.util.ApiSuccessResponse
import kotlinx.coroutines.flow.Flow

/**
 * MVI Architecture: the Repository returns a flow of ViewState wrapped in DataState
 * (to add a loading state and an error state) in response to a StateEvent sent from
 * the View to the ViewModel.
 */
class Repository3Impl(
    private val apiService3: ApiService3
) : Repository3 {
    override suspend fun getArticles(query: String): Flow<DataState<ArticleListViewState>> {
        return object : NetworkBoundResource3<ArticlesResponse, ArticleListViewState>() {

            override suspend fun fetchFromNetwork(): ApiResponse<ArticlesResponse> {
                return ApiResponse.create(apiService3.getArticles(query))
            }

            override suspend fun processResponse(response: ApiSuccessResponse<ArticlesResponse>): ArticleListViewState {
                return ArticleListViewState(
                    articles = NetworkMapper.networkArticleListToArticleList(
                        query,
                        response.body.articles ?: listOf()
                    ),
                    query = query
                )
            }
        }.asFlow()
    }
}
