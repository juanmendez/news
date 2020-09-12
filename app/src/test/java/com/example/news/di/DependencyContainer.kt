package com.example.news.di

import com.example.news.mock.FakeApiServiceImpl
import com.example.news.mock.FakeCacheServiceImpl
import com.example.news.factory.ArticleFactory
import com.example.news.factory.ArticlesDataFactory
import com.example.news.mock.FakeRepositoryImpl
import com.example.news.model.Repository
import com.example.news.model.cache.CacheService
import com.example.news.model.network.ApiService
import com.example.news.util.isUnitTest

class DependencyContainer {

    lateinit var articleFactory: ArticleFactory
    lateinit var articlesDataFactory: ArticlesDataFactory
    lateinit var cacheService: CacheService
    lateinit var apiService: ApiService
    lateinit var repository: Repository

    init {
        // for Logger.kt so that we log using println
        // (we cannot use Log.d, that's an Android API)
        isUnitTest = true
    }

    // instantiate all the fake dependencies
    fun build() {

        // create fake article factory
        articleFactory = ArticleFactory()

        // create the factory producing the fake data
        this.javaClass.classLoader?.let { classLoader ->
            articlesDataFactory = ArticlesDataFactory(classLoader)
        }

        // create the fake cache service implementation
        cacheService = FakeCacheServiceImpl(
            // use factory to get a HashMap of articles (fake cache)
            articlesData = articlesDataFactory.produceHashMapOfArticles(
                articlesDataFactory.produceCacheListOfArticles()
            )
        )

        // create the fake API service implementation
        apiService = FakeApiServiceImpl(
            // use factory to get a HashMap of articles (fake API)
            articlesData = articlesDataFactory.produceHashMapOfArticles(
                articlesDataFactory.produceNetworkListOfArticles()
            )
        )

        // create the fake Repo implementation
        repository = FakeRepositoryImpl(
            // use factory to get a HashMap of articles (fake cache)
            cacheArticlesData = articlesDataFactory.produceHashMapOfArticles(
                articlesDataFactory.produceCacheListOfArticles()
            ),
            // use factory to get a HashMap of articles (fake API)
            networkArticlesData = articlesDataFactory.produceHashMapOfArticles(
                articlesDataFactory.produceNetworkListOfArticles()
            )
        )
    }
}
