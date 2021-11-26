package com.example.news.mvi

/**
 * MVI Architecture: upon user actions events are sent from View to ViewModel.
 */
sealed class ArticleListStateEvent {

    /**
     * Event sent from the View to the ViewModel when the user performs an articles search.
     * @param query the search query [String] value
     * @param page the page [Int] value
     */
    class GetArticlesEvent(val query: String, val page: Int) : ArticleListStateEvent()

    /**
     * Event sent from the View to the ViewModel when the user scrolls to the bottom of the
     * articles list.
     * This sub-class is converted to an object as it has no state and no overridden equals.
     */
    object IncrementPageEvent : ArticleListStateEvent()

    /**
     * Event sent from the View to the ViewModel when the user performs a swipe to refresh.
     * This sub-class is converted to an object as it has no state and no overridden equals.
     */
    object RefreshEvent : ArticleListStateEvent()

    /**
     * No data event.
     * This sub-class is converted to an object as it has no state and no overridden equals.
     */
    object NoneEvent : ArticleListStateEvent()
}
