package com.example.news.factory

import com.example.news.model.Article
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Factory for fake cache and network (remote cache) articles.
 */
class FakeArticlesDataFactory(
    private val testClassLoader: ClassLoader
) {
    fun produceFakeCacheListOfArticles(): List<com.example.news.model.Article> {
        return Gson()
            .fromJson(
                getArticlesFromFile("cache.json"),
                object : TypeToken<List<com.example.news.model.Article>>() {}.type
            )
    }

    fun produceFakeNetworkListOfArticles(): List<com.example.news.model.Article> {
        return Gson()
            .fromJson(
                getArticlesFromFile("network.json"),
                object : TypeToken<List<com.example.news.model.Article>>() {}.type
            )
    }

    fun produceFakeUpdatedCacheListOfArticles(): List<com.example.news.model.Article> {
        return Gson()
            .fromJson(
                getArticlesFromFile("updated_cache.json"),
                object : TypeToken<List<com.example.news.model.Article>>() {}.type
            )
    }

    fun produceFakeHashMapOfArticles(noteList: List<com.example.news.model.Article>): HashMap<String, com.example.news.model.Article> {
        val map = HashMap<String, com.example.news.model.Article>()
        for (note in noteList) {
            map[note.id] = note
        }
        return map
    }

    fun produceEmptyListOfArticles(): List<com.example.news.model.Article> {
        return ArrayList()
    }

    // Outputs raw file data in a String. The class loader is used
    // as a context to get to the resources to get to the file
    private fun getArticlesFromFile(fileName: String): String {
        return testClassLoader.getResource(fileName).readText()
    }
}
