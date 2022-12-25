package com.cemebsa.biomassa.utils

import java.text.SimpleDateFormat
import java.util.*

fun convertLongToDateString(systemTime: Long, pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault())
        .format(systemTime).toString()
}

fun convertStringToDateLong(systemTime: String, pattern: String): Long {
    return SimpleDateFormat(pattern, Locale.getDefault())
        .parse(systemTime)!!.time
}