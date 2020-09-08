package com.example.news.model.network.impl

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Network {

    private val builder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl("http://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
    }

    // Singleton Retrofit instance
    val retrofit: Retrofit by lazy {
        builder.build()
    }

    // Singleton Retrofit API instance
    val articlesApi: ArticlesApi by lazy {
        createService(retrofit, ArticlesApi::class.java)
    }
}
