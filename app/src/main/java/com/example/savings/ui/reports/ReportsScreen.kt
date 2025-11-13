package com.example.savings.ui.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.savings.data.models.Member
import com.example.savings.data.models.Transaction
import com.example.savings.data.models.TransactionType
import com.example.savings.ui.transactions.TransactionItem

data class TopSaver(val rank: Int, val name: String, val amount: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    transactions: List<Transaction>,
    members: List<Member>,
    onNavigateBack: () -> Unit,
    onExport: () -> Unit,
    onShare: () -> Unit
) {
    val totalSavings = transactions.filter { it.type == TransactionType.DEPOSIT }.sumOf { it.amount }
    val totalLoans = transactions.filter { it.type == TransactionType.LOAN }.sumOf { it.amount }

    val topSavers = remember(transactions, members) {
        members
            .map { member ->
                val memberSavings = transactions
                    .filter { it.memberId == member.id && it.type == TransactionType.DEPOSIT }
                    .sumOf { it.amount }
                member.name to memberSavings
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(3)
            .mapIndexed { index, pair ->
                TopSaver(rank = index + 1, name = pair.first, amount = pair.second)
            }
    }

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
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onExport,
                    modifier = Modifier.weight(1f)
                ) { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Export") 
                    }
                }
                Button(
                    onClick = onShare,
                    modifier = Modifier.weight(1f)
                ) { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Share") 
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Financial Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        BoxWithConstraints {
                            val isSmallScreen = maxWidth < 600.dp
                            if (isSmallScreen) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    PieChart(totalSavings, totalLoans, modifier = Modifier.size(150.dp))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    PieChartLegend(
                                        savings = totalSavings,
                                        loans = totalLoans,
                                        savingsColor = MaterialTheme.colorScheme.primary,
                                        loansColor = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    PieChart(totalSavings, totalLoans, modifier = Modifier.size(200.dp))
                                    Spacer(modifier = Modifier.width(32.dp))
                                    PieChartLegend(
                                        savings = totalSavings,
                                        loans = totalLoans,
                                        savingsColor = MaterialTheme.colorScheme.primary,
                                        loansColor = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Top Savers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (topSavers.isEmpty()) {
                            Text("No savings data available to rank top savers.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        } else {
                            topSavers.forEach { saver ->
                                TopSaverItem(
                                    rank = saver.rank,
                                    name = saver.name,
                                    amount = "MWK ${String.format("%,.2f", saver.amount)}"
                                )
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("All Transactions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun PieChart(savings: Double, loans: Double, modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
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
fun PieChartLegend(savings: Double, loans: Double, savingsColor: Color, loansColor: Color) {
    Column {
        LegendItem(color = savingsColor, text = "Savings - MWK ${String.format("%,.2f", savings)}")
        Spacer(modifier = Modifier.height(8.dp))
        LegendItem(color = loansColor, text = "Loans - MWK ${String.format("%,.2f", loans)}")
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(16.dp).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge)
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
