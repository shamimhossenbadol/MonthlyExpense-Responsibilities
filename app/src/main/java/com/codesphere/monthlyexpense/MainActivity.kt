package com.codesphere.monthlyexpense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.codesphere.monthlyexpense.ui.screens.MainExpenseScreen
import com.codesphere.monthlyexpense.ui.theme.Background
import com.codesphere.monthlyexpense.ui.theme.MonthlyExpenseTheme
import com.codesphere.monthlyexpense.ui.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dark-only app: always light status/nav bar icons, whatever the system theme.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            MonthlyExpenseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    MainExpenseScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Starts the new month's cycle if the calendar month changed while the app was in the background.
        viewModel.onAppResumed()
    }
}
