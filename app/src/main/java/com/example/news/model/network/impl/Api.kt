package com.example.news.model.network.impl

import com.example.news.model.network.impl.data.ArticlesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit Api interface
 */
interface Api {

    /**
     * Gets articles from the network for a given [query] and [page]
     * @param query the matching query
     * @param page the matching page
     * @return the [ArticlesResponse] as a [Response]
     */
    @GET("/v2/everything")
    suspend fun getArticles(
        @Query("q") query: String,
        @Query("page") page: Int
    ): Response<ArticlesResponse>

    /**
     * Gets top headlines from the network matching a given [country] and [page]
     * @param country the matching country
     * @param page the matching page
     * @return the [ArticlesResponse] as a [Response]
     */
    @GET("/v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("page") page: Int
    ): Response<ArticlesResponse>
}
