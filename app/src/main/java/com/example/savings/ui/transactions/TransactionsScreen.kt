package com.example.savings.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.savings.data.models.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    transactions: List<Transaction>,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Transactions") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) {
        Column(modifier = Modifier.padding(it).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (transactions.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = "No transactions", modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text("No transactions yet.", style = MaterialTheme.typography.headlineSmall)
                    Text("Add a transaction to see it listed here.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(transactions) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Transaction", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.description, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(transaction.date)), style = MaterialTheme.typography.bodyMedium)
            }
            Text("MK${transaction.amount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
