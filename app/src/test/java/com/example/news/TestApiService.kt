package com.example.news

import com.example.news.di.FakeDependencyContainer
import com.example.news.factory.FakeArticlesDataFactory
import com.example.news.model.Article
import com.example.news.model.network.impl.ApiServiceImpl
import com.example.news.model.network.impl.Api
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
class TestApiService {

    // this is the system in test
    private lateinit var articlesApiService: ApiServiceImpl

    // mock
    private lateinit var mockWebServer: MockWebServer

    // dependencies
    private lateinit var retrofit: Retrofit
    private lateinit var api: Api

    // fakes
    private val fakeDependencyContainer: FakeDependencyContainer = FakeDependencyContainer()
    private val fakeArticlesDataFactory: FakeArticlesDataFactory

    init {
        // init fake dependencies
        fakeDependencyContainer.build()

        // fake data source
        fakeArticlesDataFactory = fakeDependencyContainer.fakeArticlesDataFactory
    }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // init dependencies
        retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit
            .create(Api::class.java)

        // init system in test
        articlesApiService = ApiServiceImpl(retrofit, api)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getArticles_success() = runBlocking {

        // expected
        val expectedNetworkArticles: List<Article> =
            fakeArticlesDataFactory.produceFakeNetworkListOfArticles()
        val expectedNetworkArticlesSize = 1

        // enqueue mock response into mock web server
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockResponseBody())
        mockWebServer.enqueue(response)

        // make the api call
        log(this@TestApiService.TAG, "call getArticles")
        val actualNetworkArticles: List<Article> = articlesApiService.getArticles("technology")

        // assert number of received articles matches the expected value
        log(this@TestApiService.TAG, "returned articles count: ${actualNetworkArticles.size}")
        assert(actualNetworkArticles.size == expectedNetworkArticlesSize)

        // assert the actual received article matches the expected article
        log(this@TestApiService.TAG, "article title: ${actualNetworkArticles[0].title}")
        val actual = actualNetworkArticles[0]
        val expected = expectedNetworkArticles[0]
        assert(actual.author == expected.author)
        assert(actual.content == expected.content)
        assert(actual.description == expected.description)
        assert(actual.imageUrl == expected.imageUrl)
        assert(actual.query == expected.query)
        assert(actual.sourceId == expected.sourceId)
        assert(actual.sourceName == expected.sourceName)
        assert(actual.title == expected.title)
        assert(actual.url == expected.url)
    }

    @Test
    fun getArticles_failure() = runBlocking {

        // enqueue mock response into the mock web server
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
        mockWebServer.enqueue(response)

        // assert the api call throws an exception
        assertThrows<Exception> {

            // make the api call
            log(this@TestApiService.TAG, "call getArticles and fail")
            articlesApiService.getArticles("technology")
        }
    }

    // the mock API response body contains a single article matching
    // the network.json object which will be the expected response
    private fun mockResponseBody(): String {
        return "{\"status\":\"ok\",\"totalResults\":1,\"articles\":[{\"source\":{\"id\":\"\",\"name\":\"IGN\"},\"author\":\"John Johnson\",\"title\":\"Nintendo Switch Pro Launch Date\",\"description\":\"description\",\"url\":\"url\",\"urlToImage\":\"imageUrl\",\"publishedAt\":\"2020-08-09T17:49:08Z\",\"content\":\"content\"}]}"
    }
}
