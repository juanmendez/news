package com.example.news.view

import com.example.news.model.Article

/**
 * Functional interface, i.e. single method interface, also called Single Abstract Method (SAM)
 * interface, for handling user clicks on an article row in the UI.
 */
interface OnArticleClickListener {
    fun onArticleClick(article: Article)
}
