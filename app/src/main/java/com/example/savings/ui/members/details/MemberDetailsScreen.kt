package com.example.savings.ui.members.details

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.savings.data.models.Member

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailsScreen(
    member: Member,
    onNavigateBack: () -> Unit,
    onAddDeposit: () -> Unit,
    onAddLoan: () -> Unit,
    onAddPayment: () -> Unit
) {
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
        }
    ) {
        Column(modifier = Modifier.padding(it).fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            MemberSummaryCard(member)
            Spacer(modifier = Modifier.height(16.dp))
            FinancialDetailsCard(member)
            Spacer(modifier = Modifier.height(24.dp))
            ActionsCard(onAddDeposit, onAddLoan, onAddPayment)
        }
    }
}

@Composable
fun MemberSummaryCard(member: Member) {
    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column {
                Text(member.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(member.phone, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun FinancialDetailsCard(member: Member) {
    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoRow(icon = Icons.Default.AccountBalanceWallet, label = "Total Savings", value = "MK${member.totalSavings}")
            InfoRow(icon = Icons.Default.CreditCard, label = "Total Loan", value = "MK${member.totalLoan}")
        }
    }
}

@Composable
fun ActionsCard(onAddDeposit: () -> Unit, onAddLoan: () -> Unit, onAddPayment: () -> Unit) {
    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            ActionButton(icon = Icons.Default.AddCard, text = "Add Deposit", onClick = onAddDeposit)
            Spacer(modifier = Modifier.height(8.dp))
            ActionButton(icon = Icons.Default.CreditCard, text = "Add Loan", onClick = onAddLoan)
            Spacer(modifier = Modifier.height(8.dp))
            ActionButton(icon = Icons.Default.Payments, text = "Add Payment", onClick = onAddPayment)
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ActionButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        Text(text)
    }
}
