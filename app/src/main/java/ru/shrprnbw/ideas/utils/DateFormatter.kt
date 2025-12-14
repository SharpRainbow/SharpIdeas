package ru.shrprnbw.ideas.utils

import java.text.DateFormat
import java.text.SimpleDateFormat

object DateFormatter {

    private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

    fun formatDateToString(timestamp: Long): String {
        return formatter.format(timestamp)
    }

    fun formatCurrentDate(): String {
        return formatter.format(System.currentTimeMillis())
    }

}