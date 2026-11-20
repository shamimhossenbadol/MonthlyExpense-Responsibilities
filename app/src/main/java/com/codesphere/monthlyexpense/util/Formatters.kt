package com.codesphere.monthlyexpense.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {

    private val banglaOrEnglishLocale = Locale.US

    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(banglaOrEnglishLocale).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }
        val formattedNumber = formatter.format(amount)
        return "৳$formattedNumber"
    }

    fun formatMonthYear(year: Int, month: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
        return sdf.format(cal.time)
    }

    fun formatShortMonthYear(year: Int, month: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val sdf = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
        return sdf.format(cal.time)
    }

    fun formatPaidDate(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("d MMM, hh:mm a", Locale.ENGLISH)
        return sdf.format(Date(timestampMillis))
    }
}
