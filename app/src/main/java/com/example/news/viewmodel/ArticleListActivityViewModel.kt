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
//            liveData(viewModelScope.coroutineContext + Dispatchers.IO + job) {
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
            // the Repository asynchronously, and emit the LiveData values,
            // instead of having the Repository returning LiveData, that way
            // the Repository does not depend on Android APIs, therefore it
            // can be Unit Tested
            liveData(viewModelScope.coroutineContext + Dispatchers.IO + job) {
                try {
                    // collect the Flow's List<Article> objects
                    repository.getArticles(query).collect {
                        // each emit suspends this block's execution until
                        // the LiveData is set on the main thread
                        emit(it)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showError(e.message.toString())
                    }
                }
            }
        }
    }
}