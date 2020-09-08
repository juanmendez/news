package com.example.news

import com.example.news.di.DependencyContainer
import com.example.news.factory.ArticlesDataFactory
import com.example.news.model.Article
import com.example.news.model.network.impl.ArticlesApiServiceImpl
import com.example.news.model.network.impl.ArticlesApi
import com.example.news.util.TAG
import com.example.news.util.assertThrows
import com.example.news.util.log
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

@InternalCoroutinesApi
class TestArticlesApiService {

    // this is the system in test
    private lateinit var articlesApiService: ArticlesApiServiceImpl

    // mocks
    private lateinit var mockWebServer: MockWebServer
    private lateinit var retrofit: Retrofit
    private lateinit var articlesApi: ArticlesApi

    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val articlesDataFactory: ArticlesDataFactory

    init {
        // init fake dependencies
        dependencyContainer.build()

        // fake data sources
        articlesDataFactory = dependencyContainer.articlesDataFactory
    }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        articlesApi = retrofit
            .create(ArticlesApi::class.java)

        // init system in test
        articlesApiService = ArticlesApiServiceImpl(retrofit, articlesApi)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getArticles_success() = runBlocking {

        // expected
        val expectedNetworkArticles: List<Article> =
            articlesDataFactory.produceNetworkListOfArticles()

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockResponseBody())
        mockWebServer.enqueue(response)

        log(this@TestArticlesApiService.TAG, "call getArticles")
        val articles: List<Article> = articlesApiService.getArticles("technology")

        log(this@TestArticlesApiService.TAG, "returned articles count: ${articles.size}")
        assert(articles.size == 1)
        log(this@TestArticlesApiService.TAG, "article title: ${articles[0].title}")
        val received = articles[0]
        val expected = expectedNetworkArticles[0]
        assert(received.author == expected.author)
        assert(received.content == expected.content)
        assert(received.description == expected.description)
        assert(received.imageUrl == expected.imageUrl)
        assert(received.query == expected.query)
        assert(received.sourceId == expected.sourceId)
        assert(received.sourceName == expected.sourceName)
        assert(received.title == expected.title)
        assert(received.url == expected.url)
    }

    @Test
    fun getArticles_failure() = runBlocking {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
        mockWebServer.enqueue(response)

        assertThrows<Exception> {
            log(this@TestArticlesApiService.TAG, "call getArticles and fail")
            articlesApiService.getArticles("technology")
        }
    }

    // the mock API response body contains a single article matching
    // the network.json object which will be the expected response
    private fun mockResponseBody(): String {
        return "{\"status\":\"ok\",\"totalResults\":1,\"articles\":[{\"source\":{\"id\":\"\",\"name\":\"IGN\"},\"author\":\"John Johnson\",\"title\":\"Nintendo Switch Pro Launch Date\",\"description\":\"description\",\"url\":\"url\",\"urlToImage\":\"imageUrl\",\"publishedAt\":\"2020-08-09T17:49:08Z\",\"content\":\"content\"}]}"
    }
}
