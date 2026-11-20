package com.codesphere.monthlyexpense.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codesphere.monthlyexpense.ui.theme.AccentIndigo
import com.codesphere.monthlyexpense.ui.theme.AccentIndigoLight
import com.codesphere.monthlyexpense.ui.theme.BorderColor
import com.codesphere.monthlyexpense.ui.theme.CardSurface
import com.codesphere.monthlyexpense.ui.theme.PendingAmber
import com.codesphere.monthlyexpense.ui.theme.PendingAmberLight
import com.codesphere.monthlyexpense.ui.theme.PrimaryText
import com.codesphere.monthlyexpense.ui.theme.SecondaryText
import com.codesphere.monthlyexpense.util.MonthYear
import com.codesphere.monthlyexpense.util.Formatters

@Composable
fun MonthSelector(
    selectedMonth: MonthYear,
    isCurrentMonth: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onReturnToCurrentMonth: () -> Unit,
    modifier: Modifier = Modifier,
    canGoBack: Boolean = true
) {
    val canGoForward = !isCurrentMonth

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardSurface)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onPreviousMonth,
                enabled = canGoBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronLeft,
                    contentDescription = "Previous Month",
                    tint = if (canGoBack) PrimaryText else BorderColor
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = null,
                    tint = AccentIndigo,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Formatters.formatMonthYear(selectedMonth.year, selectedMonth.month),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = PrimaryText
                )
            }

            IconButton(
                onClick = onNextMonth,
                enabled = canGoForward,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = "Next Month",
                    tint = if (canGoForward) PrimaryText else BorderColor
                )
            }
        }

        AnimatedVisibility(
            visible = !isCurrentMonth,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PendingAmberLight)
                    .border(1.dp, PendingAmber.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable { onReturnToCurrentMonth() }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        tint = PendingAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Viewing past month's archive",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = PrimaryText
                    )
                }

                Text(
                    text = "Go to Current →",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentIndigo
                    )
                )
            }
        }
    }
}
