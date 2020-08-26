package com.example.news.view

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.MyApplication
import com.example.news.R
import com.example.news.model.Article
import com.example.news.util.InjectorUtil
import com.example.news.viewmodel.MainActivityViewModel
import com.example.news.viewmodel.MainActivityViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val listener = object : OnArticleClickListener {
        override fun onArticleClick(article: Article) {
            // TODO show article.content
        }
    }

    private val viewModel: MainActivityViewModel by viewModels {
        val repository = InjectorUtil.provideRepository(application as MyApplication)
        MainActivityViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        initObservers()

        // get data and update UI
        viewModel.setQuery("technology")
    }

    private fun initUI() {
        linearLayoutManager = LinearLayoutManager(this)
        articles_recyclerview.layoutManager = linearLayoutManager
    }

    private fun initObservers() {
        lifecycle.addObserver(viewModel)
        viewModel.articles.observe(this, Observer { articles ->
            articles_recyclerview.adapter = ArticlesAdapter(articles, listener)
        })
        viewModel.showError.observe(this, Observer { show ->
            show?.let {
                when (show) {
                    true -> showAlertDialog()
                }
            }
        })
    }

    private fun showAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder
            .setMessage("Something went wrong...")
            .setCancelable(false)
            .setPositiveButton("Ok", DialogInterface.OnClickListener { _, _ ->
                run {
                    viewModel.showError(false)
                }
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Error")
        alert.show()
    }
}
