package com.example.englishlearners

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Utils {
    companion object {
        private fun timestampToDate(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }

        fun getTimeSince(begin: Long): String {
            val currentTimestamp = System.currentTimeMillis()
            val elapsedTime = currentTimestamp - begin

            val elapsedTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime)
            val elapsedTimeInMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime)
            val elapsedTimeInHours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
            val elapsedTimeInDays = TimeUnit.MILLISECONDS.toDays(elapsedTime)

            val displayTimeSince = if (elapsedTimeInDays > 7) {
                timestampToDate(begin)
            } else if (elapsedTimeInDays.toInt() != 0) {
                "$elapsedTimeInDays ngày trước"
            } else if (elapsedTimeInHours.toInt() != 0) {
                "$elapsedTimeInHours giờ trước"
            } else if (elapsedTimeInMinutes.toInt() != 0) {
                "$elapsedTimeInMinutes phút trước"
            } else {
                "$elapsedTimeInSeconds giây trước"
            }

            return  displayTimeSince
        }
    }
}