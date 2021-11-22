package com.example.news.di

import com.example.news.fake.FakeApiServiceImpl
import com.example.news.fake.FakeCacheServiceImpl
import com.example.news.factory.FakeArticlesDataFactory
import com.example.news.fake.FakeRepositoryImpl
import com.example.news.model.Repository
import com.example.news.model.cache.CacheService
import com.example.news.model.network.ApiService
import com.example.news.util.isUnitTest

/**
 * Contains all the fake dependencies needed for Unit Tests
 */
class FakeDependencyContainer {

    /**
     * Fake articles data factory
     */
    lateinit var fakeArticlesDataFactory: FakeArticlesDataFactory

    /**
     * Fake cache service used by Unit Tests
     */
    lateinit var fakeCacheService: CacheService

    /**
     * Fake api service used by Unit Tests
     */
    lateinit var fakeApiService: ApiService

    /**
     * Fake repository used by Unit Tests
     */
    lateinit var fakeRepository: Repository

    init {
        // for Logger.kt so that we log using println
        // (we cannot use Log.d, that's an Android API)
        isUnitTest = true
    }

    /**
     * Instantiates all the fake dependencies needed for Unit Tests
     */
    fun build() {

        // create the factory producing the fake data
        this.javaClass.classLoader?.let { classLoader ->
            fakeArticlesDataFactory = FakeArticlesDataFactory(classLoader)
        }

        // create the fake cache service implementation
        fakeCacheService = FakeCacheServiceImpl(
            // use factory to get a HashMap of articles (fake cache)
            fakeCacheArticlesData = fakeArticlesDataFactory.produceFakeHashMapOfArticles(
                fakeArticlesDataFactory.produceFakeCacheListOfArticles()
            )
        )

        // create the fake API service implementation
        fakeApiService = FakeApiServiceImpl(
            // use factory to get a HashMap of articles (fake API)
            fakeNetworkArticlesData = fakeArticlesDataFactory.produceFakeHashMapOfArticles(
                fakeArticlesDataFactory.produceFakeNetworkListOfArticles()
            )
        )

        // create the fake Repo implementation
        fakeRepository = FakeRepositoryImpl(
            // use factory to get a HashMap of articles (fake cache)
            fakeCacheArticlesData = fakeArticlesDataFactory.produceFakeHashMapOfArticles(
                fakeArticlesDataFactory.produceFakeCacheListOfArticles()
            ),
            // use factory to get a HashMap of articles (fake API)
            fakeNetworkArticlesData = fakeArticlesDataFactory.produceFakeHashMapOfArticles(
                fakeArticlesDataFactory.produceFakeNetworkListOfArticles()
            )
        )
    }
}
