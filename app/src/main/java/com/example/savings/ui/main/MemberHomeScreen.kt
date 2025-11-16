@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.savings.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.savings.data.models.Transaction
import com.example.savings.data.models.TransactionType
import com.example.savings.ui.members.MemberViewModel
import com.example.savings.ui.transactions.TransactionListItem
import com.example.savings.ui.transactions.TransactionViewModel

@Composable
fun MemberHomeScreen(
    memberViewModel: MemberViewModel,
    transactionViewModel: TransactionViewModel,
    navController: NavController
) {
    // For now, we\'ll assume a hardcoded member ID. In a real app, this would come from the login session.
    val memberId = "1" 
    val member by memberViewModel.getMemberById("1", memberId).collectAsState(initial = null)
    val savingsTransactions by transactionViewModel.getTransactionsForMember("1", memberId).collectAsState(initial = emptyList())
    val loanTransactions by transactionViewModel.getTransactionsForMember("1", memberId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome, ${member?.name ?: ""}") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text("Your Financial Overview", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                FinancialSummary(savings = savingsTransactions, loans = loanTransactions)
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                TransactionHistory(title = "Recent Activity", transactions = savingsTransactions + loanTransactions)
            }
        }
    }
}

@Composable
private fun FinancialSummary(savings: List<Transaction>, loans: List<Transaction>) {
    val totalSavings = savings.sumOf { it.amount }
    val totalLoans = loans.filter { it.type == TransactionType.LOAN }.sumOf { it.amount }

    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            StatItem("Savings", "MK${String.format("%,.2f", totalSavings)}", Icons.Default.ArrowUpward, MaterialTheme.colorScheme.primary)
            Divider(modifier = Modifier.height(60.dp).padding(horizontal = 8.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)))
            StatItem("Loans", "MK${String.format("%,.2f", totalLoans)}", Icons.Default.ArrowDownward, MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun StatItem(title: String, amount: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(amount, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun TransactionHistory(title: String, transactions: List<Transaction>) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (transactions.isEmpty()) {
            Text("No transactions yet.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        } else {
            Card(elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
                Column {
                    transactions.forEachIndexed { index, transaction ->
                        TransactionListItem(transaction)
                        if (index < transactions.size - 1) {
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
