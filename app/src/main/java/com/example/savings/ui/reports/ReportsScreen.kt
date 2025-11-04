package com.example.savings.ui.reports

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.savings.data.models.Transaction
import com.example.savings.data.models.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    transactions: List<Transaction>,
    onNavigateBack: () -> Unit,
    onExport: () -> Unit,
    onShare: () -> Unit
) {
    val totalSavings = transactions.filter { it.type == TransactionType.DEPOSIT }.sumOf { it.amount }
    val totalLoans = transactions.filter { it.type == TransactionType.LOAN }.sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports") },
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
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Financial Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    PieChart(totalSavings, totalLoans)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Total Savings: MWK $totalSavings", style = MaterialTheme.typography.bodyLarge)
                    Text("Total Loans: MWK $totalLoans", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Placeholder for Top Savers
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Top Savers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    TopSaverItem(rank = 1, name = "Mary Banda", amount = "MWK 50,000")
                    TopSaverItem(rank = 2, name = "John Phiri", amount = "MWK 45,000")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = onExport) { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Export") 
                    }
                }
                Button(onClick = onShare) { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Share") 
                    }
                }
            }
        }
    }
}

@Composable
fun PieChart(savings: Double, loans: Double, modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Box(modifier = modifier.size(150.dp), contentAlignment = Alignment.Center) {
        if (savings + loans == 0.0) {
            Text("No data available")
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val total = (savings + loans).toFloat()
                val savingsAngle = (savings.toFloat() / total) * 360f
                val loansAngle = (loans.toFloat() / total) * 360f

                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = savingsAngle,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )
                drawArc(
                    color = secondaryColor,
                    startAngle = -90f + savingsAngle,
                    sweepAngle = loansAngle,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )
            }
        }
    }
}

@Composable
fun TopSaverItem(rank: Int, name: String, amount: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Star, contentDescription = "Top Saver", tint = if (rank == 1) Color(0xFFFFD700) else Color.Gray)
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Text("$rank. $name", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Text(amount, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
