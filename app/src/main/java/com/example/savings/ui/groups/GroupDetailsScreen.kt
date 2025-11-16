package com.example.savings.ui.groups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.savings.data.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    group: Group?,
    onNavigateBack: () -> Unit,
    onMembersClicked: () -> Unit,
    onTransactionsClicked: () -> Unit,
    onReportsClicked: () -> Unit,
    isAdmin: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(group?.name ?: "Group Details") })
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Button(onClick = onMembersClicked) {
                Text("Members")
            }
            Button(onClick = onTransactionsClicked) {
                Text("Transactions")
            }
            Button(onClick = onReportsClicked) {
                Text("Reports")
            }
        }
    }
}