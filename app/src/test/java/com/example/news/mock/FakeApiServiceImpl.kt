package com.example.news.mock

import com.example.news.model.Article
import com.example.news.model.network.ApiService

const val FORCE_GET_NETWORK_ARTICLES_EXCEPTION = "FORCE_GET_NETWORK_ARTICLES_EXCEPTION"

class FakeApiServiceImpl
constructor(
    private val articlesData: HashMap<String, Article>,
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
        for (article in articlesData.values) {
            if (article.query.contains(query)) {
                results.add(article)
            }
        }
        return results
    }
}
