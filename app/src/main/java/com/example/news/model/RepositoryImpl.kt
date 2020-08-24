package com.example.news.model

import androidx.lifecycle.LiveData
import com.example.news.model.local.CacheMapper
import com.example.news.model.remote.ArticleRemote
import com.example.news.model.remote.NetworkMapper
import com.example.news.util.DateUtil
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class RepositoryImpl(private val database: ArticlesDatabase) : Repository {
    private var getArticlesJob: CompletableJob? = null

    private val articlesDao: ArticlesDao by lazy {
        database.articlesDao()
    }

    private suspend fun articlesRemote(query: String) : List<ArticleRemote>? {
        return ApiService.instance.getArticles(query).articles
    }

    override fun getArticles(query: String) : LiveData<List<Article>> {
        getArticlesJob = Job()
        return object : LiveData<List<Article>>() {
            override fun onActive() {
                super.onActive()
                getArticlesJob?.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        // "2020-08-24T14:38:37Z"
                        val cacheMapper =
                            CacheMapper(dateUtil = DateUtil(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")))
                        val available = articlesDao.getArticlesCount(query)
                        if (available == 0) {
                            articlesRemote(query)?.let {
                                articlesDao.insertArticles(
                                    cacheMapper.articleListToEntityList(NetworkMapper.remoteListToArticleList(query, it)))
                                val articles = articlesDao.getArticles(query)
                                withContext(Dispatchers.Main) {
                                    value = cacheMapper.entityListToArticleList(articles)
                                    job.complete()
                                }
                            }
                        } else {
                            val articles = articlesDao.getArticles(query)
                            withContext(Dispatchers.Main) {
                                value = cacheMapper.entityListToArticleList(articles)
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
