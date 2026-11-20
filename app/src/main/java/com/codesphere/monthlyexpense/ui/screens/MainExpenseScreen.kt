package com.codesphere.monthlyexpense.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import com.codesphere.monthlyexpense.util.Formatters
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.codesphere.monthlyexpense.util.MonthYear
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codesphere.monthlyexpense.data.model.MonthlyExpense
import com.codesphere.monthlyexpense.ui.components.AddEditExpenseDialog
import com.codesphere.monthlyexpense.ui.components.DashboardSummaryCard
import com.codesphere.monthlyexpense.ui.components.DeleteConfirmationDialog
import com.codesphere.monthlyexpense.ui.components.EmptyState
import com.codesphere.monthlyexpense.ui.components.ExpenseItemCard
import com.codesphere.monthlyexpense.ui.components.MonthSelector
import com.codesphere.monthlyexpense.ui.theme.AccentIndigo
import com.codesphere.monthlyexpense.ui.theme.OnAccent
import com.codesphere.monthlyexpense.ui.theme.AccentIndigoLight
import com.codesphere.monthlyexpense.ui.theme.Background
import com.codesphere.monthlyexpense.ui.theme.ElevatedSurface
import com.codesphere.monthlyexpense.ui.theme.BorderColor
import com.codesphere.monthlyexpense.ui.theme.CardSurface
import com.codesphere.monthlyexpense.ui.theme.PrimaryText
import com.codesphere.monthlyexpense.ui.theme.SecondaryText
import com.codesphere.monthlyexpense.ui.theme.SuccessGreen
import com.codesphere.monthlyexpense.ui.viewmodel.ExpenseViewModel

@Composable
fun MainExpenseScreen(
    viewModel: ExpenseViewModel = viewModel()
) {
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val stats by viewModel.dashboardStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val isCurrentMonth by viewModel.isCurrentMonth.collectAsState()
    val earliestMonth by viewModel.earliestMonth.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<MonthlyExpense?>(null) }
    var deletingExpense by remember { mutableStateOf<MonthlyExpense?>(null) }

    // Toast listener
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElevatedSurface)
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = data.visuals.message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = PrimaryText
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentIndigo)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top App Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "My Responsibilities",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = headerSubtitle(selectedMonth, stats.paidCount, stats.totalCount),
                                style = MaterialTheme.typography.bodyMedium,
                                color = SecondaryText
                            )
                        }

                        IconButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AccentIndigo)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Add expense",
                                tint = OnAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Month Selector
                item {
                    MonthSelector(
                        selectedMonth = selectedMonth,
                        isCurrentMonth = isCurrentMonth,
                        canGoBack = earliestMonth?.let { selectedMonth > it } ?: false,
                        onPreviousMonth = { viewModel.navigateMonth(-1) },
                        onNextMonth = { viewModel.navigateMonth(1) },
                        onReturnToCurrentMonth = { viewModel.goToCurrentMonth() }
                    )
                }

                // Dashboard Statistics Card
                item {
                    DashboardSummaryCard(stats = stats)
                }

                // Expense List or Empty State
                if (expenses.isEmpty()) {
                    item {
                        if (isCurrentMonth) {
                            EmptyState(
                                onAddExpense = { showAddDialog = true },
                                modifier = Modifier.animateItem()
                            )
                        } else {
                            EmptyState(
                                onAddExpense = null,
                                title = "No Records",
                                message = "Nothing was tracked in this month.",
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                } else {
                    items(
                        items = expenses,
                        key = { it.id }
                    ) { expense ->
                        ExpenseItemCard(
                            expense = expense,
                            canEdit = isCurrentMonth,
                            onTogglePayment = { viewModel.togglePaymentStatus(expense) },
                            onEdit = { editingExpense = expense },
                            onDelete = { deletingExpense = expense },
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(300),
                                placementSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                fadeOutSpec = tween(200)
                            )
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Add Expense Dialog
    if (showAddDialog) {
        AddEditExpenseDialog(
            initialExpense = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, amount, icon ->
                viewModel.addExpense(name, amount, icon)
                showAddDialog = false
            }
        )
    }

    // Edit Expense Dialog
    editingExpense?.let { expense ->
        AddEditExpenseDialog(
            initialExpense = expense,
            onDismiss = { editingExpense = null },
            onSave = { name, amount, icon ->
                viewModel.updateExpense(expense.templateId, name, amount, icon)
                editingExpense = null
            }
        )
    }

    // Delete Confirmation Dialog
    deletingExpense?.let { expense ->
        DeleteConfirmationDialog(
            expenseName = expense.name,
            onDismiss = { deletingExpense = null },
            onConfirm = {
                viewModel.deleteExpense(expense.templateId)
                deletingExpense = null
            }
        )
    }
}

/** "November 2026 · 4 of 7 paid", or "· All 7 paid" once everything is settled. */
private fun headerSubtitle(month: MonthYear, paid: Int, total: Int): AnnotatedString = buildAnnotatedString {
    append(Formatters.formatMonthYear(month.year, month.month))
    if (total == 0) return@buildAnnotatedString
    append("  ·  ")
    if (paid == total) {
        withStyle(SpanStyle(color = SuccessGreen, fontWeight = FontWeight.SemiBold)) {
            append("All $total paid")
        }
    } else {
        withStyle(SpanStyle(color = PrimaryText, fontWeight = FontWeight.SemiBold)) {
            append("$paid of $total")
        }
        append(" paid")
    }
}
