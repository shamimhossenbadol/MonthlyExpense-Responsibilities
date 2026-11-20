package com.codesphere.monthlyexpense

import com.codesphere.monthlyexpense.data.dao.ExpenseDao
import com.codesphere.monthlyexpense.data.model.ExpenseTemplate
import com.codesphere.monthlyexpense.data.model.MonthlyExpense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory ExpenseDao that mirrors the Room behaviour the repository relies on, incl. the unique index. */
class FakeExpenseDao : ExpenseDao {
    val templates = mutableListOf<ExpenseTemplate>()
    private val records = MutableStateFlow<List<MonthlyExpense>>(emptyList())
    val allRecords: List<MonthlyExpense> get() = records.value
    private var nextTemplateId = 1L
    private var nextRecordId = 1L

    override suspend fun insertTemplate(template: ExpenseTemplate): Long {
        val id = nextTemplateId++
        templates += template.copy(id = id)
        return id
    }

    override suspend fun updateTemplate(template: ExpenseTemplate) {
        templates.replaceAll { if (it.id == template.id) template else it }
    }

    override suspend fun deactivateTemplate(templateId: Long) {
        templates.replaceAll { if (it.id == templateId) it.copy(isActive = false) else it }
    }

    override suspend fun getActiveTemplatesOnce() =
        templates.filter { it.isActive }.sortedWith(compareBy({ it.createdAt }, { it.id }))

    override suspend fun getTemplateById(id: Long) = templates.find { it.id == id }

    override suspend fun insertMonthlyExpenses(expenses: List<MonthlyExpense>) {
        val current = records.value.toMutableList()
        for (e in expenses) {
            val dup = current.any { it.templateId == e.templateId && it.year == e.year && it.month == e.month }
            if (!dup) current += e.copy(id = nextRecordId++)
        }
        records.value = current
    }

    override suspend fun updateMonthlyExpense(expense: MonthlyExpense) {
        records.value = records.value.map { if (it.id == expense.id) expense else it }
    }

    override fun getExpensesForMonth(year: Int, month: Int): Flow<List<MonthlyExpense>> =
        records.map { list -> list.filter { it.year == year && it.month == month } }

    override suspend fun getExpensesForMonthOnce(year: Int, month: Int) =
        records.value.filter { it.year == year && it.month == month }

    override suspend fun getMonthlyExpenseById(id: Long) = records.value.find { it.id == id }

    override suspend fun updatePaymentStatus(id: Long, isPaid: Boolean, paidDate: Long?) {
        records.value = records.value.map { if (it.id == id) it.copy(isPaid = isPaid, paidDate = paidDate) else it }
    }

    override suspend fun deleteMonthlyExpenseByTemplate(templateId: Long, year: Int, month: Int) {
        records.value = records.value.filterNot { it.templateId == templateId && it.year == year && it.month == month }
    }

    override fun getEarliestMonthKey(): Flow<Int?> =
        records.map { list -> list.minOfOrNull { it.year * 100 + it.month } }
}
