package com.example.savings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.savings.data.SavingsDatabase
import com.example.savings.data.models.TransactionType
import com.example.savings.ui.auth.ForgotPasswordScreen
import com.example.savings.ui.auth.LoginScreen
import com.example.savings.ui.auth.RegistrationScreen
import com.example.savings.ui.group.CreateGroupScreen
import com.example.savings.ui.group.EditGroupScreen
import com.example.savings.ui.group.GroupDetailsScreen
import com.example.savings.ui.group.GroupSelectionScreen
import com.example.savings.ui.group.GroupViewModel
import com.example.savings.ui.group.GroupViewModelFactory
import com.example.savings.ui.landing.LandingScreen
import com.example.savings.ui.members.AddMemberScreen
import com.example.savings.ui.members.MemberViewModel
import com.example.savings.ui.members.MemberViewModelFactory
import com.example.savings.ui.members.MembersScreen
import com.example.savings.ui.members.details.MemberDetailsScreen
import com.example.savings.ui.notifications.NotificationsScreen
import com.example.savings.ui.profile.EditProfileScreen
import com.example.savings.ui.profile.ProfileViewModel
import com.example.savings.ui.profile.ProfileViewModelFactory
import com.example.savings.ui.reports.ReportsScreen
import com.example.savings.ui.theme.SavingsTheme
import com.example.savings.ui.transactions.AddTransactionScreen
import com.example.savings.ui.transactions.TransactionViewModel
import com.example.savings.ui.transactions.TransactionViewModelFactory
import com.example.savings.ui.transactions.TransactionsScreen

sealed class AppScreen(val route: String, val label: String, val icon: ImageVector) {
    object Home : AppScreen("home", "Home", Icons.Default.Home)
    object Profile : AppScreen("profile", "Profile", Icons.Default.Person)
}

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
        val profileViewModel: ProfileViewModel by viewModels { ProfileViewModelFactory() }

        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            SavingsTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        val showBottomBar = currentDestination?.route in listOf(AppScreen.Home.route, AppScreen.Profile.route)

                        if (showBottomBar) {
                            NavigationBar {
                                val items = listOf(AppScreen.Home, AppScreen.Profile)
                                items.forEach { screen ->
                                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { 
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
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
                    }
                ) {
                    innerPadding ->
                    NavHost(navController = navController, startDestination = "landing", modifier = Modifier.padding(innerPadding)) {
                        composable("landing") {
                            LandingScreen(onGetStarted = { navController.navigate("login") })
                        }
                        composable("login") {
                            LoginScreen(
                                onLogin = { navController.navigate(AppScreen.Home.route) { popUpTo("login") { inclusive = true } } },
                                onRegister = { navController.navigate("register") },
                                onForgotPassword = { navController.navigate("forgotPassword") }
                            )
                        }
                        composable("register") {
                            RegistrationScreen(
                                onRegister = { navController.navigate(AppScreen.Home.route) { popUpTo("login") { inclusive = true } } }
                            )
                        }
                        composable("forgotPassword") {
                            ForgotPasswordScreen(onNavigateBack = { navController.popBackStack() }, onResetPassword = {})
                        }
                        composable(AppScreen.Home.route) {
                            GroupSelectionScreen(
                                groups = groupViewModel.groups.collectAsState(initial = emptyList()).value,
                                onGroupSelected = { groupId ->
                                    navController.navigate("groupDetails/$groupId")
                                },
                                onCreateGroup = { navController.navigate("createGroup") },
                                onLogout = {
                                    groupViewModel.onLogout()
                                    memberViewModel.onLogout()
                                    transactionViewModel.onLogout()
                                    navController.navigate("login") {
                                        popUpTo(AppScreen.Home.route) { inclusive = true }
                                    }
                                },
                                onNotificationsClicked = { navController.navigate("notifications") },
                                onEditGroup = { groupId -> navController.navigate("editGroup/$groupId") }
                            )
                        }
                        composable(AppScreen.Profile.route) {
                            EditProfileScreen(
                                onNavigateBack = { navController.navigate(AppScreen.Home.route) },
                                onToggleDarkMode = { isDarkMode = it },
                                isDarkMode = isDarkMode,
                                onLogout = {
                                    groupViewModel.onLogout()
                                    memberViewModel.onLogout()
                                    transactionViewModel.onLogout()
                                    navController.navigate("login") {
                                        popUpTo(AppScreen.Profile.route) { inclusive = true }
                                    }
                                },
                                profileViewModel = profileViewModel
                            )
                        }
                        composable("createGroup") {
                            CreateGroupScreen(onCreateGroup = { groupName ->
                                groupViewModel.createGroup(groupName)
                                navController.popBackStack()
                            }, onNavigateBack = { navController.popBackStack() })
                        }
                        composable(
                            route = "editGroup/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getInt("groupId") ?: -1
                            val group = groupViewModel.groups.collectAsState(initial = emptyList()).value.find { it.id == groupId }

                            if (group != null) {
                                EditGroupScreen(
                                    groupName = group.name,
                                    onNavigateBack = { navController.popBackStack() },
                                    onSave = { newName ->
                                        groupViewModel.updateGroup(group.copy(name = newName))
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                        composable(
                            route = "groupDetails/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getInt("groupId") ?: -1
                            val group by groupViewModel.groups.collectAsState(initial = emptyList())
                            GroupDetailsScreen(
                                group = group.find { it.id == groupId },
                                onNavigateBack = { navController.popBackStack() },
                                onMembersClicked = { navController.navigate("members/$groupId") },
                                onTransactionsClicked = { navController.navigate("transactions/$groupId") },
                                onReportsClicked = { navController.navigate("reports/$groupId") }
                            )
                        }
                        composable(
                            route = "members/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getInt("groupId") ?: -1
                            val members by memberViewModel.getMembersForGroup(groupId).collectAsState(initial = emptyList())
                            MembersScreen(
                                members = members,
                                onMemberClicked = { member -> navController.navigate("memberDetails/${member.id}") },
                                onAddMemberClicked = { navController.navigate("addMember/$groupId") },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = "transactions/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getInt("groupId") ?: -1
                            val transactions by transactionViewModel.getAllTransactionsForGroup(groupId).collectAsState(initial = emptyList())
                            TransactionsScreen(transactions = transactions, onNavigateBack = { navController.popBackStack() })
                        }
                        composable(
                            route = "reports/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getInt("groupId") ?: -1
                            val transactions by transactionViewModel.getAllTransactionsForGroup(groupId).collectAsState(initial = emptyList())
                            val members by memberViewModel.getMembersForGroup(groupId).collectAsState(initial = emptyList())
                            ReportsScreen(
                                transactions = transactions,
                                members = members,
                                onNavigateBack = { navController.popBackStack() },
                                onExport = { /* TODO */ },
                                onShare = { /* TODO */ }
                            )
                        }
                        composable(
                            route = "addMember/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getInt("groupId") ?: -1
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
                                    onNavigateBack = { navController.popBackStack() },
                                    onAddTransaction = { transactionType ->
                                        navController.navigate("addTransaction/$transactionType/$memberId")
                                    }
                                )
                            }
                        }
                        composable(
                            route = "addTransaction/{type}/{memberId}",
                            arguments = listOf(
                                navArgument("type") { type = NavType.StringType },
                                navArgument("memberId") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val memberId = backStackEntry.arguments?.getInt("memberId")
                            val typeString = backStackEntry.arguments?.getString("type")

                            val transactionType = try {
                                typeString?.let { TransactionType.valueOf(it) }
                            } catch (_: IllegalArgumentException) {
                                null
                            }
                            val member by memberViewModel.getMemberById(memberId!!).collectAsState(initial = null)

                             if (memberId != null && transactionType != null && member != null) {
                                AddTransactionScreen(
                                    memberName = member!!.name,
                                    transactionType = transactionType.name,
                                    onNavigateBack = { navController.popBackStack() },
                                    onSave = { amount, date, type ->
                                        transactionViewModel.addTransaction(memberId, amount, type, date, "Transaction for ${member!!.name}")
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                        composable("notifications") {
                            NotificationsScreen(onNavigateBack = { navController.popBackStack() }, onMarkAsRead = {}, onDelete = {})
                        }
                    }
                }
            }
        }
    }
}
