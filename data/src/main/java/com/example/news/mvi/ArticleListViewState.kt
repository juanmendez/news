package com.example.news.mvi

/**
 * MVI Architecture: the ViewState contains all the data for the View.
 * ArticleListViewState contains all the data for ArticleListActivity3.
 */
data class ArticleListViewState(
    var articles: List<com.example.news.model.Article>? = null,
    var query: String? = null,
    var page: Int? = null
)
