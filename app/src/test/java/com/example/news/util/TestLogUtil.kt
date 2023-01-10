package com.example.news.util

fun logObservedArticles(tag: String, articles: List<com.example.news.model.Article>) {
    log(tag, "observed count: ${articles.size}")
    for (article in articles) {
        log(tag, "observed title: ${article.title}")
    }
}
