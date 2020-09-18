package com.example.news.viewmodel

import androidx.lifecycle.*
import com.example.news.model.Article
import com.example.news.model.Repository
import com.example.news.util.TOP_HEADLINES
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

// implements LifecycleObserver so that it can act upon the Activity's lifecycle events
// (if added as an observer to the Activity's lifecycle, in the Activity's code)
class ArticleListActivityViewModel (
    private val repository: Repository
) : ViewModel(), LifecycleObserver {

    private var getArticlesJob: CompletableJob? = null

    // acting on the Activity's lifecycle: if the Activity is destroyed we cancel the Job, not all
    // that necessary as the LiveData would not update the UI anyway (it is lifecycle aware)
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        getArticlesJob?.cancel()
    }

    // holds the ProgressBar data
    // MutableLiveData extends LiveData and exposes LiveData's protected methods setValue and
    // postValue thus allowing updating the stored data. Usually MutableLiveData is used in the
    // ViewModel to hold the data and then the ViewModel only exposes LiveData reference copies.
    private val _showProgress: MutableLiveData<Boolean> = MutableLiveData()
    // Reference copy exposed to the Activity for being observed. LiveData has no publicly available
    // methods to update the stored data (LiveData's setValue is protected) therefore it is safe to
    // be exposed to the Activity: the Activity will not be able to modify the LiveData value.
    val showProgress : LiveData<Boolean> = _showProgress

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
    // When creating an empty MutableLiveData by calling MutableLiveData(), its value is null
    // (see MutableLiveData / LiveData source code). Here we initialize the query to Top Headlines
    // which will prompt loading the Top Headlines articles.
    private val _query: MutableLiveData<String> = MutableLiveData(TOP_HEADLINES)
    val query : LiveData<String?> = _query
    fun setQuery(query: String) {
        if (query != _query.value) {
            _query.value = query
            _showProgress.value = true
        }
    }

    // holds the RecyclerView data
    // The articles data are not modified by the the ViewModel, only rendered,
    // therefore there is no need for MutableLiveData, LiveData will suffice.
    val articles: LiveData<List<Article>> =
        // Fetching the articles depends on the query value, using switchMap utility (uses
        // MediatorLiveData), to transform one LiveData (first switchMap parameter, the query)
        // into another LiveData (switchMap output, the articles) by applying the lambda function
        // (second switchMap parameter) to each value set on the input LiveData (the query).
        Transformations.switchMap(_query) { query ->
            // Another utility that creates a new LiveData ONLY IF the source LiveData (the query)
            // value has changed, often used with data backing RecyclerViews. Note that this is
            // redundant to the code logic we implemented in the setQuery function above.
            Transformations.distinctUntilChanged(
                getArticles(query)
            )
    }

    private fun getArticles(query: String): LiveData<List<Article>> {
        val job = Job()

        // making a copy of the Job instance reference value (pointer to the job Object instance)
        // so that we can cancel the Job if needed
        getArticlesJob = job

        // Using liveData builder to create a coroutine that will call the Repository asynchronously
        // and return the LiveData values, instead of having the Repository returning LiveData, that
        // way the Repository does not depend on Android APIs, therefore it can be Unit Tested
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO + job) {
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
