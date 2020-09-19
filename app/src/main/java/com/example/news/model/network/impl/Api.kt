package com.example.news.model.network.impl

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("/v2/everything")
    suspend fun getArticles(
        @Query("q") query: String,
        @Query("page") page: Int
    ): Response<ArticlesResponse>

    @GET("/v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("page") page: Int
    ): Response<ArticlesResponse>
}
