package com.example.news.state

import com.example.news.model.Article

// MVI: contains all the data for the ArticleListActivity3
data class ArticleListViewState (
    var articles: List<Article>? = null,
    var query: String? = null
)
