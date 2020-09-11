package com.example.news.viewmodel

import androidx.lifecycle.*
import com.example.news.model.Article
import com.example.news.model.Repository2
import com.example.news.util.network.Status
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class ArticleListActivityViewModel2(
    private val repository2: Repository2
) : ViewModel(), LifecycleObserver {

    companion object {
        const val TOP_HEADLINES = "Top Headlines"
    }

    private var getArticlesJob: CompletableJob? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        getArticlesJob?.cancel()
    }

    private val _showProgress: MutableLiveData<Boolean> = MutableLiveData()
    val showProgress: LiveData<Boolean> = _showProgress

    // holds tha error message data
    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage
    fun showError(value: String?) {
        if (value != _errorMessage.value) {
            _errorMessage.value = value
            _showProgress.value = false
        }
    }

    private val _query: MutableLiveData<String> = MutableLiveData(TOP_HEADLINES)
    val query: LiveData<String?> = _query
    fun setQuery(query: String) {
        if (query != _query.value) {
            _query.value = query
            _showProgress.value = true
        }
    }

    val articles: LiveData<List<Article>> =
        Transformations.switchMap(_query) { query ->
            Transformations.distinctUntilChanged(
                getArticles(query)
            )
        }

    private fun getArticles(query: String): LiveData<List<Article>> {
        val job = Job()
        getArticlesJob = job

        return liveData(viewModelScope.coroutineContext + Dispatchers.IO + job) {
            repository2.getArticles(query).collect { resource ->
                withContext(Dispatchers.Main) {
                    _showProgress.value = resource.status == Status.LOADING
                }
                resource.data?.let { emit(it) }
                if (resource.status == Status.ERROR) {
                    withContext(Dispatchers.Main) {
                        showError(resource.message.toString())
                    }
                }
            }
        }
    }
}
