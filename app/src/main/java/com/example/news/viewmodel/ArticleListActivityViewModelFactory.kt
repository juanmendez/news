package com.example.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.news.model.Repository

/**
 * Factory for the [ArticleListActivityViewModel].
 * Constructor-injects the [Repository] instance.
 */
@Suppress("UNCHECKED_CAST")
class ArticleListActivityViewModelFactory(
    private val repository: com.example.news.model.Repository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArticleListActivityViewModel(repository) as T
    }
}
