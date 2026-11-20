package com.codesphere.monthlyexpense.data.model

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "expense_templates")
data class ExpenseTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    /** Key into ExpenseIcons. */
    @ColumnInfo(defaultValue = "receipt")
    val icon: String = "receipt"
)
