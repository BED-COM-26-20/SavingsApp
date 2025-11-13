@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.savings.ui.members.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.savings.data.models.Member
import com.example.savings.data.models.Transaction
import com.example.savings.data.models.TransactionType
import com.example.savings.ui.transactions.TransactionListItem

@Composable
fun MemberDetailsScreen(
    member: Member,
    savingsTransactions: List<Transaction>,
    loanTransactions: List<Transaction>,
    onNavigateBack: () -> Unit,
    onAddTransaction: (TransactionType) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Savings", "Loans")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(member.name) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(modifier = Modifier.fillMaxSize()) {
                ProfileHeader(member)
                Spacer(modifier = Modifier.height(24.dp))

                PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text = title) }
                        )
                    }
                }
                when (selectedTabIndex) {
                    0 -> SavingsTab(savingsTransactions)
                    1 -> LoansTab(loanTransactions)
                }
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(text = { Text("Add Deposit") }, onClick = { onAddTransaction(TransactionType.DEPOSIT); showMenu = false })
                DropdownMenuItem(text = { Text("Add Loan") }, onClick = { onAddTransaction(TransactionType.LOAN); showMenu = false })
                DropdownMenuItem(text = { Text("Add Repayment") }, onClick = { onAddTransaction(TransactionType.LOAN_REPAYMENT); showMenu = false })
            }
        }
    }
}

@Composable
fun SavingsTab(transactions: List<Transaction>) {
    val totalSavings = transactions.sumOf { it.amount }
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            FinancialSummaryCard("Total Savings", "MK${String.format("%,.2f", totalSavings)}", Icons.Default.ArrowUpward, MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(transactions) { transaction ->
            TransactionListItem(transaction)
        }
    }
}

@Composable
fun LoansTab(transactions: List<Transaction>) {
    val totalLoans = transactions.filter { it.type == TransactionType.LOAN }.sumOf { it.amount }
    val totalRepayments = transactions.filter { it.type == TransactionType.LOAN_REPAYMENT }.sumOf { it.amount }
    val outstandingLoan = totalLoans - totalRepayments

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            FinancialSummaryCard("Outstanding Loan", "MK${String.format("%,.2f", outstandingLoan)}", Icons.Default.ArrowDownward, MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(transactions) { transaction ->
            TransactionListItem(transaction)
        }
    }
}

@Composable
fun FinancialSummaryCard(title: String, amount: String, icon: ImageVector, color: Color) {
    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(amount, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun ProfileHeader(member: Member) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "Member", tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(60.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(member.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(member.phone, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
    }
}
