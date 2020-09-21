package com.example.news.mvi

/**
 * MVI Architecture: upon user actions events are sent from View to ViewModel.
 * These are the events that can be sent from ArticleListActivity3 to ArticleListActivityViewModel3
 * in response to user actions. The user can perform a single action in the activity: perform a
 * search query. This will trigger getting articles, thus the GetArticlesEvent, which obviously
 * holds the search query.
 */
sealed class ArticleListStateEvent {
    class GetArticlesEvent(val query: String) : ArticleListStateEvent()
    class None : ArticleListStateEvent()
}
