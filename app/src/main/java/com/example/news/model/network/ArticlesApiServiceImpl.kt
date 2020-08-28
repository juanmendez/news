package com.example.news.model.network

import com.example.news.model.Article
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

// This ArticlesApiService implementation uses Retrofit
class ArticlesApiServiceImpl : ArticlesApiService {

    override suspend fun getArticles(query: String): List<Article> {
        // synchronous Retrofit call that will be
        // executed inside an IO-scoped coroutine
        val response = handleError(INSTANCE.getArticles(query, API_KEY))
        val networkArticles = response.body()?.articles
        networkArticles?.let {
            return NetworkMapper.networkArticleListToArticleList(query, networkArticles)
        } ?: run {
            throw Exception()
        }
    }

    companion object {

        // https://newsapi.org
        // claudiu.colteu@gmail.com / abcd1234
        const val API_KEY = "7da5d9626af74c1eab78e5e8aee72b0d"

        private val builder: Retrofit.Builder by lazy {
            Retrofit.Builder()
                .baseUrl("http://newsapi.org")
                .addConverterFactory(GsonConverterFactory.create())
        }

        private val retrofit: Retrofit by lazy {
            builder.build()
        }

        private fun <S> createService(
            serviceClass: Class<S>?
        ): S {
            return retrofit.create(serviceClass)
        }

        private fun parseError(response: Response<*>): ApiError? {
            val converter: Converter<ResponseBody, ApiError> =
                retrofit.responseBodyConverter(
                    ApiError::class.java,
                    arrayOfNulls<Annotation>(0)
                )
            val error: ApiError?
            error = try {
                converter.convert(response.errorBody())
            } catch (e: IOException) {
                return ApiError()
            }
            return error
        }

        private fun <T> handleError(response: Response<T>): Response<T> {
            if (!response.isSuccessful) {
                throw Exception(parseError(response)?.message())
            }
            return response
        }

        // Singleton Retrofit API instance
        val INSTANCE: RetrofitApi by lazy {
            createService(RetrofitApi::class.java)
        }
    }
}
