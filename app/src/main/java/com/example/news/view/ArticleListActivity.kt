package com.example.news.view

import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.MyApplication
import com.example.news.R
import com.example.news.model.Article
import com.example.news.util.InjectorUtil
import com.example.news.viewmodel.ArticleListActivityViewModel
import com.example.news.viewmodel.ArticleListActivityViewModelFactory
import kotlinx.android.synthetic.main.activity_article_list.*

class ArticleListActivity : BaseActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager

    private val listener = object : OnArticleClickListener {
        override fun onArticleClick(article: Article) {
            // TODO show article.content
        }
    }

    private val viewModel: ArticleListActivityViewModel by viewModels {
        val repository = InjectorUtil.provideRepository(application as MyApplication)
        ArticleListActivityViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_list)
        initUI()
        initObservers()

        // for the initial data load, triggered within the view model
        showProgressBar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_article_list_menu, menu)
        val searchMenu = menu.findItem(R.id.activity_article_list_menu_search)
        val searchView = searchMenu.actionView as SearchView
        searchView.apply {
            setIconifiedByDefault(true)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(input: String?): Boolean {
                val query = input?.trim()
                query?.let {
                    showProgressBar()
                    viewModel.setQuery(query)
                    this@ArticleListActivity.hideKeyboard()
                    searchMenu.collapseActionView()
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                // TODO show suggestions based on previous queries
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun initUI() {
        linearLayoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE ->
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            else -> LinearLayoutManager(this)
        }
        articles_recyclerview.layoutManager = linearLayoutManager
    }

    private fun initObservers() {
        lifecycle.addObserver(viewModel)
        viewModel.articles.observe(this, { articles ->
            hideProgressBar()
            articles_recyclerview.adapter = ArticlesAdapter(articles, listener)
        })
        viewModel.errorMessage.observe(this, { msg ->
            hideProgressBar()
            msg?.let {
                showAlertDialog(msg)
            }
        })
        viewModel.query.observe(this, { query ->
            supportActionBar?.title = query
        })
    }

    private fun showAlertDialog(message: String?) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder
            .setMessage(message ?: "Something went wrong...")
            .setCancelable(false)
            .setPositiveButton("Ok", DialogInterface.OnClickListener { _, _ ->
                run {
                    viewModel.showError(null)
                }
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Error")
        alert.show()
    }
}
