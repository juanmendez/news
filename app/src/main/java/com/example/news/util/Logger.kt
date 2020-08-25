package com.example.news.util

import android.util.Log
import com.example.news.BuildConfig.DEBUG

const val TAG = "News"

// In Unit Tests we cannot use Log.d, that's an Android API
// So in Unit Tests we will set isUnitTest to true and thus
// we will use println
var isUnitTest = false

fun log(className: String?, message: String) {
    if (DEBUG && !isUnitTest) {
        Log.d(TAG, "$className: $message")
    } else if (DEBUG && isUnitTest) {
        println("$className: $message")
    }
}
