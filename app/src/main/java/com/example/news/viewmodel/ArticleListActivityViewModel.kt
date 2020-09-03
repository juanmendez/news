package com.example.news.viewmodel

import androidx.lifecycle.*
import com.example.news.model.Article
import com.example.news.model.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

// implements LifecycleObserver so that it can act upon the Activity's lifecycle events
// (if added as an observer to the Activity's lifecycle, in the Activity's code)
class ArticleListActivityViewModel (
    private val repository: Repository
) : ViewModel(), LifecycleObserver {

    companion object {
        const val TOP_HEADLINES = "Top Headlines"
    }

    private var getArticlesJob: CompletableJob? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        getArticlesJob?.cancel()
    }

    // holds tha error message data
    private val _errorMessage : MutableLiveData<String?> = MutableLiveData()
    val errorMessage : LiveData<String?> = _errorMessage
    fun showError(value: String?) {
        if (value != _errorMessage.value) {
            _errorMessage.value = value
            _showProgress.value = false
        }
    }

    // holds the query data, updated as the user types in a query, and displayed in the ActionBar
    // initialized to "Top Headlines" which triggers fetching articles on app start
    private val _query: MutableLiveData<String> = MutableLiveData(TOP_HEADLINES)
    val query : LiveData<String?> = _query
    fun setQuery(query: String) {
        if (query != _query.value) {
            _query.value = query
            _showProgress.value = true
        }
    }

    // holds the ProgressBar data
    private val _showProgress: MutableLiveData<Boolean> = MutableLiveData()
    val showProgress : LiveData<Boolean> = _showProgress

    // holds the RecyclerView data
    // switchMap would allow to update the UI "live" as the user would for example
    // type into an EditText and upon each typed character we would invoke setQuery
    // This capability is not used here
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

                        // remove progress bar
                        withContext(Dispatchers.Main) {
                            _showProgress.value = false
                        }
                    }
                } catch (e: Exception) {
                    // catch exceptions propagated from data sources through
                    // the repository and inform the user by updating the UI
                    withContext(Dispatchers.Main) {
                        showError(e.message.toString())
                    }
                }
            }
        }
    }

//    val articles: LiveData<List<Article>> = Transformations.switchMap(_query) { query ->
//        getArticlesJob = Job()
//        getArticlesJob?.let { job ->
//            liveData(viewModelScope.coroutineContext + Dispatchers.IO + job) {
//                try {
//                    emit(repository.getCachedArticles(query))
//                    withContext(Dispatchers.Main) {
//                        _showProgress.value = false
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        showError(true)
//                    }
//                }
//            }
//        }
//    }
}
