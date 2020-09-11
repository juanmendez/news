package com.example.news.model.network.impl

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {

    companion object {

        const val API_KEY_PARAM_NAME = "apiKey"

        // https://newsapi.org
        // claudiu.colteu@gmail.com / abcd1234
        const val API_KEY_PARAM_VALUE = "7da5d9626af74c1eab78e5e8aee72b0d"

        // use this to see an error thrown to the UI
        //const val API_KEY_PARAM_VALUE = "bad-api-key"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val originalHttpUrl: HttpUrl = originalRequest.url()
        val url = originalHttpUrl.newBuilder()
            .addQueryParameter(API_KEY_PARAM_NAME, API_KEY_PARAM_VALUE)
            .build()
        val request: Request = originalRequest.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}
