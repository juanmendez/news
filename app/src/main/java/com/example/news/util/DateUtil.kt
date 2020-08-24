package com.example.news.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtil
constructor(
    private val dateFormat: SimpleDateFormat
) {
    fun dateToTime(date: String): Long {
        return dateFormat.parse(date).time
    }

    fun timeToDate(time: Long): String {
        return dateFormat.format(Date(time))
    }
}
