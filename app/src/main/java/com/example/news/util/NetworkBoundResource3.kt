package com.example.news.util

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.example.news.mvi.DataState
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
abstract class NetworkBoundResource3<CacheObjectType, NetworkObjectType, ViewStateType> {

    fun asFlow() = flow<DataState<ViewStateType>> {

        // emit loading
        emit(DataState.loading(isLoading = true, data = null))

        val cacheValue = loadFromCache().first()

        if (shouldFetchFromNetwork(cacheValue)) {

            // emit cache data
            emit(DataState.loading(isLoading = true, data = processCache(cacheValue)))

            // fetch network data
            when (val apiResponse = fetchFromNetwork()) {
                is ApiSuccessResponse -> {

                    // update cache with network data
                    saveNetworkResponseToCache(processResponse(apiResponse))

                    // emit updated cache value
                    val updatedCacheValue = loadFromCache().first()
                    emit(DataState.data(message = null, data = processCache(updatedCacheValue)))
                }
                is ApiEmptyResponse -> {

                    // network didn't return anything, return cache data
                    emit(DataState.data(message = null, data = processCache(cacheValue)))
                }
                is ApiErrorResponse -> {

                    // handle error
                    onFetchFailed()

                    // emit error
                    emit(DataState.error(apiResponse.errorMessage))
                }
            }

        } else {

            // emit cache data
            emit(DataState.data(message = null, data = processCache(cacheValue)))
        }
    }

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<NetworkObjectType>) =
        response.body

    /*
     * Abstract method to be implemented in concrete classes
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

    /**
     * Processes cache data
     * @param data the cache data to be processed
     * @return the [ViewStateType] containing the processed [data]
     */
    @WorkerThread
    protected abstract suspend fun processCache(data: CacheObjectType): ViewStateType

    /**
     * Optional: implement in sub-classes to handle errors
     */
    protected open fun onFetchFailed() {}
}
