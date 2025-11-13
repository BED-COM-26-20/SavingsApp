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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import com.example.savings.data.models.Member
import com.example.savings.data.models.Transaction
import com.example.savings.data.models.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailsScreen(
    member: Member,
    savingsTransactions: List<Transaction>,
    loanTransactions: List<Transaction>,
    onNavigateBack: () -> Unit,
    onAddTransaction: (TransactionType) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                item {
                    ProfileHeader(member)
                    Spacer(modifier = Modifier.height(24.dp))
                    FinancialSummary(savingsTransactions, loanTransactions)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    TransactionHistory(title = "Savings History", transactions = savingsTransactions)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    TransactionHistory(title = "Loan History", transactions = loanTransactions)
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
fun ProfileHeader(member: Member) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
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
        Text(member.phone, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
    }
}

@Composable
fun FinancialSummary(savings: List<Transaction>, loans: List<Transaction>) {
    val totalSavings = savings.sumOf { it.amount }
    val totalLoans = loans.filter { it.type == TransactionType.LOAN }.sumOf { it.amount }

    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            StatItem("Savings", "MK${String.format("%,.2f", totalSavings)}", Icons.Default.ArrowUpward, MaterialTheme.colorScheme.primary)
            Divider(modifier = Modifier.height(60.dp).padding(horizontal = 8.dp).background(Color.Gray))
            StatItem("Loans", "MK${String.format("%,.2f", totalLoans)}", Icons.Default.ArrowDownward, MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun StatItem(title: String, amount: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(amount, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun TransactionHistory(title: String, transactions: List<Transaction>) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (transactions.isEmpty()) {
            Text("No transactions yet.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
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

@Composable
fun TransactionListItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (icon, color) = when (transaction.type) {
            TransactionType.DEPOSIT, TransactionType.LOAN_REPAYMENT -> Pair(Icons.Default.ArrowUpward, MaterialTheme.colorScheme.primary)
            TransactionType.LOAN -> Pair(Icons.Default.ArrowDownward, MaterialTheme.colorScheme.error)
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.padding(horizontal = 16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.description, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(transaction.date)), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Text("MK${String.format("%,.2f", transaction.amount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = color)
    }
}
