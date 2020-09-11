package com.example.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.news.model.Repository2

class ArticleListActivityViewModel2Factory(private val repository2: Repository2) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArticleListActivityViewModel2(repository2) as T
    }
}
