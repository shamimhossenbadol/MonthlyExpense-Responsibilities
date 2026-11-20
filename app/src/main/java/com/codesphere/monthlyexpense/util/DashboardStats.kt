package com.codesphere.monthlyexpense.util

import com.codesphere.monthlyexpense.data.model.MonthlyExpense

data class DashboardStats(
    val totalBudget: Double = 0.0,
    val paidAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    /** 0f..1f share of the budget already paid. */
    val progressPercent: Float = 0f,
    val paidCount: Int = 0,
    val totalCount: Int = 0
) {
    companion object {
        fun from(expenses: List<MonthlyExpense>): DashboardStats {
            if (expenses.isEmpty()) return DashboardStats()
            val total = expenses.sumOf { it.amount }
            val paid = expenses.filter { it.isPaid }.sumOf { it.amount }
            return DashboardStats(
                totalBudget = total,
                paidAmount = paid,
                remainingAmount = total - paid,
                progressPercent = if (total > 0) (paid / total).toFloat().coerceIn(0f, 1f) else 0f,
                paidCount = expenses.count { it.isPaid },
                totalCount = expenses.size
            )
        }
    }
}
