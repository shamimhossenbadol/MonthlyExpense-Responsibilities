package com.codesphere.monthlyexpense

import com.codesphere.monthlyexpense.data.repository.ExpenseRepository
import com.codesphere.monthlyexpense.util.DashboardStats
import com.codesphere.monthlyexpense.util.MonthYear
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExpenseRepositoryTest {
    private val nov = MonthYear(2026, 11)
    private val dec = MonthYear(2026, 12)
    private val jan = MonthYear(2027, 1)

    private lateinit var dao: FakeExpenseDao
    private var today = nov
    private var nowMillis = 1_000L
    private lateinit var repo: ExpenseRepository

    @Before
    fun setUp() {
        dao = FakeExpenseDao()
        today = nov
        repo = ExpenseRepository(dao, clock = { today }, now = { nowMillis++ })
    }

    private suspend fun month(m: MonthYear) = dao.getExpensesForMonthOnce(m.year, m.month)

    /** The user's own six responsibilities, added the way the UI adds them. */
    private suspend fun addSix() {
        repo.addExpense("House Rent", 25000.0, "home")
        repo.addExpense("Parents' Hand Cash", 10000.0, "family")
        repo.addExpense("Sister's Hand Cash", 5000.0, "person")
        repo.addExpense("Electricity Bill", 2500.0, "bolt")
        repo.addExpense("Internet Bill", 1200.0, "wifi")
        repo.addExpense("Personal Expenses", 8000.0, "shopping")
    }

    @Test
    fun freshInstallStartsEmptyWithNoSampleData() = runBlocking {
        repo.ensureCurrentMonth()
        repo.ensureCurrentMonth()
        assertTrue(dao.templates.isEmpty())
        assertTrue(month(nov).isEmpty())
    }

    @Test
    fun addedExpensesStartPendingAndAreNotDuplicatedByRollover() = runBlocking {
        addSix()
        repo.ensureCurrentMonth()
        assertEquals(6, month(nov).size)
        assertTrue(month(nov).none { it.isPaid })
    }

    @Test
    fun deletingEverythingDoesNotReseedDefaults() = runBlocking {
        addSix()
        month(nov).forEach { repo.deleteExpense(it.templateId) }
        repo.ensureCurrentMonth()
        today = dec
        repo.ensureCurrentMonth()
        assertTrue(month(nov).isEmpty())
        assertTrue(month(dec).isEmpty())
    }

    @Test
    fun markPaidRecordsDateAndPendingClearsIt() = runBlocking {
        addSix()
        val rent = month(nov).first()
        assertEquals(true, repo.togglePaymentStatus(rent.id))
        assertNotNull(dao.getMonthlyExpenseById(rent.id)!!.paidDate)
        assertEquals(false, repo.togglePaymentStatus(rent.id))
        val after = dao.getMonthlyExpenseById(rent.id)!!
        assertFalse(after.isPaid)
        assertNull(after.paidDate)
    }

    @Test
    fun rolloverResetsToPendingAndKeepsHistory() = runBlocking {
        addSix()
        month(nov).take(2).forEach { repo.togglePaymentStatus(it.id) }

        today = dec
        repo.ensureCurrentMonth()

        val decRows = month(dec)
        assertEquals(6, decRows.size)
        assertTrue(decRows.all { !it.isPaid && it.paidDate == null })
        // November untouched
        assertEquals(2, month(nov).count { it.isPaid })
        assertTrue(month(nov).filter { it.isPaid }.all { it.paidDate != null })
    }

    @Test
    fun rolloverIsIdempotentAndKeepsPaymentsMadeThisMonth() = runBlocking {
        addSix()
        today = dec
        repo.ensureCurrentMonth()
        val paid = month(dec).first()
        repo.togglePaymentStatus(paid.id)

        repeat(3) { repo.ensureCurrentMonth() }

        assertEquals(6, month(dec).size)
        assertTrue(dao.getMonthlyExpenseById(paid.id)!!.isPaid)
    }

    @Test
    fun concurrentRolloversNeverDuplicate() = runBlocking {
        addSix()
        today = dec
        (1..20).map { async { repo.ensureCurrentMonth() } }.awaitAll()
        assertEquals(6, month(dec).size)
    }

    @Test
    fun rolloverSkipsMonthsWhenAppWasNotOpened() = runBlocking {
        addSix()
        today = jan // app not opened in December
        repo.ensureCurrentMonth()
        assertEquals(6, month(jan).size)
        assertTrue(month(dec).isEmpty())
        assertEquals(2027, today.year)
    }

    @Test
    fun editChangesCurrentAndFutureButNotHistory() = runBlocking {
        addSix()
        val rentNov = month(nov).first { it.name == "House Rent" }
        repo.togglePaymentStatus(rentNov.id)

        today = dec
        repo.ensureCurrentMonth()
        repo.updateExpense(rentNov.templateId, "House Rent", 27000.0, "home")

        today = jan
        repo.ensureCurrentMonth()

        assertEquals(25000.0, month(nov).first { it.templateId == rentNov.templateId }.amount, 0.0)
        assertEquals(27000.0, month(dec).first { it.templateId == rentNov.templateId }.amount, 0.0)
        assertEquals(27000.0, month(jan).first { it.templateId == rentNov.templateId }.amount, 0.0)
        assertTrue(month(nov).first { it.templateId == rentNov.templateId }.isPaid)
    }

    @Test
    fun editKeepsPaymentStatusOfCurrentMonth() = runBlocking {
        addSix()
        val row = month(nov).first()
        repo.togglePaymentStatus(row.id)
        repo.updateExpense(row.templateId, "Renamed", 1.0, "gift")
        val updated = dao.getMonthlyExpenseById(row.id)!!
        assertEquals("Renamed", updated.name)
        assertTrue(updated.isPaid)
    }

    @Test
    fun deleteRemovesFromCurrentAndFutureButKeepsHistory() = runBlocking {
        addSix()
        val internet = month(nov).first { it.name == "Internet Bill" }
        repo.togglePaymentStatus(internet.id)

        today = dec
        repo.ensureCurrentMonth()
        repo.deleteExpense(internet.templateId)
        today = jan
        repo.ensureCurrentMonth()

        assertTrue(month(nov).any { it.templateId == internet.templateId && it.isPaid })
        assertTrue(month(dec).none { it.templateId == internet.templateId })
        assertTrue(month(jan).none { it.templateId == internet.templateId })
        assertEquals(5, month(jan).size)
    }

    @Test
    fun addedExpenseJoinsCurrentMonthAndRecurs() = runBlocking {
        addSix()
        repo.addExpense("Gym", 1500.0, "fitness")
        assertEquals(7, month(nov).size)
        today = dec
        repo.ensureCurrentMonth()
        assertTrue(month(dec).any { it.name == "Gym" && it.amount == 1500.0 && !it.isPaid })
    }

    @Test
    fun iconsCarryIntoNewMonthsAndEditsDontTouchHistory() = runBlocking {
        addSix()
        assertEquals("home", month(nov).first { it.name == "House Rent" }.icon)
        val rent = month(nov).first { it.name == "House Rent" }
        today = dec
        repo.ensureCurrentMonth()
        repo.updateExpense(rent.templateId, "House Rent", 25000.0, "savings")
        today = jan
        repo.ensureCurrentMonth()
        assertEquals("home", month(nov).first { it.templateId == rent.templateId }.icon)
        assertEquals("savings", month(jan).first { it.templateId == rent.templateId }.icon)
    }

    @Test
    fun earliestMonthTracksHistory() = runBlocking {
        addSix()
        today = dec
        repo.ensureCurrentMonth()
        assertEquals(nov, repo.getEarliestMonth().first())
    }

    @Test
    fun dashboardStatsMatchPayments() = runBlocking {
        addSix() // 25000+10000+5000+2500+1200+8000 = 51700
        val rows = month(nov)
        repo.togglePaymentStatus(rows.first { it.name == "House Rent" }.id)
        repo.togglePaymentStatus(rows.first { it.name == "Internet Bill" }.id)

        val stats = DashboardStats.from(month(nov))
        assertEquals(51700.0, stats.totalBudget, 0.0)
        assertEquals(26200.0, stats.paidAmount, 0.0)
        assertEquals(25500.0, stats.remainingAmount, 0.0)
        assertEquals(26200f / 51700f, stats.progressPercent, 0.0001f)
        assertEquals(2, stats.paidCount)
        assertEquals(6, stats.totalCount)
    }
}
