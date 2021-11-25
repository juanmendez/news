package com.example.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.news.model.Repository
import com.example.news.model.Repository3

/**
 * Factory for the [ArticleListActivityViewModel3].
 * Constructor-injects the [Repository3] instance.
 */
class ArticleListActivityViewModel3Factory(private val repository3: Repository3) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArticleListActivityViewModel3(repository3) as T
    }
}
