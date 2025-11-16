package com.example.savings.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.savings.data.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelectionScreen(
    groups: List<Group>,
    onGroupSelected: (String) -> Unit,
    onCreateGroup: () -> Unit,
    onNotificationsClicked: () -> Unit,
    onEditGroup: (String) -> Unit,
    isAdmin: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isAdmin) "Admin Dashboard" else "Your Groups") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onNotificationsClicked) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = onCreateGroup,
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Group", tint = MaterialTheme.colorScheme.onTertiary)
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            if (groups.isEmpty()) {
                EmptyState(isAdmin)
            } else {
                val headerText = if (isAdmin) "Select a group to manage:" else "Select a group to view:"
                Text(headerText, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(groups) { group ->
                        GroupItem(group = group, onClick = { onGroupSelected(group.id) }, onEdit = { onEditGroup(group.id) }, isAdmin = isAdmin)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(isAdmin: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.GroupWork,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        val title = if (isAdmin) "Welcome, Admin!" else "Welcome!"
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        val message = if (isAdmin) "Create a new savings group to get started." else "You are not yet part of any savings group."
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun GroupItem(group: Group, onClick: () -> Unit, onEdit: () -> Unit, isAdmin: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.GroupWork,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(horizontal = 16.dp))
            Text(group.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            if (isAdmin) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Group", tint = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}
