package com.example.news.state

sealed class ArticleListStateEvent {
    class GetArticlesEvent(val query: String) : ArticleListStateEvent()
    class None : ArticleListStateEvent()
}
