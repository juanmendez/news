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
import androidx.recyclerview.widget.RecyclerView
import com.example.news.MyApplication
import com.example.news.R
import com.example.news.databinding.ActivityArticleListBinding
import com.example.news.mvi.ArticleListStateEvent
import com.example.news.util.InjectorUtil
import com.example.news.util.Status
import com.example.news.util.TOP_HEADLINES
import com.example.news.util.log
import com.example.news.viewmodel.ArticleListActivityViewModel3
import com.example.news.viewmodel.ArticleListActivityViewModel3Factory

/**
 * Same as ArticleListActivity, but implements MVI Architecture.
 * In response to user actions the View sends StateEvents to the ViewModel. The ViewModel handles
 * the StateEvents via the Repository and sends back ViewStates wrapped in DataStates to the View.
 */
class ArticleListActivity3 : BaseActivity() {
    private lateinit var viewBinding: ActivityArticleListBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var searchMenu: MenuItem
    private lateinit var adapter: ArticlesAdapter
    private var articles: ArrayList<com.example.news.model.Article> = arrayListOf()

    private val listener = object : OnArticleClickListener {
        override fun onArticleClick(article: com.example.news.model.Article) {
            val intent = Intent(this@ArticleListActivity3, WebViewActivity::class.java)
            intent.putExtra(WebViewActivity.URL_EXTRA, article.url)
            this@ArticleListActivity3.startActivity(intent)
        }
    }

    // instantiates the ViewModel using a factory, constructor-injects the repository interface
    // implementation needed by the ViewModel instance, the repository needs the application context
    // for caching purposes
    private val viewModel3: ArticleListActivityViewModel3 by viewModels {
        val repository3 = InjectorUtil.provideRepository3(application as MyApplication)
        ArticleListActivityViewModel3Factory(repository3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityArticleListBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
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
        viewBinding.articlesRecyclerview.layoutManager = linearLayoutManager
        // triggering loading new pages as we scroll to the end of the list
        val scrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (resources.configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE ->
                            if (!viewBinding.articlesRecyclerview.canScrollHorizontally(1)) {
                                /**
                                 * MVI Architecture: user actions trigger events being sent from View to ViewModel.
                                 * Here we send IncrementPage to the ViewModel when the user performs a search.
                                 */
                                viewModel3.setStateEvent(ArticleListStateEvent.IncrementPageEvent)
                            }
                        else ->
                            if (!viewBinding.articlesRecyclerview.canScrollVertically(1)) {
                                /**
                                 * MVI Architecture: user actions trigger events being sent from View to ViewModel.
                                 * Here we send IncrementPage to the ViewModel when the user performs a search.
                                 */
                                viewModel3.setStateEvent(ArticleListStateEvent.IncrementPageEvent)
                            }
                    }
                }
            }
        viewBinding.articlesRecyclerview.addOnScrollListener(scrollListener)
        adapter = ArticlesAdapter(articles, listener)
        viewBinding.articlesRecyclerview.adapter = adapter
        viewBinding.swipeRefresh.setOnRefreshListener {
            /**
             * MVI Architecture: user actions trigger events being sent from View to ViewModel.
             * Here we send Refresh to the ViewModel when the user performs a search.
             */
            viewModel3.setStateEvent(ArticleListStateEvent.RefreshEvent)
        }
    }

    private fun initObservers() {
        // AppCompatActivity implements LifeCycleOwner single method interface and exposes it via
        // getLifecycle. By adding the ViewModel as an observer we allow the ViewModel to handle
        // the Activity's lifecycle events via annotations
        lifecycle.addObserver(viewModel3)

        /**
         * MVI Architecture: the View observes the DataState which contains the state of the
         * whole View: its data, which will be set to the ViewState (observed for UI updates)
         * and its state (loading state, error state).
         */
        viewModel3.dataState.observe(this) { dataState ->

            log("toto", "$dataState")

            // update ViewState
            dataState.data?.let { event ->
                event.getContentIfNotConsumed()?.let { articleListViewState ->
                    viewModel3.updateViewState(articleListViewState)
                }
            }

            // update loading state
            showProgressBar(dataState.status == Status.LOADING)

            // update error state
            dataState.message?.let { event ->
                event.getContentIfNotConsumed()?.let { message ->
                    showAlertDialog(message)
                }
            }

            // update refresh state
            if (viewModel3.viewState.value?.page == 1) {
                viewBinding.swipeRefresh.isRefreshing = false
            }
        }

        /**
         * MVI Architecture: the View observes the ViewState for UI updates.
         */
        viewModel3.viewState.observe(this) { viewState ->
            viewState.articles?.let { data ->
                articles.clear()
                articles.addAll(data)
                adapter.notifyDataSetChanged()
            }
            viewState.query?.let { query ->
                supportActionBar?.title = query
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_article_list_menu, menu)
        searchMenu = menu.findItem(R.id.activity_article_list_menu_search)
        val searchView = searchMenu.actionView as SearchView

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
                saveQueryToRecentSuggestions(query)
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
            this@ArticleListActivity3.hideKeyboard()
            searchMenu.collapseActionView()
            /**
             * MVI Architecture: user actions trigger events being sent from View to ViewModel.
             * Here we send GetArticlesEvent to the ViewModel when the user performs a search.
             */
            viewModel3.setStateEvent(ArticleListStateEvent.GetArticlesEvent(query, 1))
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
                    // TODO clear error message
                }
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Error")
        alert.show()
    }
}
