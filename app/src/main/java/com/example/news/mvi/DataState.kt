package com.example.news.mvi

import com.example.news.util.Status

/**
 * Data wrapper that adds state and error message to data.
 *
 * This is done in order to bundle the loading state and an error message with the data. This
 * relieves the upper layers (ViewModel) from the responsibility of managing the data state such
 * as the loading state (and its associated progress indicator), the error state (and its error
 * dialog), and finally the nominal success state. The Repository will first emit a Resource to
 * indicate the loading state, later it will emit another Resource once the data is retrieved,
 * and eventually a different Resource in case of an error.
 *
 * The data and message are wrapped in a consumable [Event] as they will be consumed by the UI and
 * should not be showed again. For example if the Airplane mode is set to ON, the UI will receive
 * and display an error message. If the phone changes orientations that error message will be
 * displayed again (LiveData) unless wrapped into a consumable [Event].
 *
 * The wrapped data is usually the ViewState ([ArticleListViewState] for example)
 *
 * @param T the data type
 * @param status the [Status] of the [DataState]
 * @param data the data wrapped into a consumable [Event]
 * @param message the message [String] wrapped into a consumable [Event]
 */
data class DataState<T>(
    val status: Status,
    val data: Event<T>?,
    val message: Event<String>?
) {
    companion object {

        /**
         * Generates a [Status.SUCCESS] [DataState]. Wraps both the data and message into
         * respective consumable [Event].
         * @param T the data type
         * @param data the data
         * @param message the message [String]
         * @return the [DataState]
         */
        fun <T> success(
            data: T? = null,
            message: String? = null
        ): DataState<T> {
            return DataState(
                status = Status.SUCCESS,
                data = Event.dataEvent(data),
                message = Event.messageEvent(message)
            )
        }

        /**
         * Generates a [Status.ERROR] [DataState]. Wraps the message into a consumable [Event].
         * @param T the data type
         * @param message the error message [String]
         * @return the [DataState]
         */
        fun <T> error(
            message: String
        ): DataState<T> {
            return DataState(
                status = Status.ERROR,
                data = null,
                message = Event(message)
            )
        }

        /**
         * Generates a [Status.LOADING] [DataState]. Wraps the data into a consumable [Event].
         * @param T the data type
         * @param data the data
         * @return the [DataState]
         */
        fun <T> loading(
            data: T? = null
        ): DataState<T> {
            return DataState(
                status = Status.LOADING,
                data = Event.dataEvent(data),
                message = null
            )
        }
    }

    // for logging purposes
    override fun toString(): String {
        val sb = StringBuilder("${javaClass.simpleName}(")
        sb.append("status=${status.name}, ")
        sb.append("message=${message?.peekContent() ?: null}, ")
        sb.append("data=${data?.peekContent() ?: null}")
        sb.append(")")
        return sb.toString()
    }
}
