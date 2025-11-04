package com.example.savings.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Groups
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
import com.example.savings.data.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    group: Group,
    onMembersClicked: () -> Unit,
    onTransactionsClicked: () -> Unit,
    onReportsClicked: () -> Unit,
    onNotificationsClicked: () -> Unit,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val dashboardItems = listOf(
        DashboardItemData(Icons.Default.Groups, "Members", onMembersClicked),
        DashboardItemData(Icons.Default.SyncAlt, "Transactions", onTransactionsClicked),
        DashboardItemData(Icons.Default.Assessment, "Reports", onReportsClicked),
        DashboardItemData(Icons.Default.Notifications, "Notifications", onNotificationsClicked)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group.name) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) {
        Column(Modifier.padding(it).background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            SummaryCard(group)
            Spacer(modifier = Modifier.height(24.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dashboardItems) { item ->
                    DashboardItem(item.icon, item.label, item.onClick)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(group: Group) {
    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Group Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow(Icons.Outlined.AccountBalanceWallet, "Total Savings", "MK${group.totalSavings}")
            InfoRow(Icons.Outlined.ArrowUpward, "Total Loans", "MK${group.totalLoans}")
            InfoRow(Icons.Outlined.Groups, "Members", "${group.numberOfMembers}")
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun DashboardItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

data class DashboardItemData(val icon: ImageVector, val label: String, val onClick: () -> Unit)
