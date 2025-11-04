package com.example.savings.ui.members

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.savings.data.models.Member

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(members: List<Member>, onMemberClicked: (Member) -> Unit, onAddMemberClicked: () -> Unit, onNavigateBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredMembers = members.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Members") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMemberClicked) {
                Icon(Icons.Default.Add, contentDescription = "Add Member")
            }
        }
    ) {
        Column(modifier = Modifier.padding(it).fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search members...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(top = 16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredMembers) { member ->
                    MemberListItem(member = member, onClick = { onMemberClicked(member) })
                }
            }
        }
    }
}

@Composable
fun MemberListItem(member: Member, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = "Member", modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column {
                Text(member.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Savings: MK${member.totalSavings}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
