package com.example.news.viewmodel

import androidx.lifecycle.*
import com.example.news.model.Article
import com.example.news.model.Repository
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MainActivityViewModel (private val repository: Repository) : ViewModel(), LifecycleObserver {

    private var getArticlesJob: CompletableJob? = null

    private val _query: MutableLiveData<String> = MutableLiveData()

    val articles: LiveData<List<Article>> = Transformations.switchMap(_query) { query ->
        getArticlesJob = Job()
        getArticlesJob?.let { job ->
            // we create LiveData here instead of having the Repository returning
            // LiveData, that way the Repository does not depend on Android APIs,
            // thus we can Unit Test it in isolation
            liveData(Dispatchers.IO + job) {
                emit(repository.getArticles(query))
            }
        }
    }

    fun setQuery(query: String) {
        if (query != _query.value) {
            _query.value = query
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        getArticlesJob?.cancel()
    }
}
