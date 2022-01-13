package com.example.news.view.composable

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.news.R
import com.example.news.model.Article
import com.example.news.view.WebViewActivity

@Composable
fun ArticleList(
    articles: SnapshotStateList<Article>?
) {
    val context = LocalContext.current
    articles?.let { articles ->
        LazyColumn(
            modifier = Modifier.background(
                color = Color(context.resources.getColor(R.color.card_background, null))
            )
        ) {
            itemsIndexed(
                items = articles
            ) { index, article ->
                ArticleCard(
                    article = article,
                    onClick = {
                        val intent = Intent(
                            context,
                            WebViewActivity::class.java
                        )
                        intent.putExtra(WebViewActivity.URL_EXTRA, article.url)
                        context.startActivity(intent)
                    })
            }
        }
    }
}
