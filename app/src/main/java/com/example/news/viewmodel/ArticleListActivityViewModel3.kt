package com.example.news.viewmodel

import androidx.lifecycle.*
import com.example.news.model.Repository3
import com.example.news.mvi.ArticleListStateEvent
import com.example.news.mvi.ArticleListViewState
import com.example.news.mvi.DataState
import com.example.news.util.AbsentLiveData
import com.example.news.util.TOP_HEADLINES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

class ArticleListActivityViewModel3(
    private val repository3: Repository3
) : ViewModel(), LifecycleObserver {

    private val _stateEvent: MutableLiveData<ArticleListStateEvent> = MutableLiveData(
        /**
         * initial StateEvent value that triggers fetching the top headlines
         */
        ArticleListStateEvent.GetArticlesEvent(TOP_HEADLINES)
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
        return when (stateEvent) {

            /**
             * Handle GetArticlesEvent.
             */
            is ArticleListStateEvent.GetArticlesEvent -> {
                return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                    try {
                        repository3.getArticles(stateEvent.query).collect { dataState ->
                            emit(dataState)
                        }
                    } catch (e: Exception) {
                        emit(DataState.error<ArticleListViewState>("Error. Check network status."))
                    }
                }
            }

            /**
             * Handle None event.
             */
            is ArticleListStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    private fun getCurrentViewStateOrNew(): ArticleListViewState {
        return viewState.value?.let {
            it
        } ?: ArticleListViewState()
    }

    fun updateViewState(articleListViewState: ArticleListViewState) {
        val update = getCurrentViewStateOrNew()
        articleListViewState.articles?.let { articles ->
            update.articles = articles
        }
        articleListViewState.query?.let { query ->
            update.query = query
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
