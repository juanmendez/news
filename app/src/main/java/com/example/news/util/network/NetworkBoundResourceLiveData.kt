package com.example.news.util.network

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
 */
// CacheObject - comes from cache (database cache)
// NetworkObject - comes from network API (network request)
abstract class NetworkBoundResourceLiveData<CacheObjectType, NetworkObjectType> {

    private val results: MediatorLiveData<Resource<CacheObjectType>> = MediatorLiveData()

    init {
        // update LiveData for loading status
        results.value = Resource.loading(null)

        // observe LiveData from cache
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
        // update LiveData for loading status
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

                        // save data to db
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
                    // API didn't return anything, we still return the cache data
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

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    fun asLiveData(): LiveData<Resource<CacheObjectType>> = results
}
