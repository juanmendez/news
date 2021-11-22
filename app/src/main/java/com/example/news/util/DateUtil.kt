package com.example.news.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtil
constructor(
    private val dateToTimeFormat: SimpleDateFormat,
    private val timeToDateFormat: SimpleDateFormat
) {
    fun dateToTime(date: String): Long {
        return dateToTimeFormat.parse(date)?.time ?: 0
    }

    fun timeToDate(time: Long): String {
        return timeToDateFormat.format(Date(time))
    }
}
