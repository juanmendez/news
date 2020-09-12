package com.example.news.util.network

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

/**
 * Generic class for handling network requests
 */
@FlowPreview
@ExperimentalCoroutinesApi
abstract class NetworkBoundResource<CacheObjectType, NetworkObjectType> {

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

    /**
     * Abstract methods to be implemented in concrete classes
     */
    @MainThread
    protected abstract fun shouldFetchFromNetwork(data: CacheObjectType?): Boolean

    @MainThread
    protected abstract suspend fun fetchFromNetwork(): ApiResponse<NetworkObjectType>

    @WorkerThread
    protected abstract suspend fun saveNetworkResponseToCache(item: NetworkObjectType)

    @MainThread
    protected abstract fun loadFromCache(): Flow<CacheObjectType>
}
