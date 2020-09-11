package com.example.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.news.model.Repository

class ArticleListActivityViewModel3Factory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArticleListActivityViewModel3(repository) as T
    }
}
