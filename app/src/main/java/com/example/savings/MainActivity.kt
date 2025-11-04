package com.example.savings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.savings.data.SavingsDatabase
import com.example.savings.data.models.TransactionType
import com.example.savings.ui.auth.LoginScreen
import com.example.savings.ui.auth.RegistrationScreen
import com.example.savings.ui.group.CreateGroupScreen
import com.example.savings.ui.group.GroupSelectionScreen
import com.example.savings.ui.group.GroupViewModel
import com.example.savings.ui.group.GroupViewModelFactory
import com.example.savings.ui.home.HomeScreen
import com.example.savings.ui.members.AddMemberScreen
import com.example.savings.ui.members.MemberViewModel
import com.example.savings.ui.members.MemberViewModelFactory
import com.example.savings.ui.members.MembersScreen
import com.example.savings.ui.members.details.MemberDetailsScreen
import com.example.savings.ui.notifications.NotificationsScreen
import com.example.savings.ui.reports.ReportsScreen
import com.example.savings.ui.theme.SavingsTheme
import com.example.savings.ui.transactions.AddTransactionScreen
import com.example.savings.ui.transactions.TransactionViewModel
import com.example.savings.ui.transactions.TransactionViewModelFactory
import com.example.savings.ui.transactions.TransactionsScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = SavingsDatabase.getDatabase(this)
        val memberDao = database.memberDao()
        val transactionDao = database.transactionDao()
        val groupDao = database.groupDao()
        val memberViewModel: MemberViewModel by viewModels { MemberViewModelFactory(memberDao) }
        val transactionViewModel: TransactionViewModel by viewModels { TransactionViewModelFactory(transactionDao) }
        val groupViewModel: GroupViewModel by viewModels { GroupViewModelFactory(groupDao) }

        setContent {
            SavingsTheme {
                Scaffold {
                    innerPadding ->
                    val navController = rememberNavController()
                    var selectedGroupId by remember { mutableStateOf<Int?>(null) }
                    val groups by groupViewModel.groups.collectAsState()

                    NavHost(navController = navController, startDestination = "login", modifier = Modifier.padding(innerPadding)) {
                        composable("login") {
                            LoginScreen(
                                onLogin = { navController.navigate("groupSelection") { popUpTo("login") { inclusive = true } } },
                                onRegister = { navController.navigate("register") }
                            )
                        }
                        composable("register") {
                            RegistrationScreen(
                                onRegister = { navController.navigate("groupSelection") { popUpTo("login") { inclusive = true } } }
                            )
                        }
                        composable("groupSelection") {
                            GroupSelectionScreen(
                                groups = groups,
                                onGroupSelected = { groupId ->
                                    selectedGroupId = groupId
                                    navController.navigate("home")
                                },
                                onCreateGroup = { navController.navigate("createGroup") },
                                onLogout = { navController.navigate("login") { popUpTo("groupSelection") { inclusive = true } } }
                            )
                        }
                        composable("createGroup") {
                            CreateGroupScreen(onCreateGroup = { groupName ->
                                groupViewModel.createGroup(groupName)
                                navController.popBackStack()
                            }, onNavigateBack = { navController.popBackStack() })
                        }
                        composable("home") {
                            val groupId = selectedGroupId ?: return@composable
                            val members by memberViewModel.getMembersForGroup(groupId).collectAsState(initial = emptyList())
                            val allTransactions by transactionViewModel.getAllTransactionsForGroup(groupId).collectAsState(initial = emptyList())
                            val group = groups.find { it.id == groupId }

                            val totalSavings = allTransactions.filter { it.type == TransactionType.DEPOSIT }.sumOf { it.amount }
                            val totalLoans = allTransactions.filter { it.type == TransactionType.LOAN }.sumOf { it.amount }

                            if (group != null) {
                                HomeScreen(
                                    group = group.copy(
                                        totalSavings = totalSavings,
                                        totalLoans = totalLoans,
                                        numberOfMembers = members.size
                                    ),
                                    onMembersClicked = { navController.navigate("members") },
                                    onTransactionsClicked = { navController.navigate("transactions") },
                                    onReportsClicked = { navController.navigate("reports") },
                                    onNotificationsClicked = { navController.navigate("notifications") },
                                    onLogout = {
                                        navController.navigate("login") {
                                            popUpTo("home") {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    onNavigateBack = TODO()
                                )
                            }
                        }
                        composable("members") {
                            val groupId = selectedGroupId ?: return@composable
                            val members by memberViewModel.getMembersForGroup(groupId).collectAsState(initial = emptyList())

                            MembersScreen(
                                members = members,
                                onMemberClicked = { member -> navController.navigate("memberDetails/${member.id}") },
                                onAddMemberClicked = { navController.navigate("addMember") },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("addMember") {
                            val groupId = selectedGroupId ?: return@composable
                            AddMemberScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onSaveMember = { name, phone ->
                                    memberViewModel.addMember(name, phone, groupId)
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = "memberDetails/{memberId}",
                            arguments = listOf(navArgument("memberId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val groupId = selectedGroupId ?: return@composable
                            val memberId = backStackEntry.arguments?.getInt("memberId") ?: -1
                            val members by memberViewModel.getMembersForGroup(groupId).collectAsState(initial = emptyList())
                            val member = members.find { it.id == memberId }

                            if (member != null) {
                                val memberTransactions by transactionViewModel.getTransactionsForMember(memberId).collectAsState(initial = emptyList())
                                val memberTotalSavings = memberTransactions.filter { it.type == TransactionType.DEPOSIT }.sumOf { it.amount }
                                val memberTotalLoans = memberTransactions.filter { it.type == TransactionType.LOAN }.sumOf { it.amount }

                                MemberDetailsScreen(
                                    member = member.copy(totalSavings = memberTotalSavings, totalLoan = memberTotalLoans),
                                    onNavigateBack = { navController.popBackStack() },
                                    onAddDeposit = { navController.navigate("addTransaction/DEPOSIT/${member.id}") },
                                    onAddLoan = { navController.navigate("addTransaction/LOAN/${member.id}") },
                                    onAddPayment = { navController.navigate("addTransaction/LOAN_REPAYMENT/${member.id}") }
                                )
                            }
                        }
                        composable(
                            route = "addTransaction/{type}/{memberId}",
                            arguments = listOf(
                                navArgument("type") { type = NavType.StringType },
                                navArgument("memberId") { type = NavType.IntType },
                            )
                        ) { backStackEntry ->
                            val groupId = selectedGroupId ?: return@composable
                            val memberId = backStackEntry.arguments?.getInt("memberId")
                            val members by memberViewModel.getMembersForGroup(groupId).collectAsState(initial = emptyList())
                            val typeString = backStackEntry.arguments?.getString("type")

                            val transactionType = try {
                                typeString?.let { TransactionType.valueOf(it) }
                            } catch (_: IllegalArgumentException) {
                                null
                            }
                            val member = members.find { it.id == memberId }

                            if (member != null && transactionType != null && memberId != null) {
                                AddTransactionScreen(
                                    memberName = member.name,
                                    transactionType = transactionType.name,
                                    onNavigateBack = { navController.popBackStack() },
                                    onSave = { amount, date, type ->
                                        transactionViewModel.addTransaction(memberId, amount, type, date, "Transaction for ${member.name}")
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                        composable("transactions") {
                            val groupId = selectedGroupId ?: return@composable
                            val allTransactions by transactionViewModel.getAllTransactionsForGroup(groupId).collectAsState(initial = emptyList())
                            TransactionsScreen(transactions = allTransactions, onNavigateBack = { navController.popBackStack() })
                        }
                        composable("reports") {
                            val groupId = selectedGroupId ?: return@composable
                            val allTransactions by transactionViewModel.getAllTransactionsForGroup(groupId).collectAsState(initial = emptyList())
                            ReportsScreen(
                                transactions = allTransactions,
                                onNavigateBack = { navController.popBackStack() },
                                onExport = { /* Handle Export */ },
                                onShare = { /* Handle Share */ }
                            )
                        }
                        composable("notifications") {
                            NotificationsScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onMarkAsRead = { /* Handle Mark As Read */ },
                                onDelete = { /* Handle Delete */ }
                            )
                        }
                    }
                }
            }
        }
    }
}
