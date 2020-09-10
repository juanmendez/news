package com.example.news.model.network.impl

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticlesApi {

    // http://newsapi.org/v2/everything?q=technology&apiKey=7da5d9626af74c1eab78e5e8aee72b0d
    @GET("/v2/everything")
    suspend fun getArticles(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<GetArticlesResponse>

    @GET("/v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<GetArticlesResponse>
}