package com.example.news.mvi

/**
 * DataState is a ViewState wrapper that adds a loading state and an error message to the View data.
 *
 * Its data and message are wrapped in an Event as they will be consumed by the UI and should
 * not be showed again. For example if you set the Airplane mode ON, you will get and show an
 * error message. As you rotate the phone that error message will be shown again unless wrapped
 * in a consumable Event.
 */
data class DataState<T>(
    var message: Event<String>? = null,
    var loading: Boolean = false,
    val data: Event<T>?
) {
    companion object {

        fun <T> data(
            message: String? = null,
            data: T? = null
        ): DataState<T> {
            return DataState(
                message = Event.messageEvent(message),
                loading = false,
                data = Event.dataEvent(data)
            )
        }

        fun <T> error(
            message: String
        ): DataState<T> {
            return DataState(
                message =  Event(message),
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
