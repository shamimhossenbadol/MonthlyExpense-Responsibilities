package com.codesphere.monthlyexpense.data.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

/**
 * One responsibility in one calendar month. This is the historical/payment record:
 * name and amount are snapshotted from the template when the month starts, so later
 * template edits or deletions never change past months.
 */
@Entity(
    tableName = "monthly_expenses",
    indices = [Index(value = ["templateId", "year", "month"], unique = true)]
)
data class MonthlyExpense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateId: Long,
    val name: String,
    val amount: Double,
    val isPaid: Boolean = false,
    val paidDate: Long? = null,
    val notes: String = "",
    val year: Int,
    val month: Int, // 1-12
    @ColumnInfo(defaultValue = "receipt")
    val icon: String = "receipt"
)
