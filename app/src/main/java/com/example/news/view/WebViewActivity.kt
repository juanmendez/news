package com.example.news.view

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.news.R
import kotlinx.android.synthetic.main.activity_webview.*


class WebViewActivity : BaseActivity() {

    companion object {
        const val URL_EXTRA = "com.example.news.view.ArticleActivity.URL_EXTRA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        if (intent.hasExtra(URL_EXTRA)) {
            val url = intent.getStringExtra(URL_EXTRA)
            url?.let {
                web_view.settings.javaScriptEnabled = true
                web_view.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        view.loadUrl(url)
                        return false
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        showProgressBar()
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        hideProgressBar()
                    }
                }
                web_view.loadUrl(url)
            }
        }
    }
}
