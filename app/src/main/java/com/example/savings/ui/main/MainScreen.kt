package com.example.savings.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.savings.data.models.TransactionType
import com.example.savings.ui.group.GroupViewModel
import com.example.savings.ui.home.HomeScreen
import com.example.savings.ui.members.AddMemberScreen
import com.example.savings.ui.members.MemberViewModel
import com.example.savings.ui.members.MembersScreen
import com.example.savings.ui.members.details.MemberDetailsScreen
import com.example.savings.ui.notifications.NotificationsScreen
import com.example.savings.ui.profile.EditProfileScreen
import com.example.savings.ui.profile.ProfileViewModel
import com.example.savings.ui.reports.ReportsScreen
import com.example.savings.ui.transactions.AddTransactionScreen
import com.example.savings.ui.transactions.TransactionViewModel
import com.example.savings.ui.transactions.TransactionsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Members : Screen("members", "Members", Icons.Default.Groups)
    object Transactions : Screen("transactions", "Transactions", Icons.Default.SyncAlt)
    object Reports : Screen("reports", "Reports", Icons.Default.Assessment)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Notifications : Screen("notifications", "Notifications", Icons.Default.Notifications)
}

@Composable
fun MainScreen(
    navController: NavController,
    groupId: Int,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    groupViewModel: GroupViewModel,
    memberViewModel: MemberViewModel,
    transactionViewModel: TransactionViewModel,
    profileViewModel: ProfileViewModel
) {
    val mainNavController = rememberNavController()
    val group = groupViewModel.groups.collectAsState(initial = emptyList()).value.find { it.id == groupId }
    val members by memberViewModel.getMembersForGroup(groupId).collectAsState(initial = emptyList())
    val allTransactions by transactionViewModel.getAllTransactionsForGroup(groupId).collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(Screen.Home, Screen.Members, Screen.Transactions, Screen.Reports, Screen.Profile)
                items.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = { 
                            mainNavController.navigate(screen.route) {
                                popUpTo(mainNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = mainNavController, startDestination = Screen.Home.route, modifier = Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) {
                if (group != null) {
                    val memberCount by memberViewModel.getMemberCountForGroup(groupId).collectAsState(initial = 0)
                    val totalSavings by transactionViewModel.getGroupTotalSavings(groupId).collectAsState(initial = 0.0)
                    val totalLoans by transactionViewModel.getGroupTotalLoans(groupId).collectAsState(initial = 0.0)

                    HomeScreen(
                        group = group.copy(
                            totalSavings = totalSavings ?: 0.0,
                            totalLoans = totalLoans ?: 0.0,
                            numberOfMembers = memberCount
                        ),
                        onNotificationsClicked = { mainNavController.navigate(Screen.Notifications.route) },
                        onNavigateBack = { navController.popBackStack() }
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading...")
                    }
                }
            }
            composable(Screen.Members.route) {
                MembersScreen(
                    members = members, 
                    onMemberClicked = { member -> mainNavController.navigate("memberDetails/${member.id}") }, 
                    onAddMemberClicked = { mainNavController.navigate("addMember") }, 
                    onNavigateBack = { navController.popBackStack() })
            }
            composable(Screen.Transactions.route) {
                TransactionsScreen(transactions = allTransactions, onNavigateBack = { mainNavController.popBackStack() })
            }
            composable(Screen.Reports.route) {
                ReportsScreen(
                    transactions = allTransactions, 
                    members = members,
                    onNavigateBack = { mainNavController.popBackStack() }, 
                    onExport = { /* TODO */ }, 
                    onShare = { /* TODO */ })
            }
            composable(Screen.Profile.route) {
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onToggleDarkMode = onToggleDarkMode,
                    isDarkMode = isDarkMode,
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("main/$groupId") { inclusive = true }
                        }
                    },
                    profileViewModel = profileViewModel
                )
            }
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    onNavigateBack = { mainNavController.popBackStack() },
                    onMarkAsRead = { /* TODO */ },
                    onDelete = { /* TODO */ }
                )
            }
            composable("addMember") {
                AddMemberScreen(
                    onNavigateBack = { mainNavController.popBackStack() },
                    onSaveMember = { name, phone ->
                        memberViewModel.addMember(name, phone, groupId)
                        mainNavController.popBackStack()
                    }
                )
            }
            composable(
                route = "memberDetails/{memberId}",
                arguments = listOf(navArgument("memberId") { type = NavType.IntType })
            ) { backStackEntry ->
                val memberId = backStackEntry.arguments?.getInt("memberId") ?: -1
                val member by memberViewModel.getMemberById(memberId).collectAsState(initial = null)

                if (member != null) {
                    val memberTransactions by transactionViewModel.getTransactionsForMember(memberId).collectAsState(initial = emptyList())
                    val savingsTransactions = remember(memberTransactions) { memberTransactions.filter { it.type == TransactionType.DEPOSIT } }
                    val loanTransactions = remember(memberTransactions) { memberTransactions.filter { it.type == TransactionType.LOAN || it.type == TransactionType.LOAN_REPAYMENT } }

                    MemberDetailsScreen(
                        member = member!!,
                        savingsTransactions = savingsTransactions,
                        loanTransactions = loanTransactions,
                        onNavigateBack = { mainNavController.popBackStack() },
                        onAddTransaction = { transactionType ->
                            mainNavController.navigate("addTransaction/$transactionType/${member!!.id}")
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading member details...")
                    }
                }
            }
            composable(
                route = "addTransaction/{type}/{memberId}",
                arguments = listOf(
                    navArgument("type") { type = NavType.StringType },
                    navArgument("memberId") { type = NavType.IntType },
                )
            ) { backStackEntry ->
                val memberId = backStackEntry.arguments?.getInt("memberId")
                val typeString = backStackEntry.arguments?.getString("type")

                val transactionType = try {
                    typeString?.let { TransactionType.valueOf(it) }
                } catch (_: IllegalArgumentException) {
                    null
                }

                if (memberId != null && transactionType != null) {
                   val member by memberViewModel.getMemberById(memberId).collectAsState(initial = null)
                    if (member != null) {
                        AddTransactionScreen(
                            memberName = member!!.name,
                            transactionType = transactionType.name,
                            onNavigateBack = { mainNavController.popBackStack() },
                            onSave = { amount, date, type ->
                                transactionViewModel.addTransaction(memberId, amount, type, date, "Transaction for ${member!!.name}")
                                mainNavController.popBackStack()
                            }
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading...")
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading...")
                    }
                }
            }
        }
    }
}
