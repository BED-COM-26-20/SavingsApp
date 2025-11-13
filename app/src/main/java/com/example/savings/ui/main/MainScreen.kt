package com.example.savings.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.savings.data.models.UserRole
import com.example.savings.ui.group.GroupSelectionScreen
import com.example.savings.ui.group.GroupViewModel
import com.example.savings.ui.members.MemberViewModel
import com.example.savings.ui.transactions.TransactionViewModel

@Composable
fun MainScreen(
    userRole: UserRole,
    navController: NavController,
    groupViewModel: GroupViewModel,
    memberViewModel: MemberViewModel,
    transactionViewModel: TransactionViewModel
) {
    val groups by groupViewModel.groups.collectAsState(initial = emptyList())

    when (userRole) {
        UserRole.ADMIN -> {
            GroupSelectionScreen(
                groups = groups,
                onGroupSelected = { groupId ->
                    navController.navigate("groupDetails/$groupId")
                },
                onCreateGroup = { navController.navigate("createGroup") },
                onNotificationsClicked = { navController.navigate("notifications") },
                onEditGroup = { groupId -> navController.navigate("editGroup/$groupId") },
                isAdmin = true
            )
        }
        UserRole.MEMBER -> {
            GroupSelectionScreen(
                groups = groups, // In a real app, you would filter this to the member's groups
                onGroupSelected = { groupId ->
                    navController.navigate("groupDetails/$groupId")
                },
                onCreateGroup = {}, // No-op for members
                onNotificationsClicked = { navController.navigate("notifications") },
                onEditGroup = {}, // No-op for members
                isAdmin = false
            )
        }
    }
}
