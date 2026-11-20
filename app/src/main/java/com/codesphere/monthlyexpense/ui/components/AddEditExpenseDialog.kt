package com.codesphere.monthlyexpense.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.codesphere.monthlyexpense.ui.theme.ElevatedSurface
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.codesphere.monthlyexpense.data.model.MonthlyExpense
import com.codesphere.monthlyexpense.ui.theme.AccentIndigo
import com.codesphere.monthlyexpense.ui.theme.OnAccent
import com.codesphere.monthlyexpense.ui.theme.AccentIndigoLight
import com.codesphere.monthlyexpense.ui.theme.Background
import com.codesphere.monthlyexpense.ui.theme.BorderColor
import com.codesphere.monthlyexpense.ui.theme.CardSurface
import com.codesphere.monthlyexpense.ui.theme.PrimaryText
import com.codesphere.monthlyexpense.ui.theme.SecondaryText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditExpenseDialog(
    initialExpense: MonthlyExpense? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, amount: Double, icon: String) -> Unit
) {
    val isEditing = initialExpense != null

    var name by remember { mutableStateOf(initialExpense?.name ?: "") }
    var amountText by remember {
        mutableStateOf(
            initialExpense?.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: ""
        )
    }
    var icon by remember { mutableStateOf(initialExpense?.icon ?: ExpenseIcons.DEFAULT) }
    // Until the user taps an icon, follow the name they type (e.g. "Water bill" -> water drop).
    var iconPickedManually by remember { mutableStateOf(isEditing) }

    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AccentIndigo,
        unfocusedBorderColor = BorderColor,
        focusedLabelColor = AccentIndigo,
        cursorColor = AccentIndigo
    )

    fun submit() {
        val trimmedName = name.trim()
        val amount = amountText.replace(",", "").toDoubleOrNull()
        nameError = trimmedName.isEmpty()
        amountError = amount == null || amount <= 0
        if (!nameError && !amountError && amount != null) onSave(trimmedName, amount, icon)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CardSurface,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderColor, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = if (isEditing) "Edit Expense" else "New Expense",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryText
                )

                Spacer(modifier = Modifier.height(20.dp))

                IconPicker(
                    selected = icon,
                    onSelect = {
                        icon = it
                        iconPickedManually = true
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                        if (!iconPickedManually) icon = ExpenseIcons.guess(it) ?: ExpenseIcons.DEFAULT
                    },
                    label = { Text("Title") },
                    placeholder = { Text("e.g. House Rent") },
                    isError = nameError,
                    supportingText = if (nameError) ({ Text("Title is required") }) else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        amountText = input.filter { it.isDigit() || it == '.' }
                        amountError = false
                    },
                    label = { Text("Amount") },
                    placeholder = { Text("0") },
                    leadingIcon = {
                        Text(
                            text = "৳",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = AccentIndigo
                        )
                    },
                    isError = amountError,
                    supportingText = if (amountError) ({ Text("Enter an amount greater than 0") }) else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DialogButton(
                        text = "Cancel",
                        container = Background,
                        content = SecondaryText,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    DialogButton(
                        text = if (isEditing) "Save" else "Add Expense",
                        container = AccentIndigo,
                        content = OnAccent,
                        onClick = ::submit,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private const val ICON_COLUMNS = 6
private const val COLLAPSED_ROWS = 2

/**
 * Collapsed: 2 rows = the most common icons + a "more" tile in the last slot. The selected icon is
 * always visible (it takes the last icon slot if it isn't one of the common ones).
 * Expanded: every icon, with a "less" tile at the end. Picking an icon collapses the grid.
 */
@Composable
private fun IconPicker(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val all = ExpenseIcons.all
    val collapsedCount = ICON_COLUMNS * COLLAPSED_ROWS - 1

    val visible = when {
        expanded -> all
        all.take(collapsedCount).any { it.first == selected } -> all.take(collapsedCount)
        else -> all.take(collapsedCount - 1) + all.filter { it.first == selected }
    }
    // null = the more/less toggle tile
    val tiles: List<Pair<String, ImageVector>?> = visible + null

    Column(
        modifier = Modifier.fillMaxWidth().animateContentSize(tween(250)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tiles.chunked(ICON_COLUMNS).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { tile ->
                    val cell = Modifier.weight(1f).aspectRatio(1f)
                    if (tile == null) {
                        IconChoice(
                            vector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.MoreHoriz,
                            selected = false,
                            description = if (expanded) "Show fewer icons" else "Show all icons",
                            modifier = cell,
                            onClick = { expanded = !expanded }
                        )
                    } else {
                        IconChoice(
                            vector = tile.second,
                            selected = tile.first == selected,
                            description = tile.first,
                            modifier = cell,
                            onClick = {
                                onSelect(tile.first)
                                expanded = false
                            }
                        )
                    }
                }
                repeat(ICON_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun IconChoice(
    vector: ImageVector,
    selected: Boolean,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(if (selected) AccentIndigo else ElevatedSurface, tween(200), label = "IconBg")
    val tint by animateColorAsState(if (selected) OnAccent else SecondaryText, tween(200), label = "IconTint")
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .semantics { contentDescription = description }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = vector, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun DialogButton(
    text: String,
    container: Color,
    content: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        modifier = modifier.height(48.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}
