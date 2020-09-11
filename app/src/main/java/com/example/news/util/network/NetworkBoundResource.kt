package com.example.news.util.network

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
abstract class NetworkBoundResource<CacheObjectType, NetworkObjectType> {

    fun asFlow() = flow {
        emit(Resource.loading(null))

        val dbValue = loadFromDb().first()
        if (shouldFetch(dbValue)) {
            emit(Resource.loading(dbValue))
            when (val apiResponse = fetchFromNetwork()) {
                is ApiSuccessResponse -> {
                    saveNetworkResult(processResponse(apiResponse))
                    emitAll(loadFromDb().map { Resource.success(it) })
                }
                is ApiEmptyResponse -> {
                    emitAll(loadFromDb().map { Resource.success(it) })
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    emitAll(loadFromDb().map { Resource.error(apiResponse.errorMessage, it) })
                }
            }
        } else {
            emitAll(loadFromDb().map { Resource.success(it) })
        }
    }

    protected open fun onFetchFailed() {
        // Implement in sub-classes to handle errors
    }

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<NetworkObjectType>) =
        response.body

    @WorkerThread
    protected abstract suspend fun saveNetworkResult(item: NetworkObjectType)

    @MainThread
    protected abstract fun shouldFetch(data: CacheObjectType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): Flow<CacheObjectType>

    @MainThread
    protected abstract suspend fun fetchFromNetwork(): ApiResponse<NetworkObjectType>
}
