package com.example.news.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.news.model.Article
import com.example.news.model.Repository2
import com.example.news.util.DoubleTrigger
import com.example.news.util.network.Status
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

// implements LifecycleObserver so that it can act upon the Activity's lifecycle events
// (if added as an observer to the Activity's lifecycle, in the Activity's code)
class ArticleListActivityViewModel2(
    private val repository2: Repository2
) : ViewModel(), LifecycleObserver {

    companion object {
        const val TOP_HEADLINES = "Top Headlines"
    }

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

    // holds the query data, updated as the user types in a query, and displayed in the ActionBar
    // When creating an empty MutableLiveData by calling MutableLiveData(), its value is null
    // (see MutableLiveData / LiveData source code). Here we initialize the query to Top Headlines
    // which will prompt loading the Top Headlines articles.
    private val _query: MutableLiveData<String> = MutableLiveData(TOP_HEADLINES)
    val query: LiveData<String?> = _query
    fun setQuery(query: String) {
        if (query != _query.value) {
            _query.value = query
            _showProgress.value = true
        }
    }

    private val _page: MutableLiveData<Int> = MutableLiveData(1)
    fun incrementPage() {
        _page.value = _page.value?.plus(1)
    }

    // holds the RecyclerView data
    // The articles data are not modified by the the ViewModel, only rendered,
    // therefore there is no need for MutableLiveData, LiveData will suffice.
    val articles: LiveData<List<Article>> =
        // Fetching the articles depends on the query value, using switchMap utility (uses
        // MediatorLiveData), to transform one LiveData (first switchMap parameter, the query)
        // into another LiveData (switchMap output, the articles) by applying the lambda function
        // (second switchMap parameter) to each value set on the input LiveData (the query).
        Transformations.switchMap(DoubleTrigger(_query, _page)) { doubleTrigger ->
            val query: String = doubleTrigger.first ?: ""
            val page: Int = doubleTrigger.second ?: 1
            val job = Job()

            // making a copy of the Job instance reference value (pointer to the job Object
            // instance) so that we can cancel the Job if needed
            getArticlesJob = job

            // Another utility that creates a new LiveData ONLY IF the source LiveData (the query)
            // value has changed, often used with data backing RecyclerViews. Note that this is
            // redundant to the code logic we implemented in the setQuery function above.
            Transformations.distinctUntilChanged(
                liveData(viewModelScope.coroutineContext + Dispatchers.IO + job) {
                    try {
                        val flow = when (query) {
                            TOP_HEADLINES -> repository2.getTopHeadlines(page)
                            else -> repository2.getArticles(query, page)
                        }

                        // collect the Flow's List<Article> objects
                        flow.collect { resource ->

                            // update progress bar
                            withContext(Dispatchers.Main) {
                                _showProgress.value = resource.status == Status.LOADING
                            }

                            // update articles list data
                            resource.data?.let {
                                // each emit suspends this block's execution until
                                // the LiveData is set on the main thread
                                Log.d("toto", "display: ${it.size}")
                                emit(it)
                            }

                            // update error message
                            if (resource.status == Status.ERROR) {
                                withContext(Dispatchers.Main) {
                                    showError(resource.message.toString())
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // catch Retrofit exception thrown in Airplane mode
                        withContext(Dispatchers.Main) {
                            showError(e.message.toString())
                        }
                    }
                }
            )
        }
}
