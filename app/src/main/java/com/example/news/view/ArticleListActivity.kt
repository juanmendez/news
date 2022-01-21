package com.example.news.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.MyApplication
import com.example.news.R
import com.example.news.model.Article
import com.example.news.util.InjectorUtil
import com.example.news.util.TOP_HEADLINES
import com.example.news.view.WebViewActivity.Companion.URL_EXTRA
import com.example.news.viewmodel.ArticleListActivityViewModel
import com.example.news.viewmodel.ArticleListActivityViewModelFactory
import kotlinx.android.synthetic.main.activity_article_list.*

/**
 * Implements MVVM architecture
 */
class ArticleListActivity : BaseActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var searchMenu: MenuItem
    private lateinit var adapter: ArticlesAdapter
    private var articles: ArrayList<Article> = arrayListOf()

    private val listener = object : OnArticleClickListener {
        override fun onArticleClick(article: Article) {
            val intent = Intent(this@ArticleListActivity, WebViewActivity::class.java)
            intent.putExtra(URL_EXTRA, article.url)
            this@ArticleListActivity.startActivity(intent)
        }
    }

    // instantiates the ViewModel using a factory, constructor-injects the repository interface
    // implementation needed by the ViewModel instance, the repository needs the application context
    // for caching purposes
    private val viewModel: ArticleListActivityViewModel by viewModels {
        val repository = InjectorUtil.provideRepository(application as MyApplication)
        ArticleListActivityViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_list)
        initUI()
        initObservers()
        saveQueryToRecentSuggestions(TOP_HEADLINES)
    }

    private fun initUI() {
        linearLayoutManager = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE ->
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            else -> LinearLayoutManager(this)
        }
        articles_recyclerview.layoutManager = linearLayoutManager
        adapter = ArticlesAdapter(articles, listener)
        articles_recyclerview.adapter = adapter
    }

    private fun initObservers() {
        // AppCompatActivity implements LifeCycleOwner single method interface and exposes it via
        // getLifecycle. By adding the ViewModel as an observer we allow the ViewModel to handle
        // the Activity's lifecycle events via annotations
        lifecycle.addObserver(viewModel)

        // init data observers and update the UI
        viewModel.articles.observe(this, { data ->
            articles.clear()
            articles.addAll(data)
            adapter.notifyDataSetChanged()
        })
        viewModel.errorMessage.observe(this, { msg ->
            msg?.let {
                showAlertDialog(msg)
            }
        })
        viewModel.query.observe(this, { query ->
            supportActionBar?.title = query
        })
        viewModel.showProgress.observe(this, { show ->
            showProgressBar(show)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_article_list_menu, menu)
        searchMenu = menu.findItem(R.id.activity_article_list_menu_search)
        val searchView = searchMenu.actionView as SearchView

        // handle search locally
//        searchView.apply {
//            setIconifiedByDefault(true)
//        }
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(input: String): Boolean {
//                search(input.trim())
//                return true
//            }
//
//            override fun onQueryTextChange(p0: String?): Boolean {
//                return true
//            }
//        })
//        return super.onCreateOptionsMenu(menu)

        // handle search via a search provider, making this activity the searchable activity
        // setup SearchView to send a search intent
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(true)
        }
        return true
    }

    // receive and handle the search intent sent by the SearchView
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == Intent.ACTION_SEARCH) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->

                // save query to recent suggestions
                saveQueryToRecentSuggestions(query)

                // perform search
                search(query)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.activity_article_list_menu_clear_history -> {
                SearchRecentSuggestions(
                    this,
                    ArticleSearchSuggestionProvider.AUTHORITY,
                    ArticleSearchSuggestionProvider.MODE
                )
                    .clearHistory()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun search(query: String) {
        query.let {

            // update UI
            this@ArticleListActivity.hideKeyboard()
            searchMenu.collapseActionView()

            // perform search
            viewModel.setQuery(query)
        }
    }

    private fun saveQueryToRecentSuggestions(query: String) {
        SearchRecentSuggestions(
            this,
            ArticleSearchSuggestionProvider.AUTHORITY,
            ArticleSearchSuggestionProvider.MODE
        )
            .saveRecentQuery(query, null)
    }

    private fun showAlertDialog(message: String?) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder
            .setMessage(message ?: "Something went wrong...")
            .setCancelable(false)
            .setPositiveButton("Ok") { _, _ ->
                run {
                    // user dismissed the error dialog, clear the error message
                    viewModel.showError(null)
                }
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Error")
        alert.show()
    }
}
