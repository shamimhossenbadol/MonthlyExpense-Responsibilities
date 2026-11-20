package com.codesphere.monthlyexpense

import com.codesphere.monthlyexpense.util.DashboardStats
import com.codesphere.monthlyexpense.util.Formatters
import com.codesphere.monthlyexpense.util.MonthYear
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class MonthYearAndFormattersTest {

    @Test
    fun plusMonthsCrossesYearBoundaries() {
        assertEquals(MonthYear(2027, 1), MonthYear(2026, 12).plusMonths(1))
        assertEquals(MonthYear(2025, 12), MonthYear(2026, 1).plusMonths(-1))
        assertEquals(MonthYear(2024, 11), MonthYear(2026, 11).plusMonths(-24))
    }

    @Test
    fun ordering() {
        assertTrue(MonthYear(2026, 12) < MonthYear(2027, 1))
        assertEquals(202611, MonthYear(2026, 11).key)
        assertEquals(MonthYear(2026, 11), MonthYear.fromKey(202611))
    }

    @Test
    fun startOfNextMonthIsFirstDayMidnight() {
        val cal = Calendar.getInstance().apply { timeInMillis = MonthYear(2026, 12).startOfNextMonthMillis() }
        assertEquals(2027, cal.get(Calendar.YEAR))
        assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH))
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun currencyUsesTakaSignAndThousandsSeparators() {
        assertEquals("৳125,000", Formatters.formatCurrency(125000.0))
        assertEquals("৳1,200.5", Formatters.formatCurrency(1200.5))
        assertEquals("৳0", Formatters.formatCurrency(0.0))
    }

    @Test
    fun emptyStatsAreZero() {
        val s = DashboardStats.from(emptyList())
        assertEquals(0.0, s.totalBudget, 0.0)
        assertEquals(0f, s.progressPercent, 0f)
    }
}
