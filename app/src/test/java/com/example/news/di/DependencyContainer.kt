package com.example.news.di

import com.example.news.mock.FakeArticlesApiServiceImpl
import com.example.news.mock.FakeArticlesCacheServiceImpl
import com.example.news.factory.ArticleFactory
import com.example.news.factory.ArticlesDataFactory
import com.example.news.model.cache.ArticlesCacheService
import com.example.news.model.network.ArticlesApiService
import com.example.news.util.isUnitTest

class DependencyContainer {

    lateinit var articleFactory: ArticleFactory
    lateinit var articlesDataFactory: ArticlesDataFactory
    lateinit var articlesCacheService: ArticlesCacheService
    lateinit var articlesApiService: ArticlesApiService

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
        articlesCacheService = FakeArticlesCacheServiceImpl(
            // use factory to get a HashMap of articles (fake cache)
            articlesData = articlesDataFactory.produceHashMapOfArticles(
                articlesDataFactory.produceCacheListOfArticles()
            )
        )

        // create the fake API service implementation
        articlesApiService = FakeArticlesApiServiceImpl(
            // use factory to get a HashMap of articles (fake API)
            articlesData = articlesDataFactory.produceHashMapOfArticles(
                articlesDataFactory.produceNetworkListOfArticles()
            )
        )
    }
}
