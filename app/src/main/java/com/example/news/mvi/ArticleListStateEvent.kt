package com.example.news.mvi

sealed class ArticleListStateEvent {
    class GetArticlesEvent(val query: String) : ArticleListStateEvent()
    class None : ArticleListStateEvent()
}
