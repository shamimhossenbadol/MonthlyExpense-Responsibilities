package com.codesphere.monthlyexpense.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.codesphere.monthlyexpense.ui.theme.SuccessGreen
import com.codesphere.monthlyexpense.ui.theme.SuccessGreenLight
import com.codesphere.monthlyexpense.util.DashboardStats
import com.codesphere.monthlyexpense.util.Formatters

@Composable
fun DashboardSummaryCard(
    stats: DashboardStats,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = stats.progressPercent.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "PaymentProgressBar"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top row: Total Budget Highlight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL BUDGET",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatCurrency(stats.totalBudget),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = PrimaryText
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AccentIndigoLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccountBalanceWallet,
                        contentDescription = "Wallet",
                        tint = AccentIndigo,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payment Progress",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = PrimaryText
                    )
                    val percentInt = (stats.progressPercent * 100).toInt()
                    Text(
                        text = "$percentInt% completed (${stats.paidCount}/${stats.totalCount})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (percentInt == 100 && stats.totalCount > 0) SuccessGreen else AccentIndigo
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (animatedProgress >= 1f && stats.totalCount > 0) SuccessGreen else AccentIndigo,
                    trackColor = BorderColor,
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sub-metrics: Paid vs Remaining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricPill(
                    icon = Icons.Rounded.CheckCircle,
                    iconColor = SuccessGreen,
                    iconBg = SuccessGreenLight,
                    title = "Paid",
                    amount = Formatters.formatCurrency(stats.paidAmount),
                    modifier = Modifier.weight(1f)
                )

                MetricPill(
                    icon = Icons.Rounded.HourglassEmpty,
                    iconColor = PendingAmber,
                    iconBg = PendingAmberLight,
                    title = "Remaining",
                    amount = Formatters.formatCurrency(stats.remainingAmount),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricPill(
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    title: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(iconBg.copy(alpha = 0.5f))
            .border(1.dp, iconColor.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = PrimaryText
                )
            }
        }
    }
}
