package com.example.news.model.network.impl

import com.example.news.util.log
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiInterceptor : Interceptor {

    companion object {

        const val PARAM_NAME_API_KEY = "apiKey"
        const val PARAM_NAME_LANGUAGE = "language"
        const val PARAM_NAME_SORT_BY = "sortBy"
        const val PARAM_NAME_PAGE_SIZE = "pageSize"

        // https://newsapi.org
        // claudiu.colteu@gmail.com / abcd1234
        const val PARAM_VALUE_API_KEY = "7da5d9626af74c1eab78e5e8aee72b0d"
        const val PARAM_VALUE_LANGUAGE = "en"
        const val PARAM_VALUE_SORT_BY = "publishedAt"
        const val PARAM_VALUE_PAGE_SIZE = "10"

        // use this to see an error thrown to the UI
        //const val API_KEY_PARAM_VALUE = "bad-api-key"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val originalHttpUrl: HttpUrl = originalRequest.url()
        val url = originalHttpUrl.newBuilder()
            .addQueryParameter(PARAM_NAME_PAGE_SIZE, PARAM_VALUE_PAGE_SIZE)
            .addQueryParameter(PARAM_NAME_SORT_BY, PARAM_VALUE_SORT_BY)
            .addQueryParameter(PARAM_NAME_LANGUAGE, PARAM_VALUE_LANGUAGE)
            .addQueryParameter(PARAM_NAME_API_KEY, PARAM_VALUE_API_KEY)
            .build()
        val request: Request = originalRequest.newBuilder().url(url).build()
        log("toto", request.url().toString())
        return chain.proceed(request)
    }
}
