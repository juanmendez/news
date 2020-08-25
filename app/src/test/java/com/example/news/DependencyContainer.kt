package com.example.news

import com.example.news.model.cache.ArticlesDaoService
import com.example.news.util.isUnitTest

class DependencyContainer {

    lateinit var articleFactory: ArticleFactory
    lateinit var articlesDataFactory: ArticlesDataFactory
    lateinit var articlesDaoService: ArticlesDaoService

    init {
        // for Logger.kt so that we log using println
        // (we cannot use Log.d, that's an Android API)
        isUnitTest = true
    }

    // instantiate all the fake dependencies
    fun build() {

        articleFactory = ArticleFactory()

        // create the factory producing the fake data
        this.javaClass.classLoader?.let { classLoader ->
            articlesDataFactory = ArticlesDataFactory(classLoader)
        }

        // create the fake database service implementation
        articlesDaoService = FakeArticlesDaoServiceImpl(
            // use factory to get a HashMap of articles (fake database)
            articlesData = articlesDataFactory.produceHashMapOfArticles(
                articlesDataFactory.produceListOfArticles()
            )
        )
    }
}