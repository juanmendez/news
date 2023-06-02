package com.example.news.data.util

import android.util.Log
import com.example.news.data.Article
import com.example.news.data.BuildConfig.DEBUG

const val TAG = "News"

/**
 * Extension property providing a TAG for logging purposes for any object
 */
val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

/**
 * Defaults to false, set to true in Unit Tests which do not allow Android APIs, so we cannot use
 * Log.d, instead we will be using println.
 */
var isUnitTest = false

/**
 * Logs a message
 * @param className tag, often the class name
 * @param message string logged
 */
fun log(className: String?, message: String) {
    if (DEBUG && !isUnitTest) {
        Log.d(TAG, "$className: $message")
    } else if (DEBUG && isUnitTest) {
        println("$className: $message")
    }
}

/**
 * Logs all articles
 * @param msg message logged
 * @param articles list of [Article] logged
 */
fun logArticles(msg: String, articles: List<com.example.news.data.Article>) {
    log("toto", "$msg ${articles.size} articles:")
    for ((index, _) in articles.withIndex()) {
        log("toto", "${index + 1}. ${articles[index].title} | ${articles[index].publishedDate}")
    }
}

/**
 * Logs first 3 articles
 * @param msg message logged
 * @param articles list of [Article] logged
 */
fun log3Articles(msg: String, articles: List<com.example.news.data.Article>) {
    log("toto", "$msg ${articles.size} articles:")
    for ((index, _) in articles.withIndex()) {
        log("toto", "${index + 1}. ${articles[index].title} | ${articles[index].publishedDate}")
        if (index == 2) break
    }
}
