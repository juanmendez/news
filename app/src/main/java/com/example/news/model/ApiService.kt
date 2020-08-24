package com.example.news.model

import com.example.news.model.remote.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// https://newsapi.org
// claudiu.colteu@gmail.com / abcd1234
// API Key: 7da5d9626af74c1eab78e5e8aee72b0d
// Sample call:
// http://newsapi.org/v2/everything?q=technology&apiKey=7da5d9626af74c1eab78e5e8aee72b0d
interface ApiService {

    @GET("/v2/everything")
    suspend fun getArticles(@Query("q") query: String, @Query("apiKey") apiKey: String = API_KEY): Response

    companion object {
        const val API_KEY = "7da5d9626af74c1eab78e5e8aee72b0d"
        val instance: ApiService by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://newsapi.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(ApiService::class.java)
        }
    }
}
