package com.example.savings.ui.notifications

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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Savings
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

data class Notification(
    val id: Int,
    val title: String,
    val body: String,
    val category: NotificationCategory
)

sealed class NotificationCategory(val title: String, val icon: ImageVector) {
    object LoanRepayment : NotificationCategory("Loan Repayment Due", Icons.Default.Payment)
    object ContributionReminder : NotificationCategory("Contribution Reminder", Icons.Default.Event)
    object DepositConfirmation : NotificationCategory("Deposit Confirmation", Icons.Default.Savings)
    object Announcement : NotificationCategory("Announcement", Icons.Default.Campaign)
}

val sampleNotifications = listOf(
    Notification(1, "Junior", "Repayment of MWK 2,000 is due.", NotificationCategory.LoanRepayment),
    Notification(2, "Liness", "Thank you for your deposit of MWK 25,000.", NotificationCategory.DepositConfirmation),
    Notification(3, "Group Announcement", "Our next meeting will be held on the 25th.", NotificationCategory.Announcement)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
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
            LazyColumn(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sampleNotifications) { notification ->
                    NotificationItem(notification)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = onMarkAsRead, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Mark as Read")
                }
                Button(onClick = onDelete, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = notification.category.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column {
                Text(notification.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(notification.body, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
