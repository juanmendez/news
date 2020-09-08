package com.example.news.model.network.impl

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

fun <S> createService(
    retrofit: Retrofit,
    serviceClass: Class<S>?
): S {
    return retrofit.create(serviceClass)
}

private fun parseError(
    retrofit: Retrofit,
    response: Response<*>
): ApiError? {
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

fun <T> handleError(
    retrofit: Retrofit,
    response: Response<T>
): Response<T> {
    if (!response.isSuccessful) {
        parseError(retrofit, response)?.let { apiError ->
            throw Exception("\nStatus code: ${response.code()}\n\nMessage: ${apiError.message()}")
        } ?: run {
            throw Exception("Error parsing API error response")
        }
    }
    return response
}
