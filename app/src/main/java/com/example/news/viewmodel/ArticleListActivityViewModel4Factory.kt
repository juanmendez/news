package com.example.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.news.model.Repository3

/**
 * Factory for the [ArticleListActivityViewModel4].
 * Constructor-injects the [Repository3] instance.
 */
@Suppress("UNCHECKED_CAST")
class ArticleListActivityViewModel4Factory(
    private val repository3: Repository3
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArticleListActivityViewModel4(repository3) as T
    }
}