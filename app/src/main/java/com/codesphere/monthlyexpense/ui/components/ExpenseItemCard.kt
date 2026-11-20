package com.codesphere.monthlyexpense.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codesphere.monthlyexpense.data.model.MonthlyExpense
import com.codesphere.monthlyexpense.ui.theme.AccentIndigo
import com.codesphere.monthlyexpense.ui.theme.BorderColor
import com.codesphere.monthlyexpense.ui.theme.CardSurface
import com.codesphere.monthlyexpense.ui.theme.DangerRed
import com.codesphere.monthlyexpense.ui.theme.DividerColor
import com.codesphere.monthlyexpense.ui.theme.PendingAmber
import com.codesphere.monthlyexpense.ui.theme.PendingAmberLight
import com.codesphere.monthlyexpense.ui.theme.PrimaryText
import com.codesphere.monthlyexpense.ui.theme.SecondaryText
import com.codesphere.monthlyexpense.ui.theme.SuccessGreen
import com.codesphere.monthlyexpense.ui.theme.SuccessGreenLight
import com.codesphere.monthlyexpense.ui.theme.TertiaryText
import com.codesphere.monthlyexpense.util.Formatters

@Composable
fun ExpenseItemCard(
    expense: MonthlyExpense,
    onTogglePayment: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    canEdit: Boolean = true
) {
    var menuExpanded by remember { mutableStateOf(false) }

    // Color animations
    val targetBorderColor = if (expense.isPaid) SuccessGreen.copy(alpha = 0.35f) else BorderColor
    val animatedBorderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(durationMillis = 350),
        label = "BorderColor"
    )

    // Check button scale bounce animation
    val checkButtonScale by animateFloatAsState(
        targetValue = if (expense.isPaid) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CheckScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, animatedBorderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        // Tapping anywhere on the card toggles Paid/Pending; no ripple, the icon animation is the feedback.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClickLabel = if (expense.isPaid) "Mark as pending" else "Mark as paid",
                    onClick = onTogglePayment
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interactive Payment Checkbox button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .scale(checkButtonScale)
                    .clip(CircleShape)
                    .background(if (expense.isPaid) SuccessGreenLight else PendingAmberLight)
                    .border(
                        width = 1.5.dp,
                        color = if (expense.isPaid) SuccessGreen else PendingAmber.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = expense.isPaid,
                    transitionSpec = {
                        (scaleIn(
                            initialScale = 0.3f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(tween(150))) togetherWith fadeOut(tween(100))
                    },
                    contentAlignment = Alignment.Center,
                    label = "PaidIcon"
                ) { paid ->
                    if (paid) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Paid. Tap to mark as pending",
                            tint = SuccessGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Icon(
                            imageVector = ExpenseIcons.get(expense.icon),
                            contentDescription = null,
                            tint = PendingAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Main Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = expense.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (expense.isPaid) TextDecoration.None else TextDecoration.None
                        ),
                        color = PrimaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Payment Status badge + paid date / notes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (expense.isPaid && expense.paidDate != null) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Paid on ${Formatters.formatPaidDate(expense.paidDate)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = SuccessGreen
                        )
                    } else {
                        Text(
                            text = "Pending Payment",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = PendingAmber
                        )
                    }

                    if (expense.notes.isNotBlank()) {
                        Text(
                            text = "• ${expense.notes}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp
                            ),
                            color = SecondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount column
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = Formatters.formatCurrency(expense.amount),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (expense.isPaid) SuccessGreen else PrimaryText
                )

                // Status pill
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (expense.isPaid) SuccessGreenLight else PendingAmberLight)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (expense.isPaid) "PAID" else "PENDING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = if (expense.isPaid) SuccessGreen else PendingAmber
                    )
                }
            }

            // More Options menu (past months are history: payments can be fixed, structure can't)
            if (canEdit) Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Options",
                        tint = TertiaryText,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(CardSurface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = null,
                                    tint = PrimaryText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Edit Expense", color = PrimaryText)
                            }
                        },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteOutline,
                                    contentDescription = null,
                                    tint = DangerRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delete", color = DangerRed)
                            }
                        },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
