package com.example.news.model

import com.example.news.mvi.ArticleListViewState
import com.example.news.mvi.DataState
import kotlinx.coroutines.flow.Flow

interface Repository3 {
    suspend fun getArticles(query: String): Flow<DataState<ArticleListViewState>>
}