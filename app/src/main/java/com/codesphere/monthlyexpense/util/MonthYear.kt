package com.codesphere.monthlyexpense.util

import java.util.Calendar

/** A calendar month. [month] is 1-12. Ordering is chronological. */
data class MonthYear(val year: Int, val month: Int) : Comparable<MonthYear> {

    init {
        require(month in 1..12) { "month must be 1..12, was $month" }
    }

    /** Single sortable integer, e.g. 202611 for November 2026. */
    val key: Int get() = year * 100 + month

    fun plusMonths(delta: Int): MonthYear {
        val zeroBased = year * 12 + (month - 1) + delta
        return MonthYear(Math.floorDiv(zeroBased, 12), Math.floorMod(zeroBased, 12) + 1)
    }

    /** Epoch millis of the first instant of the following month, in the device time zone. */
    fun startOfNextMonthMillis(): Long {
        val next = plusMonths(1)
        return Calendar.getInstance().apply {
            clear()
            set(next.year, next.month - 1, 1, 0, 0, 0)
        }.timeInMillis
    }

    override fun compareTo(other: MonthYear): Int = key.compareTo(other.key)

    companion object {
        fun current(): MonthYear {
            val cal = Calendar.getInstance()
            return MonthYear(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
        }

        fun fromKey(key: Int): MonthYear = MonthYear(key / 100, key % 100)
    }
}
