package com.example.news.viewmodel

import androidx.lifecycle.*
import com.example.news.model.Article
import com.example.news.model.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class ArticleListActivityViewModel (
    private val repository: Repository
) : ViewModel(), LifecycleObserver {

    private var getArticlesJob: CompletableJob? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        getArticlesJob?.cancel()
    }

    private val _errorMessage : MutableLiveData<String?> = MutableLiveData()
    val errorMessage : LiveData<String?> = _errorMessage
    fun showError(value: String?) {
        if (value != _errorMessage.value) {
            _errorMessage.value = value
        }
    }

    private val _query: MutableLiveData<String> = MutableLiveData()
    fun setQuery(query: String) {
        if (query != _query.value) {
            _query.value = query
        }
    }

//    val articles: LiveData<List<Article>> = Transformations.switchMap(_query) { query ->
//        getArticlesJob = Job()
//        getArticlesJob?.let { job ->
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

                // create a coroutine and return its future result
                // (of type Flow) as an implementation of Deferred
                val data = getArticlesAsync(query)

                try {
                    data
                        .await() // get the Deferred Flow
                        .collect { // collect the Flow's List<Article> objects
                            emit(it) // emit each List<Article> to the LiveData builder
                        }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showError(e.message.toString())
                    }
                }
            }
        }
    }

    private fun getArticlesAsync(query: String) = viewModelScope.async {
        repository.getArticles(query) // Flow
    }
}
