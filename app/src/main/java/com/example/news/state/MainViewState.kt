package com.example.news.state

import com.example.news.model.Article

// MVI: contains all the data for the MainActivity
data class MainViewState (
    var articles: List<Article>? = null,
    var query: String? = null
)
