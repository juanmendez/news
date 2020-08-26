package com.example.news.viewmodel

import androidx.lifecycle.*
import com.example.news.model.Article
import com.example.news.model.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class MainActivityViewModel (
    private val repository: Repository
) : ViewModel(), LifecycleObserver {

    private var getArticlesJob: CompletableJob? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        getArticlesJob?.cancel()
    }

    private val _showError : MutableLiveData<Boolean> = MutableLiveData()
    val showError : LiveData<Boolean> = _showError
    fun showError(value: Boolean) {
        if (value != _showError.value) {
            _showError.value = value
        }
    }

    private val _query: MutableLiveData<String> = MutableLiveData()
    fun setQuery(query: String) {
        if (query != _query.value) {
            _query.value = query
        }
    }

//    // switchMap allows to update the UI "live" as the user types in an EditText
//    // and invokes setQuery to update the query with each typed character
//    val articles: LiveData<List<Article>> = Transformations.switchMap(_query) { query ->
//        getArticlesJob = Job()
//        getArticlesJob?.let { job ->
//            // We use a LiveData builder to create a coroutine that will run
//            // the Repository asynchronously, consume the response, and emit
//            // a LiveData value, instead of having the Repository returning
//            // LiveData, that way the Repository does not depend on Android
//            // APIs, thus it can be Unit Tested in isolation
//            liveData(Dispatchers.IO + job) {
//                try {
//                    emit(repository.getCachedArticles(query))
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        showError(true)
//                    }
//                }
//            }
//        }
//    }

    // switchMap allows to update the UI "live" as the user types in an EditText
    // and invokes setQuery to update the query with each typed character
    val articles: LiveData<List<Article>> = Transformations.switchMap(_query) { query ->
        getArticlesJob = Job()
        getArticlesJob?.let { job ->
            // We use a LiveData builder to create a coroutine that will run
            // the Repository asynchronously, consume the response, and emit
            // a LiveData value, instead of having the Repository returning
            // LiveData, that way the Repository does not depend on Android
            // APIs, thus it can be Unit Tested in isolation
            liveData(Dispatchers.IO + job) {
                val data = getArticlesAsync(query)
                data.await().collect { emit(it) }
            }
        }
    }

    private fun getArticlesAsync(query: String) = viewModelScope.async(Dispatchers.IO) {
        repository.getArticles(query)
    }
}
