package com.example.news.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.databinding.ArticleRowBinding

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

/**
 * Adapter binding the articles data to the RecyclerView UI component.
 */
class ArticlesAdapter(
    private val articles: List<com.example.news.model.Article>,
    private val listener: OnArticleClickListener
) : RecyclerView.Adapter<ArticlesAdapter.ArticleHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
        val viewBinding = ArticleRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ArticleHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
        holder.itemView.setOnClickListener { listener.onArticleClick(article) }
    }

    override fun getItemCount() = articles.size

    // SRP (Single Responsibility Principle): A class should have only one reason to change.
    // Here we only do one thing: bind data to views.
    class ArticleHolder(
        private val viewBinding: ArticleRowBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {
        private var article: com.example.news.model.Article? = null

        fun bind(article: com.example.news.model.Article) {
            this.article = article
            if (article.imageUrl.isNotEmpty()) {
                viewBinding.articleRowImage.visibility = View.VISIBLE
                Glide.with(viewBinding.articleRowImage)
                    .load(article.imageUrl)
                    .into(viewBinding.articleRowImage)
            } else {
                viewBinding.articleRowImage.visibility = View.GONE
                viewBinding.articleRowImage.setImageDrawable(null)
            }
            viewBinding.articleRowSource.text = article.sourceName
            viewBinding.articleRowDate.text = article.publishedDate
            viewBinding.articleRowTitle.text = article.title
            if (article.content.isNullOrEmpty()) {
                viewBinding.articleRowContent.visibility = View.GONE
            } else {
                viewBinding.articleRowContent.visibility = View.VISIBLE
                viewBinding.articleRowContent.text = article.content
            }
        }
    }
}
