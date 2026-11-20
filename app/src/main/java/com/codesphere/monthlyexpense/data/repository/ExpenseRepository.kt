package com.codesphere.monthlyexpense.data.repository

import com.codesphere.monthlyexpense.data.dao.ExpenseDao
import com.codesphere.monthlyexpense.data.model.ExpenseTemplate
import com.codesphere.monthlyexpense.data.model.MonthlyExpense
import com.codesphere.monthlyexpense.util.MonthYear
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Data model:
 *  - [ExpenseTemplate]: the recurring responsibility (name, expected amount). Soft-deleted via isActive.
 *  - [MonthlyExpense]: that responsibility's record for one calendar month, incl. payment status/date.
 *    Past months are never rewritten by template edits, deletions or the monthly reset.
 *
 * All writes go through one [Mutex] so a rollover can never run twice concurrently, and the
 * unique (templateId, year, month) index is a second guard against duplicate monthly rows.
 */
class ExpenseRepository(
    private val dao: ExpenseDao,
    private val clock: () -> MonthYear = { MonthYear.current() },
    private val now: () -> Long = { System.currentTimeMillis() }
) {
    private val mutex = Mutex()

    fun getExpensesForMonth(month: MonthYear): Flow<List<MonthlyExpense>> =
        dao.getExpensesForMonth(month.year, month.month)

    fun getEarliestMonth(): Flow<MonthYear?> =
        dao.getEarliestMonthKey().map { key -> key?.let(MonthYear::fromKey) }

    /**
     * Starts the tracking cycle for the current calendar month if needed. Idempotent: every active
     * template gets exactly one Pending row for this month; existing rows (and their payment
     * status) are left untouched, and earlier months are never modified.
     *
     * @return the current month.
     */
    suspend fun ensureCurrentMonth(): MonthYear = mutex.withLock {
        val month = clock()
        val existing = dao.getExpensesForMonthOnce(month.year, month.month)
            .mapTo(HashSet()) { it.templateId }
        val missing = dao.getActiveTemplatesOnce()
            .filter { it.id !in existing }
            .map { it.toMonthly(month) }
        if (missing.isNotEmpty()) dao.insertMonthlyExpenses(missing)
        month
    }

    suspend fun addExpense(name: String, amount: Double, icon: String) = mutex.withLock {
        val month = clock()
        val template = ExpenseTemplate(name = name, amount = amount, icon = icon, createdAt = now())
        val id = dao.insertTemplate(template)
        dao.insertMonthlyExpenses(listOf(template.copy(id = id).toMonthly(month)))
    }

    /** New name/amount apply to the template (future months) and the current month only. */
    suspend fun updateExpense(templateId: Long, name: String, amount: Double, icon: String) =
        mutex.withLock {
            val template = dao.getTemplateById(templateId) ?: return@withLock
            dao.updateTemplate(template.copy(name = name, amount = amount, icon = icon))

            val month = clock()
            dao.getExpensesForMonthOnce(month.year, month.month)
                .find { it.templateId == templateId }
                ?.let { dao.updateMonthlyExpense(it.copy(name = name, amount = amount, icon = icon)) }
        }

    /** Stops the responsibility recurring and removes it from the current month. History is kept. */
    suspend fun deleteExpense(templateId: Long) = mutex.withLock {
        dao.deactivateTemplate(templateId)
        val month = clock()
        dao.deleteMonthlyExpenseByTemplate(templateId, month.year, month.month)
    }

    /** @return the new paid state, or null if the record no longer exists. */
    suspend fun togglePaymentStatus(expenseId: Long): Boolean? = mutex.withLock {
        val expense = dao.getMonthlyExpenseById(expenseId) ?: return@withLock null
        val paid = !expense.isPaid
        dao.updatePaymentStatus(expenseId, paid, if (paid) now() else null)
        paid
    }

    private fun ExpenseTemplate.toMonthly(month: MonthYear) = MonthlyExpense(
        templateId = id,
        name = name,
        amount = amount,
        notes = notes,
        icon = icon,
        year = month.year,
        month = month.month
    )
}
