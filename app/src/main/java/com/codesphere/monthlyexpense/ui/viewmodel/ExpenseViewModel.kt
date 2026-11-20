package com.codesphere.monthlyexpense.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.codesphere.monthlyexpense.data.database.AppDatabase
import com.codesphere.monthlyexpense.data.model.MonthlyExpense
import com.codesphere.monthlyexpense.data.repository.ExpenseRepository
import com.codesphere.monthlyexpense.util.DashboardStats
import com.codesphere.monthlyexpense.util.MonthYear
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ExpenseRepository(AppDatabase.getDatabase(application).expenseDao())

    /** The real calendar month. Updated on launch, on resume and at the month boundary. */
    private val _currentMonth = MutableStateFlow(MonthYear.current())
    val currentMonth: StateFlow<MonthYear> = _currentMonth.asStateFlow()

    private val _selectedMonth = MutableStateFlow(_currentMonth.value)
    val selectedMonth: StateFlow<MonthYear> = _selectedMonth.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    val expenses: StateFlow<List<MonthlyExpense>> = _selectedMonth
        .flatMapLatest { repository.getExpensesForMonth(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboardStats: StateFlow<DashboardStats> = expenses
        .map(DashboardStats::from)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    val isCurrentMonth: StateFlow<Boolean> = combine(_selectedMonth, _currentMonth) { s, c -> s == c }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    /** Earliest month that has records; history navigation stops there. */
    val earliestMonth: StateFlow<MonthYear?> = repository.getEarliestMonth()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            refreshCurrentMonth()
            _isLoading.value = false
        }
        // Roll over even if the app stays open across midnight at the end of a month.
        viewModelScope.launch {
            while (true) {
                val wait = _currentMonth.value.startOfNextMonthMillis() - System.currentTimeMillis()
                delay(wait.coerceIn(1_000L, 6 * 60 * 60 * 1000L))
                refreshCurrentMonth()
            }
        }
    }

    /** Called on launch and every time the app returns to the foreground. Safe to call often. */
    fun onAppResumed() {
        viewModelScope.launch { refreshCurrentMonth() }
    }

    private suspend fun refreshCurrentMonth() {
        val previous = _currentMonth.value
        val now = repository.ensureCurrentMonth()
        _currentMonth.value = now
        // If the user was looking at "this month" and the month changed, follow it.
        if (now != previous && _selectedMonth.value == previous) {
            _selectedMonth.value = now
            _toastMessage.value = "New month started — all set to Pending"
        }
    }

    fun goToCurrentMonth() {
        _selectedMonth.value = _currentMonth.value
    }

    fun navigateMonth(delta: Int) {
        val target = _selectedMonth.value.plusMonths(delta)
        val earliest = earliestMonth.value ?: _currentMonth.value
        if (target > _currentMonth.value || target < earliest) return
        _selectedMonth.value = target
    }

    fun addExpense(name: String, amount: Double, icon: String) {
        viewModelScope.launch {
            repository.addExpense(name, amount, icon)
            _selectedMonth.value = _currentMonth.value
            _toastMessage.value = "Expense added"
        }
    }

    fun updateExpense(templateId: Long, name: String, amount: Double, icon: String) {
        viewModelScope.launch {
            repository.updateExpense(templateId, name, amount, icon)
            _toastMessage.value = "Expense updated"
        }
    }

    fun deleteExpense(templateId: Long) {
        viewModelScope.launch {
            repository.deleteExpense(templateId)
            _toastMessage.value = "Expense removed"
        }
    }

    fun togglePaymentStatus(expense: MonthlyExpense) {
        viewModelScope.launch {
            val paid = repository.togglePaymentStatus(expense.id) ?: return@launch
            if (paid) _toastMessage.value = "${expense.name} marked as paid"
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
