package com.example.news.view

import com.example.news.model.Article

interface OnArticleClickListener {
    fun onArticleClick(article: Article): Unit
}
