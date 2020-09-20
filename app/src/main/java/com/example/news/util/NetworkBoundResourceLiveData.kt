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

import androidx.annotation.WorkerThread
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Generic class for handling network requests
 *
 * CacheObjectType - database cache object type
 * NetworkObjectType - network request object type
 */
abstract class NetworkBoundResourceLiveData<CacheObjectType, NetworkObjectType> {

    private val results: MediatorLiveData<Resource<CacheObjectType>> = MediatorLiveData()

    init {
        results.value = Resource.loading(null)

        val dbSource: LiveData<CacheObjectType> = loadFromDb()

        results.addSource(dbSource, Observer<CacheObjectType> { cacheObject ->
            results.removeSource(dbSource)
            if (shouldFetch(cacheObject)) {
                fetchFromNetwork(dbSource)
            } else {
                results.addSource(dbSource, Observer<CacheObjectType> { cacheObject ->
                    setValue(Resource.success(cacheObject))
                })
            }
        })
    }

    private fun fetchFromNetwork(dbSource: LiveData<CacheObjectType>) {

        results.addSource(dbSource, Observer<CacheObjectType> { cacheObject ->
            setValue(Resource.loading(cacheObject))
        })

        val apiResponse: LiveData<ApiResponse<NetworkObjectType>> = createCall()

        results.addSource(apiResponse, Observer<ApiResponse<NetworkObjectType>> { response ->
            results.removeSource(dbSource)
            results.removeSource(apiResponse)

            when (response) {
                is ApiSuccessResponse -> {
                    GlobalScope.launch(Dispatchers.IO) {

                        saveCallResult(processNetworkObject(response))

                        withContext(Dispatchers.Main) {
                            results.addSource(
                                loadFromDb(),
                                Observer<CacheObjectType> { cacheObject ->
                                    setValue(Resource.success(cacheObject))
                                })
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        results.addSource(loadFromDb(), Observer<CacheObjectType> { cacheObject ->
                            setValue(Resource.success(cacheObject))
                        })
                    }
                }
                is ApiErrorResponse -> {
                    results.addSource(dbSource, Observer<CacheObjectType> { cacheObject ->
                        setValue(Resource.error(response.errorMessage, cacheObject))
                    })
                }
            }
        })
    }

    private fun processNetworkObject(response: ApiSuccessResponse<NetworkObjectType>): NetworkObjectType {
        return response.body
    }

    private fun setValue(newValue: Resource<CacheObjectType>) {
        if (results.value != newValue) {
            results.value = newValue
        }
    }

    // Returns a LiveData object that represents the resource implemented in the base class
    fun asLiveData(): LiveData<Resource<CacheObjectType>> = results

    /**
     * Abstract methods to be implemented in concrete classes
     */

    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract fun saveCallResult(item: NetworkObjectType)

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract fun shouldFetch(data: CacheObjectType?): Boolean

    // Called to get the cached data from the database.
    @MainThread
    protected abstract fun loadFromDb(): LiveData<CacheObjectType>

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<NetworkObjectType>>
}
