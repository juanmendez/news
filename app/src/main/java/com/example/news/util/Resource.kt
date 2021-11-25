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

/**
 * Status of a resource that is provided to the UI.
 *
 * These are usually created by the Repository classes where they return `LiveData<Resource<T>>`
 * to pass back the latest data to the UI with its fetch status.
 *
 * Or the Repository class may return `Flow<Resource<T>>` to the ViewModel and let it collect the
 * data via coroutines and build LiveData for UI updates.
 */
enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

/**
 * A generic class that wraps a generic data of type [T] a message [String] and a [Status].
 *
 * This is done in order to bundle the loading state with the data. This relieves the responsibility
 * on the upper layers (ViewModel) to manage the data state such as the loading state (and its
 * associated progress indicator), the error state (and its error dialog), and finally the nominal
 * success state. The Repository will first emit a Resource to indicate the loading state, later it
 * will emit another Resource once the data is retrieved, and eventually a different Resource in
 * case of an error.
 *
 * @param T the [Resource] data type
 * @param status the [Status] of the [Resource]
 * @param data the [Resource] data
 * @param message the [Resource] error message
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {

        /**
         * Generates a [Status.SUCCESS] [Resource]
         * @param data the [Resource] data
         * @return a success [Resource]
         */
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        /**
         * Generates a [Status.ERROR] [Resource]
         * @param msg the [Resource] error message
         * @param data the [Resource] data
         * @return an error [Resource]
         */
        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        /**
         * Generates a [Status.LOADING] [Resource]
         * @param data the [Resource] data
         * @return a loading [Resource]
         */
        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}
