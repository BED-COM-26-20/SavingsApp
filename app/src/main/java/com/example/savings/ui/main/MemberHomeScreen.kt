package com.example.savings.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.savings.ui.members.MemberViewModel
import com.example.savings.ui.members.details.FinancialSummary
import com.example.savings.ui.members.details.TransactionHistory
import com.example.savings.ui.transactions.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberHomeScreen(
    memberViewModel: MemberViewModel,
    transactionViewModel: TransactionViewModel
) {
    // For now, we'll assume a hardcoded member ID. In a real app, this would come from the login session.
    val memberId = 1 
    val member by memberViewModel.getMemberById(memberId).collectAsState(initial = null)
    val savingsTransactions by transactionViewModel.getTransactionsForMember(memberId).collectAsState(initial = emptyList())
    val loanTransactions by transactionViewModel.getTransactionsForMember(memberId).collectAsState(initial = emptyList())

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
