package com.codesphere.monthlyexpense.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.codesphere.monthlyexpense.data.model.ExpenseTemplate
import com.codesphere.monthlyexpense.data.model.MonthlyExpense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // ---- Templates (recurring responsibilities) ----

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTemplate(template: ExpenseTemplate): Long

    @Update
    suspend fun updateTemplate(template: ExpenseTemplate)

    @Query("UPDATE expense_templates SET isActive = 0 WHERE id = :templateId")
    suspend fun deactivateTemplate(templateId: Long)

    @Query("SELECT * FROM expense_templates WHERE isActive = 1 ORDER BY createdAt ASC, id ASC")
    suspend fun getActiveTemplatesOnce(): List<ExpenseTemplate>

    @Query("SELECT * FROM expense_templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): ExpenseTemplate?


    // ---- Monthly records (history + payments) ----

    /** Ignores rows that already exist for (templateId, year, month): never duplicates a month. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMonthlyExpenses(expenses: List<MonthlyExpense>)

    @Update
    suspend fun updateMonthlyExpense(expense: MonthlyExpense)

    @Query("SELECT * FROM monthly_expenses WHERE year = :year AND month = :month ORDER BY id ASC")
    fun getExpensesForMonth(year: Int, month: Int): Flow<List<MonthlyExpense>>

    @Query("SELECT * FROM monthly_expenses WHERE year = :year AND month = :month ORDER BY id ASC")
    suspend fun getExpensesForMonthOnce(year: Int, month: Int): List<MonthlyExpense>

    @Query("SELECT * FROM monthly_expenses WHERE id = :id")
    suspend fun getMonthlyExpenseById(id: Long): MonthlyExpense?

    @Query("UPDATE monthly_expenses SET isPaid = :isPaid, paidDate = :paidDate WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, isPaid: Boolean, paidDate: Long?)

    @Query("DELETE FROM monthly_expenses WHERE templateId = :templateId AND year = :year AND month = :month")
    suspend fun deleteMonthlyExpenseByTemplate(templateId: Long, year: Int, month: Int)

    /** Earliest month with any record, as year*100+month, or null when there is no data. */
    @Query("SELECT MIN(year * 100 + month) FROM monthly_expenses")
    fun getEarliestMonthKey(): Flow<Int?>
}
