package com.example.news.model

import androidx.lifecycle.LiveData

interface Repository {
    fun getArticles(query: String) : LiveData<List<Article>>
    fun cancelJobs()
}