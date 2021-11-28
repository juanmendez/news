package com.example.news.model.network.impl

import com.example.news.model.network.impl.data.ApiError
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * Creates a Retrofit API service
 * @param S the Retrofit API service type
 * @param retrofit the Retrofit instance used to create the API service
 * @param serviceClass the Retrofit API service class used to create the API service
 * @return the Retrofit API service
 */
fun <S> createService(
    retrofit: Retrofit,
    serviceClass: Class<S>
): S {
    return retrofit.create(serviceClass)
}

/**
 * Facility function that throws an [Exception] if the API call was not successful, otherwise
 * returns the Retrofit response. Also attempts to parse the API error response.
 * @param retrofit the Retrofit instance used to parse the API error response
 * @throws Exception if there is the API call was not successful
 */
fun <T> checkResponseThrowError(
    retrofit: Retrofit,
    response: Response<T>
): Response<T> {
    if (!response.isSuccessful) {
        parseError(retrofit, response)?.let { apiError ->
            throw Exception("\nStatus code: ${response.code()}\n\nMessage: ${apiError.message}")
        } ?: run {
            // parseError returned null
            throw Exception("Failed parsing the API error response.")
        }
    }
    return response
}

/**
 * Parses the Retrofit response for an API error
 */
private fun parseError(
    retrofit: Retrofit,
    response: Response<*>
): ApiError? {

    // converter for transforming the API response into an ApiError network entity model
    val converter: Converter<ResponseBody, ApiError> =
        retrofit.responseBodyConverter(
            ApiError::class.java,
            arrayOfNulls<Annotation>(0)
        )

    response.errorBody()?.let {
        return try {
            // try converting the API response into an ApiError network entity model and return it
            converter.convert(it)
        } catch (e: IOException) {
            // failed parsing the API error response, return null
            return null
        }
    } ?: run {
        // empty response, return null
        return null
    }
}
