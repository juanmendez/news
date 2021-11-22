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

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

/**
 * Generic class for handling network requests
 *
 * CacheObjectType - database cache object type
 * NetworkObjectType - network request object type
 */
@FlowPreview
@ExperimentalCoroutinesApi
abstract class NetworkBoundResource2<CacheObjectType, NetworkObjectType> {

    /**
     * Creates the flow of data, cache and network.
     */
    fun asFlow() = flow {

        // emit loading
        emit(Resource.loading(null))

        val cacheValue = loadFromCache().first()

        if (shouldFetchFromNetwork(cacheValue)) {

            // emit cache data
            emit(Resource.loading(cacheValue))

            // fetch network data
            when (val apiResponse = fetchFromNetwork()) {
                is ApiSuccessResponse -> {

                    // update cache with network data
                    saveNetworkResponseToCache(processResponse(apiResponse))

                    // emit updated cache data
                    emitAll(loadFromCache().map { Resource.success(it) })
                }
                is ApiEmptyResponse -> {

                    // network didn't return anything, return cache data
                    emitAll(loadFromCache().map { Resource.success(it) })
                }
                is ApiErrorResponse -> {

                    // handle error
                    onFetchFailed()

                    // emit error
                    emitAll(loadFromCache().map { Resource.error(apiResponse.errorMessage, it) })
                }
            }

        } else {

            // emit cache data
            emitAll(loadFromCache().map { Resource.success(it) })
        }
    }

    protected open fun onFetchFailed() {
        // Optional: implement in sub-classes to handle errors
    }

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<NetworkObjectType>) =
        response.body

    /*
     * Abstract methods to be implemented in concrete classes
     */

    /**
     * Whether should fetch network data or not
     * @param data the cache data
     * @return [Boolean] true if network data should be fetched, business logic may be depending on
     * the cache data
     */
    @MainThread
    protected abstract fun shouldFetchFromNetwork(data: CacheObjectType?): Boolean

    /**
     * Fetches data from network
     * @return [ApiResponse] containing the network data
     */
    @MainThread
    protected abstract suspend fun fetchFromNetwork(): ApiResponse<NetworkObjectType>

    /**
     * Saves the network data to cache
     * @param data the network data to be saved to cache
     */
    @WorkerThread
    protected abstract suspend fun saveNetworkResponseToCache(data: NetworkObjectType)

    /**
     * Loads data from cache
     * @return [Flow] of the cache data
     */
    @MainThread
    protected abstract fun loadFromCache(): Flow<CacheObjectType>
}
