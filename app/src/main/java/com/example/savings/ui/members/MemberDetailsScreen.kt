package com.example.savings.ui.members

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.savings.data.models.Member
import com.example.savings.data.models.Transaction
import com.example.savings.data.models.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailsScreen(
    member: Member,
    savingsTransactions: List<Transaction>,
    loanTransactions: List<Transaction>,
    onNavigateBack: () -> Unit,
    onAddTransaction: (TransactionType) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(member.name) })
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Text("Savings")
            savingsTransactions.forEach { transaction ->
                Text(transaction.amount.toString())
            }
            Text("Loans")
            loanTransactions.forEach { transaction ->
                Text(transaction.amount.toString())
            }
            Button(onClick = { onAddTransaction(TransactionType.DEPOSIT) }) {
                Text("Add Savings Transaction")
            }
            Button(onClick = { onAddTransaction(TransactionType.LOAN) }) {
                Text("Add Loan Transaction")
            }
        }
    }
}