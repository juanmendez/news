package com.example.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.Lifecycle
import com.example.news.model.Article
import com.example.news.model.Repository2
import com.example.news.util.TOP_HEADLINES
import com.example.news.util.log
import com.example.news.util.Status
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect

/**
 * ViewModel for the ArticleListActivity2. Maintains its data and business logic.
 *
 * Implements LifecycleObserver so that it can act upon the Activity's lifecycle events if needed.
 * It requires that the Activity holding this ViewModel's instance adding the ViewModel as an
 * observer to the Activity's lifecycle in the Activity's code:
 * lifecycle.addObserver(viewModel)
 */
class ArticleListActivityViewModel2(
    private val repository2: Repository2
) : ViewModel(), LifecycleObserver {

    // holds a reference to the Job getting the articles so that it can be cancelled when the
    // Activity is destroyed
    private var getArticlesJob: CompletableJob? = null

    /**
     * Called when the Activity holding an instance of this ViewModel transitions into the onDestroy
     * state. It requires the Activity to add the ViewModel instance as an observer of the Activity
     * lifecycle in the Activity's code:
     * lifecycle.addObserver(viewModel)
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        // Cancel the Job that is fetching the articles data. May not be necessary as the LiveData
        // would not update the UI anyway since it is lifecycle aware.
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
    /**
     * Holds the [Boolean] indicating whether the UI shall show the progress bar.
     * Exposed to the UI so that the UI can observe it and display/hide the progress bar.
     */
    val showProgress: LiveData<Boolean> = _showProgress

    // holds tha error message data
    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()

    /**
     * Holds the error message [String].
     * Exposed to the UI so that the UI can observe it and display the error message dialog.
     */
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Updates the error message value.
     * Called by the UI to clear the error message value once the user dismisses the error message
     * dialog.
     */
    fun showError(value: String?) {
        if (value != _errorMessage.value) {
            _errorMessage.value = value
        }
    }

    // holds tha refreshing data, true if the user initiated a refresh, false once the data was
    // fetched
    private var _refreshing: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * Holds the [Boolean] indicating the refreshing state. True if refreshing is in progress,
     * false otherwise.
     * Exposed to the UI so that the UI can observe it and hide the swipe refresh UI.
     */
    val refreshing: LiveData<Boolean> = _refreshing

    /**
     * If there is a query string entered by the user this function will delete all the cached
     * articles matching the given query and fetch the first page from the network matching the
     * query.
     * Called by the UI when the user swipes to refresh.
     */
    fun refresh() {
        val query = _trigger.value?.first
        if (!query.isNullOrBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository2.deleteArticles(query)
                withContext(Dispatchers.Main) {
                    _trigger.apply {
                        value = query to 1
                    }
                }
            }
        }
    }

    // The trigger mutable live data holding the pair of values query and page (needed because the
    // API is paginated)
    //
    // The trigger is updated
    // - when the user types in a new query (displayed in the ActionBar): (new query, 1)
    // - when the user scrolls to the bottom (requesting a new page): (query, index + 1)
    //
    // The trigger is initialized to ("Top Headlines", 1)
    private val _trigger: MutableLiveData<Pair<String?, Int?>> = MutableLiveData(TOP_HEADLINES to 1)

    /**
     * Holds the tuple query value and page value.
     * Exposed to the UI so that the UI can observe it and update the Action Bar text and scroll to
     * the top if the query value has changed.
     */
    val trigger: LiveData<Pair<String?, Int?>> = _trigger

    /**
     * Updates the [trigger] by updating its query value.
     * Called by the UI when the user types in a query.
     */
    fun setQuery(query: String) {
        _trigger.apply {
            // if the user typed in a new query then update the trigger value to (query, 1)
            if (value != null && query != value?.first) {
                value = query to 1
            }
        }
    }

    // used to avoid multiple trigger updates as the user scrolls to the bottom
    private var pageIncrementTimestamp: Long = 0

    /**
     * Updates the [trigger] by incrementing the page value.
     * Called by the UI when the user scrolls to the bottom of the articles list.
     */
    fun incrementPage() {
        val now = System.currentTimeMillis()
        if (now - pageIncrementTimestamp > 1000) {
            pageIncrementTimestamp = now
            // the user scrolled to the bottom, increment the page
            _trigger.apply {
                value = value?.first to value?.second?.plus(1)
            }
        }
    }

    // The articles data is not modified by the ViewModel or the UI, only rendered,
    // therefore there is no need for MutableLiveData, LiveData will suffice.
    /**
     * Holds the list of articles to be displayed by the UI.
     */
    val articles: LiveData<List<Article>> =
        // Fetching the articles depends on the trigger value, using switchMap utility (which uses
        // MediatorLiveData), to transform one LiveData (first switchMap parameter, the trigger)
        // into another LiveData (switchMap output, the articles) by applying the lambda function
        // (second switchMap parameter) to each value set on the input LiveData (the trigger).
        Transformations.switchMap(_trigger) { trigger ->

            log("toto", "trigger = $trigger")

            val query: String = trigger.first ?: ""
            val page: Int = trigger.second ?: 1
            val job = Job()

            // making a copy of the Job instance reference value (pointer to the job Object
            // instance) so that we can cancel the Job if needed
            getArticlesJob = job

            // distinctUntilChanged utility creates a new LiveData ONLY IF the source LiveData
            // (the trigger) value has changed, often used with data backing RecyclerViews
            Transformations.distinctUntilChanged(
                liveData(viewModelScope.coroutineContext + Dispatchers.IO + job) {
                    try {
                        val flow = when (query) {
                            TOP_HEADLINES -> repository2.getTopHeadlines(page)
                            else -> repository2.getArticles(query, page)
                        }

                        // collect the Flow's List<Article> objects
                        flow.collect { resource ->

                            // manage loading state by simply checking the emitted resource status
                            // The loading state is bundled within the Resource and the ViewModel
                            // does not have to manage it: the ViewModel does not have to show the
                            // progress bar before calling the Repository's getArticles.
                            withContext(Dispatchers.Main) {
                                _showProgress.value = resource.status == Status.LOADING
                            }

                            // update articles list data
                            resource.data?.let {
                                // each emit suspends this block's execution until
                                // the LiveData is set on the main thread
                                emit(it)
                            }

                            // manage error state by simply checking the emitted resource status
                            // The error state is bundled within the Resource and the ViewModel
                            // does not have to manage it: the ViewModel does not have to catch
                            // an exception thrown by the Repository methods for error handling.
                            if (resource.status == Status.ERROR) {
                                withContext(Dispatchers.Main) {
                                    showError(resource.message.toString())
                                }
                            }

                            // update refresh spinner
                            if (page == 1) {
                                withContext(Dispatchers.Main) {
                                    _refreshing.value = false
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // catch Retrofit exception thrown in Airplane mode
                        withContext(Dispatchers.Main) {
                            _showProgress.value = false
                            showError(e.message.toString())
                        }
                    }
                }
            )
        }
}
