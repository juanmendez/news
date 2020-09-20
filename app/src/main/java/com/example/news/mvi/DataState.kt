package com.example.news.mvi

data class DataState<T>(
    var message: String? = null,
    var loading: Boolean = false,
    val data: T?
) {
    companion object {

        fun <T> data(
            message: String? = null,
            data: T? = null
        ): DataState<T> {
            return DataState(
                message = message,
                loading = false,
                data = data
            )
        }

        fun <T> error(
            message: String
        ): DataState<T> {
            return DataState(
                message =  message,
                loading = false,
                data = null
            )
        }

        fun <T> loading(
            isLoading: Boolean
        ): DataState<T> {
            return DataState(
                message = null,
                loading = isLoading,
                data = null
            )
        }
    }
}
