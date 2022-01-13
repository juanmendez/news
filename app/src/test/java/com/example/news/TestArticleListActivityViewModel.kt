package com.example.news

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.news.di.FakeDependencyContainer
import com.example.news.factory.FakeArticlesDataFactory
import com.example.news.fake.FORCE_GET_REPO_ARTICLES_EXCEPTION
import com.example.news.fake.REPO_ARTICLES_EXCEPTION_MESSAGE
import com.example.news.model.Article
import com.example.news.model.Repository
import com.example.news.util.*
import com.example.news.viewmodel.ArticleListActivityViewModel
import junit.framework.Assert
import kotlinx.coroutines.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TestArticleListActivityViewModel {

    // needed to test LiveData, swaps the background executor used by the Architecture
    // Components with a different one which executes each task synchronously
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // this is the system in test
    private val viewModel: ArticleListActivityViewModel

    // fakes
    private val fakeDependencyContainer: FakeDependencyContainer = FakeDependencyContainer()
    private val fakeArticlesDataFactory: FakeArticlesDataFactory
    private val fakeRepository: Repository

    // mocking the LiveData observers
    @Mock
    private lateinit var articlesObserver: Observer<List<Article>>

    @Mock
    private lateinit var errorMessageObserver: Observer<String?>

    init {
        // init fake dependencies
        fakeDependencyContainer.build()

        // fake data sources
        fakeArticlesDataFactory = fakeDependencyContainer.fakeArticlesDataFactory
        fakeRepository = fakeDependencyContainer.fakeRepository

        // init system in test
        viewModel = ArticleListActivityViewModel(fakeRepository)
    }

    @Test
    fun setQuery_success() {
        testCoroutineRule.runBlockingTest {

            // expected
            val expectedCacheArticles: List<Article> =
                fakeArticlesDataFactory.produceFakeCacheListOfArticles()
            val expectedUpdatedCacheArticles: List<Article> =
                fakeArticlesDataFactory.produceFakeUpdatedCacheListOfArticles()

            // no LifeCycle here, hence observe forever
            viewModel.articles.observeForever(articlesObserver)

            log(this@TestArticleListActivityViewModel.TAG, "call ViewModel's setQuery")
            // the query value doesn't matter since we use a fake Repository
            viewModel.setQuery("toto")

            // wait to get the cache results
            Thread.sleep(500)

            // verify the observer receives expected cache data
            verify(articlesObserver).onChanged(expectedCacheArticles)
            viewModel.articles.value?.let {
                logObservedArticles(this@TestArticleListActivityViewModel.TAG, it)
            }

            // wait for the cache to be updated with data from network: FakeRepositoryImpl fakes
            // the network update delay to 1000 ms, so we wait 1500 ms to make sure we got the data
            Thread.sleep(1500)

            // verify the observer receives expected network-updated cache data
            verify(articlesObserver).onChanged(expectedUpdatedCacheArticles)
            viewModel.articles.value?.let {
                logObservedArticles(this@TestArticleListActivityViewModel.TAG, it)
            }
        }
    }

    // this is using getOrAwaitValue
    @Test
    fun setQuery_success2() {
        testCoroutineRule.runBlockingTest {

            // expected
            val expectedCacheArticles: List<Article> =
                fakeArticlesDataFactory.produceFakeCacheListOfArticles()
            val expectedUpdatedCacheArticles: List<Article> =
                fakeArticlesDataFactory.produceFakeUpdatedCacheListOfArticles()

            log(this@TestArticleListActivityViewModel.TAG, "call ViewModel's setQuery")
            // the query value doesn't matter since we use a fake Repository
            viewModel.setQuery("toto")

            // verify the observed articles match the expected cache data
            Assert.assertEquals(viewModel.articles.getOrAwaitValue(), expectedCacheArticles)
            viewModel.articles.value?.let {
                logObservedArticles(this@TestArticleListActivityViewModel.TAG, it)
            }

            // wait for the cache to be updated with data from network: FakeRepositoryImpl fakes
            // the network update delay to 1000 ms, so we wait 1500 ms to make sure we got the data
            Thread.sleep(1500)

            // verify the observed articles match the expected network-updated cache data
            Assert.assertEquals(viewModel.articles.getOrAwaitValue(), expectedUpdatedCacheArticles)
            viewModel.articles.value?.let {
                logObservedArticles(this@TestArticleListActivityViewModel.TAG, it)
            }
        }
    }

    @Test
    fun setQuery_failure() {
        testCoroutineRule.runBlockingTest {

            val expectedErrorMessage = REPO_ARTICLES_EXCEPTION_MESSAGE

            // observe the error message
            viewModel.errorMessage.observeForever(errorMessageObserver)

            // need to also observe the articles as they trigger the error
            viewModel.articles.observeForever(articlesObserver)

            log(this@TestArticleListActivityViewModel.TAG,
                "call ViewModel's setQuery and force a Repository failure")
            // force fake Repository to fail
            viewModel.setQuery(FORCE_GET_REPO_ARTICLES_EXCEPTION)

            // wait to get the error
            Thread.sleep(500)

            // verify the observed error message matches the one sent by the fake Repository
            verify(errorMessageObserver).onChanged(expectedErrorMessage)
            log(this@TestArticleListActivityViewModel.TAG,
                "observed error: ${viewModel.errorMessage.value}")
        }
    }
}
