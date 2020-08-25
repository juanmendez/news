package com.example.news.model

import androidx.lifecycle.LiveData
import com.example.news.model.cache.ArticlesDaoService
import com.example.news.model.network.ArticlesApiService
import kotlinx.coroutines.*

class RepositoryImpl(
    private val articlesApiService: ArticlesApiService,
    private val articlesDaoService: ArticlesDaoService
) : Repository {
    private var getArticlesJob: CompletableJob? = null

    override fun getArticles(query: String) : LiveData<List<Article>> {
        getArticlesJob = Job()
        return object : LiveData<List<Article>>() {
            override fun onActive() {
                super.onActive()
                getArticlesJob?.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        val available = articlesDaoService.getArticlesCount(query)
                        if (available == 0) {
                            articlesApiService.getArticles(query)?.let {
                                articlesDaoService.insertArticles(it)
                                val articles = articlesDaoService.getArticles(query)
                                withContext(Dispatchers.Main) {
                                    value = articles
                                    job.complete()
                                }
                            }
                        } else {
                            val articles = articlesDaoService.getArticles(query)
                            withContext(Dispatchers.Main) {
                                value = articles
                                job.complete()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun cancelJobs() {
        getArticlesJob?.cancel()
    }
}
