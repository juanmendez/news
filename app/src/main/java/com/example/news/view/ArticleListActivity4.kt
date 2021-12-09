package com.example.news.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.example.news.R
import com.example.news.MyApplication
import com.example.news.mvi.ArticleListStateEvent
import com.example.news.util.InjectorUtil
import com.example.news.viewmodel.ArticleListActivityViewModel4
import com.example.news.viewmodel.ArticleListActivityViewModel4Factory
import kotlinx.android.synthetic.main.activity_article_list.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.news.model.Article

/**
 * Same as ArticleListActivity, but implements MVI Architecture and uses Composable framework for
 * UI. In response to user actions the View sends StateEvents to the ViewModel. The ViewModel
 * handles the StateEvents via the Repository and sends back ViewStates wrapped in DataStates to
 * the View.
 */
class ArticleListActivity4 : BaseActivity() {

    private lateinit var searchMenu: MenuItem

    // instantiates the ViewModel using a factory, constructor-injects the repository interface
    // implementation needed by the ViewModel instance, the repository needs the application context
    // for caching purposes
    private val viewModel4: ArticleListActivityViewModel4 by viewModels {
        val repository3 = InjectorUtil.provideRepository3(application as MyApplication)
        ArticleListActivityViewModel4Factory(repository3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CustomTheme { // handles Dark mode

                // observed by the UI, re-compose ArticleList when modified
                val articles by viewModel4.articles.observeAsState()

                ArticleList(articles)
            }
        }

        initObservers()
    }

    private fun initObservers() {
        // AppCompatActivity implements LifeCycleOwner single method interface and exposes it via
        // getLifecycle. By adding the ViewModel as an observer we allow the ViewModel to handle
        // the Activity's lifecycle events via annotations
        lifecycle.addObserver(viewModel4)

        /**
         * MVI Architecture: the View observes the DataState which contains the state of the
         * whole View: its data, which will be set to the ViewState (observed for UI updates)
         * and its state (loading state, error state).
         */
        viewModel4.dataState.observe(this, { dataState ->

            // update ViewState
            dataState.data?.let { event ->
                event.getContentIfNotConsumed()?.let { articleListViewState ->
                    viewModel4.updateViewState(articleListViewState)
                }
            }

//            // update loading state
//            showProgressBar(dataState.status == Status.LOADING)
//
//            // update error state
//            dataState.message?.let { event ->
//                event.getContentIfNotConsumed()?.let { message ->
//                    showAlertDialog(message)
//                }
//            }
//
//            // update refresh state
//            if (viewModel4.viewState.value?.page == 1) {
//                swipe_refresh.isRefreshing = false
//            }
        })
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
        return  when (item.itemId) {
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
            this@ArticleListActivity4.hideKeyboard()
            searchMenu.collapseActionView()
            /**
             * MVI Architecture: user actions trigger events being sent from View to ViewModel.
             * Here we send GetArticlesEvent to the ViewModel when the user performs a search.
             */
            viewModel4.setStateEvent(ArticleListStateEvent.GetArticlesEvent(query, 1))
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