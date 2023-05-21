package com.example.news.mvi

/**
 * A data wrapper transforming the data into a consumable event.
 *
 * Used for exposed via LiveData so that it does not update once consumed. For example if you set
 * the Airplane mode ON, you will get and show an error message. As you rotate the phone that error
 * message will be shown again unless wrapped into a consumable event.
 *
 * @param T the data type
 * @param content the data
 */
class Event<T>(private val content: T) {

    companion object {

        /**
         * Wraps generic data into a consumable [Event]
         * @param T the data type
         * @param data the data to be wrapped
         * @return the data wrapped into an [Event], null if the data is null
         */
        fun <T> dataEvent(data: T?): Event<T>? {
            data?.let {
                return Event(it)
            }
            return null
        }

        /**
         * Wraps a [String] into a consumable [Event]
         * @param message the [String] to be wrapped
         * @return the message wrapped into an [Event], null if the message is null
         */
        fun messageEvent(message: String?): Event<String>? {
            message?.let {
                return Event(message)
            }
            return null
        }
    }

    /**
     * true if the data was consumed, false otherwise
     */
    var consumed = false
        private set // Allow external read but not write

    /**
     * Returns the data if not consumed, otherwise returns null
     * @return the data if it was not consumed, null otherwise
     */
    fun getContentIfNotConsumed(): T? {
        return if (consumed) {
            null
        } else {
            consumed = true
            content
        }
    }

    /**
     * Allows peeking at the data without consuming it
     * @return the data
     */
    fun peekContent(): T = content

    override fun toString(): String {
        return "Event(data=$content, consumed=$consumed)"
    }
}
