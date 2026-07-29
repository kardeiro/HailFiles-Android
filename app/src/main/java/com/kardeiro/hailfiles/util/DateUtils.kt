package com.kardeiro.hailfiles.util

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("dd 'de' MMM 'de' yyyy", Locale("pt", "BR"))

    fun formatDate(dateString: String): String {
        return try {
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
}
