package com.example.news

import com.example.news.model.Article
import com.example.news.model.network.impl.ArticlesApiServiceImpl
import com.example.news.model.network.impl.ArticlesApi
import com.example.news.util.TAG
import com.example.news.util.assertThrows
import com.example.news.util.isUnitTest
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

    init {
        // for Logger.kt so that we log using println (we cannot use Log.d, that's an Android API)
        isUnitTest = true
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
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(mockResponseBody())
        mockWebServer.enqueue(response)

        log(this@TestArticlesApiService.TAG, "call getArticles")
        val articles: List<Article> = articlesApiService.getArticles("ps4")
        assert(articles.size == 1)
        log(this@TestArticlesApiService.TAG, "returned articles count: ${articles.size}")
        log(this@TestArticlesApiService.TAG, "article title: ${articles[0].title}")
    }

    @Test
    fun getArticles_failure() = runBlocking {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
        mockWebServer.enqueue(response)

        assertThrows<Exception> {
            log(this@TestArticlesApiService.TAG, "call getArticles and fail")
            articlesApiService.getArticles("ps4")
        }
    }

    private fun mockResponseBody(): String {
        return "{\"status\":\"ok\",\"totalResults\":2,\"articles\":[{\"source\":{\"id\":\"polygon\",\"name\":\"Polygon\"},\"author\":\"Owen S. Good\",\"title\":\"Classic Doom and Doom 2 get native widescreen, mod tools, new modes and more\",\"description\":\"Doom (1993) and Doom 2 (1994) got another large title update on Sept. 3, bringing several quality-of-life improvements to the PS4, Nintendo Switch, Xbox One, PC and mobile port.\",\"url\":\"https://www.polygon.com/2020/9/4/21423176/doom-doom-2-update-pc-switch-ps4-xbox-one-widescreen-dehacked-deathmatch\",\"urlToImage\":\"https://cdn.vox-cdn.com/thumbor/NjSwwlVpU6j9uXq_75lnmQ24H2s=/0x12:1321x704/fit-in/1200x630/cdn.vox-cdn.com/uploads/chorus_asset/file/12766453/Screen_Shot_2018_08_31_at_6.28.38_PM.png\",\"publishedAt\":\"2020-09-04T17:49:08Z\",\"content\":\"A robust update for the original Doomand Doom 2 delivers official 16:9 widescreen support, an optional crosshair overlay, and even motion-control assisted aiming for Nintendo Switch and platforms usi… [+1859 chars]\"}]}"
    }
}
