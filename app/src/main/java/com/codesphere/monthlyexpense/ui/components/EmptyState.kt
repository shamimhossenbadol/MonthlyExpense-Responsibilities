package com.codesphere.monthlyexpense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.codesphere.monthlyexpense.ui.theme.AccentIndigo
import com.codesphere.monthlyexpense.ui.theme.OnAccent
import com.codesphere.monthlyexpense.ui.theme.AccentIndigoLight
import com.codesphere.monthlyexpense.ui.theme.BorderColor
import com.codesphere.monthlyexpense.ui.theme.CardSurface
import com.codesphere.monthlyexpense.ui.theme.PrimaryText
import com.codesphere.monthlyexpense.ui.theme.SecondaryText

@Composable
fun EmptyState(
    onAddExpense: (() -> Unit)?,
    modifier: Modifier = Modifier,
    title: String = "No Responsibilities Yet",
    message: String = "Add recurring expenses like House Rent, Hand Cash, Internet bill to track them every month."
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface)
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .padding(vertical = 48.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AccentIndigoLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
                    contentDescription = null,
                    tint = AccentIndigo,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = PrimaryText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (onAddExpense == null) return@Column

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onAddExpense,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentIndigo,
                    contentColor = OnAccent
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Add First Expense",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}
