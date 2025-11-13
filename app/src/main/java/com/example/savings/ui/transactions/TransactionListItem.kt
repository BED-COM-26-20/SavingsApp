package com.example.savings.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.savings.data.models.Transaction
import com.example.savings.data.models.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            Text(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(transaction.date)), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
        Text("MK${String.format("%,.2f", transaction.amount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = color)
    }
}
