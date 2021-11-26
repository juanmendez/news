package com.example.news.mvi

/**
 * DataState is a data wrapper that adds a loading state and an error message to the data.
 *
 * The data and message are wrapped in a consumable [Event] as they will be consumed by the UI and
 * should not be showed again. For example if the Airplane mode is set to ON, the UI will receive
 * and display an error message. If the phone changes orientations that error message will be
 * displayed again (LiveData) unless wrapped into a consumable [Event].
 *
 * The wrapped data is usually the ViewState ([ArticleListViewState] for example)
 *
 * @param T the data type
 * @param message the message [String] wrapped into a consumable [Event]
 * @param loading a [Boolean] indicating the loading state
 * @param data the data wrapped into a consumable [Event]
 */
data class DataState<T>(
    var message: Event<String>? = null,
    var loading: Boolean = false,
    val data: Event<T>?
) {
    companion object {

        /**
         * Generates a [DataState] containing valid data. Wraps the data and message into
         * consumable [Event]
         * @param T the data type
         * @param message the message [String]
         * @param data the data
         * @return the [DataState]
         */
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

        /**
         * Generates a [DataState] containing an error. Wraps the error message into a
         * consumable [Event]
         * @param T the data type
         * @param message the error message [String]
         * @return the [DataState]
         */
        fun <T> error(
            message: String
        ): DataState<T> {
            return DataState(
                message = Event(message),
                loading = false,
                data = null
            )
        }

        /**
         * Generates a [DataState] containing valid data and a loading state. Wraps the data into
         * a consumable [Event]
         * @param T the data type
         * @param isLoading a [Boolean] indicating the loading state
         * @param data the data
         * @return the [DataState]
         */
        fun <T> loading(
            isLoading: Boolean,
            data: T? = null
        ): DataState<T> {
            return DataState(
                message = null,
                loading = isLoading,
                data = Event.dataEvent(data)
            )
        }
    }

    // for logging purposes
    override fun toString(): String {
        val sb = StringBuilder("${javaClass.simpleName}(")
        sb.append("loading=$loading, ")
        sb.append("message=${message?.peekContent() ?: null}, ")
        sb.append("data=${data?.peekContent() ?: null}")
        sb.append(")")
        return sb.toString()
    }
}
