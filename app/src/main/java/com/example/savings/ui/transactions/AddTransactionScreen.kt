package com.example.savings.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.savings.data.models.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    memberName: String,
    transactionType: String,
    onNavigateBack: () -> Unit,
    onSave: (amount: Double, date: Long, type: TransactionType) -> Unit,
) {
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
    var selectedTransactionType by remember { mutableStateOf(TransactionType.valueOf(transactionType)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) {
        Column(modifier = Modifier.padding(it).fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Transaction Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = memberName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Member") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TransactionTypeDropdown(initialType = selectedTransactionType, onTypeSelected = { selectedTransactionType = it })

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount: MWK") },
                        leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate)),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Date") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { TextButton(onClick = { showDatePicker = true }) { Text("Select") } }
                    )
                }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            selectedDate = dateState.selectedDateMillis ?: System.currentTimeMillis()
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = dateState)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSave(amount.toDoubleOrNull() ?: 0.0, selectedDate, selectedTransactionType) },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Save, contentDescription = "Save Transaction")
                    Spacer(Modifier.size(8.dp))
                    Text("Save Transaction", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionTypeDropdown(initialType: TransactionType, onTypeSelected: (TransactionType) -> Unit) {
    val types = TransactionType.values()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = initialType.name.replace("_", " "),
            onValueChange = {},
            readOnly = true,
            label = { Text("Transaction Type") },
            leadingIcon = { Icon(Icons.Default.SyncAlt, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name.replace("_", " ")) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}
