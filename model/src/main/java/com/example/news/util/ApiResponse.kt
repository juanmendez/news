/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.news.util

import retrofit2.Response

/**
 * Sealed class defining the various api responses: success / error / empty.
 * @param <T> the type of the response object
 */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {

    companion object {

        /**
         * Create api response from [Throwable]
         * @param error
         * @return [ApiResponse]
         */
        fun <T> create(error: Throwable): ApiErrorResponse<T> = ApiErrorResponse(
            error.message ?: "Unknown error.\nCheck network connection."
        )

        /**
         * Create api response from [Response]
         * @param response
         * @return [ApiResponse]
         */
        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {  // 204 is empty response code
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(body = body)
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(errorMsg ?: "Unknown error.")
            }
        }
    }
}

/**
 * Success api response
 */
data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>()

/**
 * Error api response
 */
data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()

/**
 * Empty api response
 */
class ApiEmptyResponse<T> : ApiResponse<T>()
