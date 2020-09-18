package com.example.news.viewmodel

import androidx.lifecycle.*
import com.example.news.model.Article
import com.example.news.model.Repository
import com.example.news.state.ArticleListStateEvent
import com.example.news.state.ArticleListViewState
import com.example.news.util.AbsentLiveData

class ArticleListActivityViewModel3(
    private val repository: Repository
) : ViewModel(), LifecycleObserver {

    private val _stateEvent: MutableLiveData<ArticleListStateEvent> = MutableLiveData()
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

                // TODO for testing purposes until we plug in the Repo
                return object : LiveData<ArticleListViewState>() {
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
                        value = ArticleListViewState(articles = articles)
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

    fun setStateEvent(event: ArticleListStateEvent) {
        _stateEvent.value = event
    }
}
