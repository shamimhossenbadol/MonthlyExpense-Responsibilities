package com.codesphere.monthlyexpense.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// The app is dark-only, regardless of the system setting.
private val DarkColorScheme = darkColorScheme(
    primary = AccentIndigo,
    onPrimary = OnAccent,
    primaryContainer = AccentIndigoLight,
    onPrimaryContainer = AccentIndigoDark,
    secondary = SecondaryText,
    onSecondary = Background,
    background = Background,
    onBackground = PrimaryText,
    surface = CardSurface,
    onSurface = PrimaryText,
    surfaceVariant = ElevatedSurface,
    onSurfaceVariant = SecondaryText,
    surfaceContainer = CardSurface,
    surfaceContainerHigh = ElevatedSurface,
    surfaceContainerHighest = ElevatedSurface,
    inverseSurface = PrimaryText,
    inverseOnSurface = Background,
    outline = BorderColor,
    outlineVariant = DividerColor,
    error = DangerRed,
    onError = Background,
    errorContainer = DangerRedLight,
    tertiary = SuccessGreen,
    tertiaryContainer = SuccessGreenLight
)

@Composable
fun MonthlyExpenseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
