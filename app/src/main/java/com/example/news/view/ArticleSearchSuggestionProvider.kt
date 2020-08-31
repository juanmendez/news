package com.example.news.view

import android.content.SearchRecentSuggestionsProvider

class ArticleSearchSuggestionProvider : SearchRecentSuggestionsProvider() {

    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.example.news.ArticleSearchSuggestionProviderAuthority"
        const val MODE: Int = DATABASE_MODE_QUERIES
    }
}
