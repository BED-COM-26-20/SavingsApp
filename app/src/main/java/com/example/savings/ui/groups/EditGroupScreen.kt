package com.example.savings.ui.groups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.savings.data.models.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroupScreen(
    group: Group,
    onSave: (Group) -> Unit,
    onNavigateBack: () -> Unit
) {
    var groupName by remember { mutableStateOf(group.name) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Group") })
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") }
            )
            Button(onClick = { onSave(group.copy(name = groupName)) }) {
                Text("Save")
            }
        }
    }
}