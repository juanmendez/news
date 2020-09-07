package com.example.news

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.news.di.DependencyContainer
import com.example.news.factory.ArticlesDataFactory
import com.example.news.model.Article
import com.example.news.model.Repository
import com.example.news.util.*
import com.example.news.viewmodel.ArticleListActivityViewModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TestArticleListActivityViewModel2 {

    // needed to test LiveData, swaps the background executor used by the Architecture
    // Components with a different one which executes each task synchronously
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // this is the system in test
    private val viewModel: ArticleListActivityViewModel

    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val articlesDataFactory: ArticlesDataFactory
    private val repository: Repository

    init {
        // init fake dependencies
        dependencyContainer.build()

        // fake data sources
        articlesDataFactory = dependencyContainer.articlesDataFactory
        repository = dependencyContainer.repository

        // init system in test
        viewModel = ArticleListActivityViewModel(repository)
    }

    @Test
    fun setQuery_success2() {
        testCoroutineRule.runBlockingTest {

            // expected
            val expectedCacheArticles: List<Article> =
                articlesDataFactory.produceCacheListOfArticles()
            val expectedUpdatedCacheArticles: List<Article> =
                articlesDataFactory.produceUpdatedCacheListOfArticles()

            log(this@TestArticleListActivityViewModel2.TAG, "call ViewModel's setQuery")
            // the query value doesn't matter since we use a fake Repository
            viewModel.setQuery("toto")

            // verify the observed articles match the expected cache data
            assertEquals(viewModel.articles.getOrAwaitValue(), expectedCacheArticles)
            viewModel.articles.value?.let {
                logObservedArticles(this@TestArticleListActivityViewModel2.TAG, it)
            }

            // wait for the cache to be updated with data from network: FakeRepositoryImpl fakes
            // the network update delay to 1000 ms, so we wait 1500 ms to make sure we got the data
            Thread.sleep(1500)

            // verify the observed articles match the expected network-updated cache data
            assertEquals(viewModel.articles.getOrAwaitValue(), expectedUpdatedCacheArticles)
            viewModel.articles.value?.let {
                logObservedArticles(this@TestArticleListActivityViewModel2.TAG, it)
            }
        }
    }
}
