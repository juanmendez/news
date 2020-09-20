package com.example.news.mvi

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.example.news.util.*
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
abstract class NetworkBoundResource<ResponseType, ViewStateType> {

    fun asFlow() = flow<DataState<ViewStateType>> {
        emit(DataState.loading(true))
        when (val apiResponse = fetchFromNetwork()) {
            is ApiSuccessResponse -> {
                emit(DataState.data(null, processResponse(apiResponse)))
            }
            is ApiEmptyResponse -> {
                emit(DataState.error("HTTP 204. Returned NOTHING."))
            }
            is ApiErrorResponse -> {
                emit(DataState.error(apiResponse.errorMessage))
            }
        }
    }

    /**
     * Abstract method to be implemented in concrete classes
     */
    @MainThread
    protected abstract suspend fun fetchFromNetwork(): ApiResponse<ResponseType>

    @WorkerThread
    protected abstract suspend fun processResponse(response: ApiSuccessResponse<ResponseType>): ViewStateType
}
