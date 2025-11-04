package com.example.savings.ui.savings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.savings.data.Saving

@Composable
fun SavingsScreen(viewModel: SavingsViewModel) {
    val savings by viewModel.savings.collectAsState()
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val amountDouble = amount.toDoubleOrNull()
                if (amountDouble != null) {
                    viewModel.addSaving(amountDouble, description, System.currentTimeMillis())
                    amount = ""
                    description = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add Saving")
        }
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(savings) { saving ->
                SavingItem(saving)
            }
        }
    }
}

@Composable
fun SavingItem(saving: Saving) {
    Text("Amount: ${saving.amount}, Description: ${saving.description}")
}
