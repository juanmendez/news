package com.example.news.network.mvi

/**
 * MVI Architecture: the ViewState contains all the data for the View.
 * ArticleListViewState contains all the data for ArticleListActivity3.
 */
data class ArticleListViewState(
    var articles: List<com.example.news.data.Article>? = null,
    var query: String? = null,
    var page: Int? = null
)
