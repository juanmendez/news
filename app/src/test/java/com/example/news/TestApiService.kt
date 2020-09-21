package com.example.news

import com.example.news.di.DependencyContainer
import com.example.news.factory.ArticlesDataFactory
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

    // mocks
    private lateinit var mockWebServer: MockWebServer
    private lateinit var retrofit: Retrofit
    private lateinit var api: Api

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
            articlesDataFactory.produceNetworkListOfArticles()

        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockResponseBody())
        mockWebServer.enqueue(response)

        log(this@TestApiService.TAG, "call getArticles")
        val articles: List<Article> = articlesApiService.getArticles("technology")

        log(this@TestApiService.TAG, "returned articles count: ${articles.size}")
        assert(articles.size == 1)
        log(this@TestApiService.TAG, "article title: ${articles[0].title}")
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
