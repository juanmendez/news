package com.example.news.view

import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.news.R

abstract class BaseActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar

    override fun setContentView(layoutResID: Int) {
        val rootView = layoutInflater.inflate(R.layout.activity_base, null)
        val activityContentView = rootView.findViewById<FrameLayout>(R.id.activity_content)
        progressBar = rootView.findViewById(R.id.progress_bar)

        // makes the FrameLayout the container of all the Activities that extend this class
        layoutInflater.inflate(layoutResID, activityContentView, true)

        super.setContentView(rootView)
    }

    fun showProgressBar(show: Boolean) {
        when (show) {
            true -> progressBar.visibility = View.VISIBLE
            false -> progressBar.visibility = View.GONE
        }
    }
}
