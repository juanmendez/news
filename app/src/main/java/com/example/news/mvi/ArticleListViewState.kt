package com.example.news.mvi

import com.example.news.model.Article

/**
 * MVI Architecture: the ViewState contains all the data for the View.
 * ArticleListViewState contains all the data for ArticleListActivity3.
 */
data class ArticleListViewState(
    var articles: List<Article>? = null,
    var query: String? = null,
    var page: Int? = null
)
