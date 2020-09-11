package com.example.news.model

import com.example.news.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface Repository2 {
    fun getArticles(query: String): Flow<Resource<List<Article>>>
    fun getHeadlines(): Flow<Resource<List<Article>>>
}
