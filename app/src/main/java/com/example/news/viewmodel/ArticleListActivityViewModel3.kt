package com.example.news.viewmodel

import androidx.lifecycle.*
import com.example.news.model.Article
import com.example.news.model.Repository
import com.example.news.state.ArticleListStateEvent
import com.example.news.state.ArticleListViewState
import com.example.news.util.AbsentLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

class ArticleListActivityViewModel3(
    private val repository: Repository
) : ViewModel(), LifecycleObserver {

    private val _stateEvent: MutableLiveData<ArticleListStateEvent> = MutableLiveData(
        // initial value that triggers fetching the top headlines
        ArticleListStateEvent.GetArticlesEvent("Top Headlines")
    )
    private val _viewState: MutableLiveData<ArticleListViewState> = MutableLiveData()

    // MVI: single LiveData data exposed to View
    val viewState: LiveData<ArticleListViewState> = _viewState

    val dataState: LiveData<ArticleListViewState> =
        Transformations.switchMap(_stateEvent) { stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    private fun handleStateEvent(stateEvent: ArticleListStateEvent): LiveData<ArticleListViewState> {
        return when (stateEvent) {
            is ArticleListStateEvent.GetArticlesEvent -> {
                return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                    try {
                        repository.getArticles(stateEvent.query).collect {
                            emit(ArticleListViewState(articles = it, query = stateEvent.query))
                        }
                    } catch (e: Exception) {
                        // TODO catch exceptions propagated from data sources through
                        //  the repository and inform the user by updating the UI
                    }
                }
            }

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

    fun setArticlesData(articles: List<Article>) {
        val update = getCurrentViewStateOrNew()
        update.articles = articles
        _viewState.value = update
    }

    fun setQueryData(query: String) {
        val update = getCurrentViewStateOrNew()
        update.query = query
        _viewState.value = update
    }

    fun setStateEvent(event: ArticleListStateEvent) {
        _stateEvent.value = event
    }
}
