package com.example.news.fake

import com.example.news.model.Article
import com.example.news.model.network.ApiService

const val FORCE_GET_NETWORK_ARTICLES_EXCEPTION = "FORCE_GET_NETWORK_ARTICLES_EXCEPTION"

/**
 * Fake api service implementation used in Unit Tests
 * A mock needs to be told what to reply with when a certain method is invoked during test, while
 * a fake is an actual implementation of the functionality (usually an interface) and allows for
 * specific behavior.
 * Here this fake implementation of the ApiService allows to test the case where an exception is
 * thrown when calling getArticles and we do that by calling getArticles with the query parameter
 * equal to FORCE_GET_NETWORK_ARTICLES_EXCEPTION.
 * We also constructor inject a hash map with the fake network (remote cache) articles.
 */
class FakeApiServiceImpl
constructor(
    private val fakeNetworkArticlesData: HashMap<String, Article>,
) : ApiService {
    override suspend fun getArticles(query: String): List<Article> {

        // we need to test the case where an exception is thrown,
        // to that effect we will fake it by sending a query with
        // the value FORCE_GET_NETWORK_ARTICLES_EXCEPTION
        if (query == FORCE_GET_NETWORK_ARTICLES_EXCEPTION) {
            throw Exception("Something went getting the network articles.")
        }

        val results: ArrayList<Article> = ArrayList()

        // search in the fake HashMap data
        // to match what an API call would do
        for (article in fakeNetworkArticlesData.values) {
            if (article.query.contains(query)) {
                results.add(article)
            }
        }
        return results
    }
}
