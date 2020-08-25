package com.example.news.model.network

import com.example.news.model.network.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// https://newsapi.org
// claudiu.colteu@gmail.com / abcd1234
// API Key: 7da5d9626af74c1eab78e5e8aee72b0d
// Sample call:
// http://newsapi.org/v2/everything?q=technology&apiKey=7da5d9626af74c1eab78e5e8aee72b0d
interface ArticlesApi {

    @GET("/v2/everything")
    suspend fun getArticles(@Query("q") query: String, @Query("apiKey") apiKey: String): Response
}
