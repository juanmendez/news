package com.example.news.model.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApi {

    // http://newsapi.org/v2/everything?q=technology&apiKey=7da5d9626af74c1eab78e5e8aee72b0d
    @GET("/v2/everything")
    suspend fun getArticles(@Query("q") query: String, @Query("apiKey") apiKey: String): Response<GetArticlesResponse>
}
