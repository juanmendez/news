package com.example.news.viewmodel

import androidx.lifecycle.*
import com.example.news.model.Article
import com.example.news.model.Repository
import com.example.news.state.MainStateEvent
import com.example.news.state.MainViewState
import com.example.news.util.AbsentLiveData

class MainViewModel(
    private val repository: Repository
) : ViewModel(), LifecycleObserver {

    private val _stateEvent: MutableLiveData<MainStateEvent> = MutableLiveData()
    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()

    // MVI: single LiveData data exposed to View
    val viewState: LiveData<MainViewState> = _viewState

    val dataState: LiveData<MainViewState> =
        Transformations.switchMap(_stateEvent) { stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    private fun handleStateEvent(stateEvent: MainStateEvent): LiveData<MainViewState> {
        return when (stateEvent) {
            is MainStateEvent.GetArticlesEvent -> {
                // TODO for testing purposes until we plug in the Repo
                return object : LiveData<MainViewState>() {
                    override fun onActive() {
                        super.onActive()
                        val articles: ArrayList<Article> = ArrayList()
                        articles.add(
                            Article(
                                id = "101",
                                query = "technology",
                                sourceId = "102",
                                sourceName = "IGN",
                                author = "John Doe",
                                title = "Xbox Series X Launch Date",
                                description = "The Xbox Series X will launch on November 10th.",
                                url = "",
                                imageUrl = "",
                                publishedDate = "September 12, 2020",
                                content = ""
                            )
                        )
                        value = MainViewState(articles = articles)
                    }
                }
            }
            is MainStateEvent.None -> {
                AbsentLiveData.create()
            }
        }
    }

    private fun getCurrentViewStateOrNew(): MainViewState {
        return viewState.value?.let {
            it
        } ?: MainViewState()
    }

    fun setArticlesListData(articles: List<Article>) {
        val update = getCurrentViewStateOrNew()
        update.articles = articles
        _viewState.value = update
    }

    fun setQueryData(query: String) {
        val update = getCurrentViewStateOrNew()
        update.query = query
        _viewState.value = update
    }

    fun setStateEvent(event: MainStateEvent) {
        _stateEvent.value = event
    }
}
