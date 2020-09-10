package com.example.news.state

sealed class MainStateEvent {
    class GetArticlesEvent(val query: String) : MainStateEvent()
    class None : MainStateEvent()
}
