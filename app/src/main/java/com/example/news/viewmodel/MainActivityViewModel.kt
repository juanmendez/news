package com.example.news.viewmodel

import androidx.lifecycle.*
import com.example.news.model.Article
import com.example.news.model.Repository

class MainActivityViewModel (private val repository: Repository) : ViewModel(), LifecycleObserver {

    private val _query: MutableLiveData<String> = MutableLiveData()

    val articles: LiveData<List<Article>> = Transformations.switchMap(_query) {
        repository.getArticles(it)
    }

    fun setQuery(query: String) {
        if (query != _query.value) {
            _query.value = query
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        repository.cancelJobs()
    }
}
