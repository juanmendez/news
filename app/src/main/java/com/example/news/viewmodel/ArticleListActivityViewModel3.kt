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
import com.example.news.model.Repository3
import com.example.news.mvi.ArticleListStateEvent
import com.example.news.mvi.ArticleListViewState
import com.example.news.mvi.DataState
import com.example.news.util.AbsentLiveData
import com.example.news.util.TOP_HEADLINES
import com.example.news.util.log
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect

/**
 * ViewModel for the ArticleListActivity3. Maintains its data and business logic.
 *
 * Implements LifecycleObserver so that it can act upon the Activity's lifecycle events if needed.
 * It requires that the Activity holding this ViewModel's instance adding the ViewModel as an
 * observer to the Activity's lifecycle in the Activity's code:
 * lifecycle.addObserver(viewModel)
 */
class ArticleListActivityViewModel3(
    private val repository3: Repository3
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

    private val _stateEvent: MutableLiveData<ArticleListStateEvent> = MutableLiveData(
        /**
         * initial ViewState value that triggers fetching the top headlines
         */
        ArticleListStateEvent.GetArticlesEvent(TOP_HEADLINES, 1)
    )
    private val _viewState: MutableLiveData<ArticleListViewState> = MutableLiveData()

    /**
     * MVI Architecture: all the View data is maintained into a single ViewState LiveData
     * object exposed to the View for observation.
     */
    val viewState: LiveData<ArticleListViewState> = _viewState

    /**
     * MVI Architecture: in response to StateEvents received from the View, ViewStates
     * wrapped in DataStates (for adding loading and error state) are emitted from the
     * Repository to the the View.
     */
    val dataState: LiveData<DataState<ArticleListViewState>> =
        Transformations.switchMap(_stateEvent) { stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    /**
     * MVI Architecture: upon user actions events are sent from View to ViewModel.
     * This function handles the events received from the View.
     */
    private fun handleStateEvent(stateEvent: ArticleListStateEvent): LiveData<DataState<ArticleListViewState>> {
        val job = Job()

        // making a copy of the Job instance reference value (pointer to the job Object instance)
        // so that we can cancel the Job if needed
        getArticlesJob = job

        return when (stateEvent) {

            // handle GetArticlesEvent event
            is ArticleListStateEvent.GetArticlesEvent -> {
                return liveData(viewModelScope.coroutineContext + Dispatchers.IO + job) {
                    try {
                        log("toto", "GetArticlesEvent: query=${stateEvent.query} page=${stateEvent.page}")
                        repository3.getArticles(stateEvent.query, stateEvent.page).collect { dataState ->
                            emit(dataState)
                        }
                    } catch (e: Exception) {
                        emit(DataState.error<ArticleListViewState>("Error. Check network status."))
                    }
                }
            }

            // handle IncrementPage event
            is ArticleListStateEvent.IncrementPageEvent -> {
                return liveData(viewModelScope.coroutineContext + Dispatchers.IO + job) {
                    getCurrentViewStateOrNew().let {
                        val query = it.query
                        var page = it.page
                        if (!query.isNullOrBlank() && page != null) {
                            try {
                                page++
                                log("toto", "IncrementPageEvent: query=$query page=$page")
                                repository3.getArticles(query, page).collect { dataState ->
                                    emit(dataState)
                                }
                            } catch (e: Exception) {
                                emit(DataState.error<ArticleListViewState>("Error. Check network status."))
                            }
                        }
                    }
                }
            }

            // handle Refresh event
            is ArticleListStateEvent.RefreshEvent -> {
                return liveData(viewModelScope.coroutineContext + Dispatchers.IO + job) {
                    getCurrentViewStateOrNew().let {
                        val query = it.query
                        if (!query.isNullOrBlank()) {
                            try {
                                repository3.deleteArticles(query)
                                log("toto", "RefreshEvent: query=$query page=1")
                                repository3.getArticles(query, 1).collect { dataState ->
                                    emit(dataState)
                                }
                            } catch (e: Exception) {
                                emit(DataState.error<ArticleListViewState>("Error. Check network status."))
                            }
                        }
                    }
                }
            }

            // handle None event
            is ArticleListStateEvent.NoneEvent -> {
                AbsentLiveData.create()
            }
        }
    }

    private fun getCurrentViewStateOrNew(): ArticleListViewState {
        return viewState.value ?: ArticleListViewState()
    }

    /**
     * Updates the ViewState value
     * Called by the UI to update the ViewState
     */
    fun updateViewState(articleListViewState: ArticleListViewState) {
        val update = getCurrentViewStateOrNew()
        articleListViewState.articles?.let { articles ->
            update.articles = articles
        }
        articleListViewState.query?.let { query ->
            update.query = query
        }
        articleListViewState.page?.let { page ->
            update.page = page
        }
        _viewState.value = update
    }

    /**
     * MVI Architecture: upon user actions events are sent from View to ViewModel.
     * This function receives the event from the View and triggers handleStateEvent
     * to process the event.
     */
    fun setStateEvent(event: ArticleListStateEvent) {
        _stateEvent.value = event
    }
}
