package com.example.news.model

import com.example.news.util.network.Resource
import kotlinx.coroutines.flow.Flow

// The Repository should expose suspend functions, or return Channel/Flow objects,
// depending on the nature of the API. The actual coroutines are then set up in
// the ViewModel. LiveData gets introduced by the ViewModel, not the Repository.
interface Repository2 {
    suspend fun getArticles(query: String): Flow<Resource<List<Article>>>
    suspend fun getHeadlines(): Flow<Resource<List<Article>>>
}
