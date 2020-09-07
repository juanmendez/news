package com.example.news

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.news.di.DependencyContainer
import com.example.news.factory.ArticlesDataFactory
import com.example.news.mock.FORCE_GET_REPO_ARTICLES_EXCEPTION
import com.example.news.mock.REPO_ARTICLES_EXCEPTION_MESSAGE
import com.example.news.model.Article
import com.example.news.model.Repository
import com.example.news.util.TAG
import com.example.news.util.TestCoroutineRule
import com.example.news.util.log
import com.example.news.viewmodel.ArticleListActivityViewModel
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

    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val articlesDataFactory: ArticlesDataFactory
    private val repository: Repository

    // mocking the observers for the LiveData
    @Mock
    private lateinit var articlesObserver: Observer<List<Article>>

    @Mock
    private lateinit var errorMessageObserver: Observer<String?>

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
    fun observeArticles_success() {
        testCoroutineRule.runBlockingTest {

            // expected
            val expectedCacheArticles: List<Article> =
                articlesDataFactory.produceCacheListOfArticles()
            val expectedUpdatedCacheArticles: List<Article> =
                articlesDataFactory.produceUpdatedCacheListOfArticles()

            // no LifeCycle here, hence observe forever
            viewModel.articles.observeForever(articlesObserver)

            log(this@TestArticleListActivityViewModel.TAG, "call ViewModel's setQuery")
            // the query value doesn't matter since we use a fake Repository
            viewModel.setQuery("toto")

            // verify the observer receives expected cache data
            verify(articlesObserver).onChanged(expectedCacheArticles)
            viewModel.articles.value?.let { logObservedArticles(it) }

            // wait for the cache to be updated with data from network:
            // FakeRepositoryImpl fakes the network update delay to 1000 ms,
            // so we wait 1500 ms to make sure we got the data
            Thread.sleep(1500)

            // verify the observer receives expected network-updated cache data
            verify(articlesObserver).onChanged(expectedUpdatedCacheArticles)
            viewModel.articles.value?.let { logObservedArticles(it) }
        }
    }

//    @Test
//    fun observeArticles_failed() {
//        testCoroutineRule.runBlockingTest {
//            viewModel.errorMessage.observeForever(errorMessageObserver)
//
//            // force a failure
//            viewModel.setQuery(FORCE_GET_REPO_ARTICLES_EXCEPTION)
//
//            // verify the observed error message matches the one sent by the fake Repository
//            verify(errorMessageObserver).onChanged(REPO_ARTICLES_EXCEPTION_MESSAGE)
//        }
//    }

    private fun logObservedArticles(articles: List<Article>) {
        log(this@TestArticleListActivityViewModel.TAG,
            "observed count: ${articles.size}")
        for (article in articles) {
            log(this@TestArticleListActivityViewModel.TAG,
                "observed title: ${article.title}")
        }
    }
}
