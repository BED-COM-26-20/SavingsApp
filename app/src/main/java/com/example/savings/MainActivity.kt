package com.example.savings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.savings.data.FirebaseDataSource
import com.example.savings.data.SavingsDatabase
import com.example.savings.data.models.TransactionType
import com.example.savings.ui.auth.AuthRepository
import com.example.savings.ui.auth.AuthViewModel
import com.example.savings.ui.auth.AuthViewModelFactory
import com.example.savings.ui.auth.ForgotPasswordScreen
import com.example.savings.ui.auth.LoginScreen
import com.example.savings.ui.auth.RegistrationScreen
import com.example.savings.ui.groups.CreateGroupScreen
import com.example.savings.ui.groups.EditGroupScreen
import com.example.savings.ui.groups.GroupDetailsScreen
import com.example.savings.ui.groups.GroupViewModel
import com.example.savings.ui.groups.GroupViewModelFactory
import com.example.savings.ui.main.LandingScreen
import com.example.savings.ui.main.MainScreen
import com.example.savings.ui.members.AddMemberScreen
import com.example.savings.ui.members.MemberDetailsScreen
import com.example.savings.ui.members.MemberViewModel
import com.example.savings.ui.members.MemberViewModelFactory
import com.example.savings.ui.members.MembersScreen
import com.example.savings.ui.profile.ChangePasswordScreen
import com.example.savings.ui.profile.EditProfileScreen
import com.example.savings.ui.profile.NotificationsScreen
import com.example.savings.ui.profile.ProfileViewModel
import com.example.savings.ui.profile.ProfileViewModelFactory
import com.example.savings.ui.reports.ReportsScreen
import com.example.savings.ui.theme.SavingsTheme
import com.example.savings.ui.transactions.AddTransactionScreen
import com.example.savings.ui.transactions.TransactionViewModel
import com.example.savings.ui.transactions.TransactionViewModelFactory
import com.example.savings.ui.transactions.TransactionsScreen
import com.example.savings.ui.util.AppScreen
import com.example.savings.ui.util.UserRole

class MainActivity : ComponentActivity() {

    private val firebaseDataSource = FirebaseDataSource()
    private val authRepository = AuthRepository(firebaseDataSource)
    private val authViewModel: AuthViewModel by viewModels { AuthViewModelFactory(authRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = SavingsDatabase.getDatabase(this)
        val groupDao = database.groupDao()
        val groupRepository = CachingGroupRepository(groupDao, firebaseDataSource)

        val memberViewModel: MemberViewModel by viewModels { MemberViewModelFactory(firebaseDataSource) }
        val transactionViewModel: TransactionViewModel by viewModels { TransactionViewModelFactory(firebaseDataSource) }
        val groupViewModel: GroupViewModel by viewModels { GroupViewModelFactory(groupRepository) }
        val profileViewModel: ProfileViewModel by viewModels { ProfileViewModelFactory() }

        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            val authState by authViewModel.authState.collectAsState()

            SavingsTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()

                LaunchedEffect(authState) {
                    if (authState is com.example.savings.ui.auth.AuthState.SignedIn) {
                        navController.navigate(AppScreen.Home.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    } else if (authState is com.example.savings.ui.auth.AuthState.SignedOut) {
                        navController.navigate("login") {
                            popUpTo(AppScreen.Home.route) { inclusive = true }
                        }
                    }
                }

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
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = "landing", modifier = Modifier.padding(innerPadding)) {
                        composable("landing") {
                            LandingScreen(onGetStarted = { navController.navigate("login") })
                        }
                        composable("login") {
                            LoginScreen(
                                onLogin = { email, password -> authViewModel.signIn(email, password) },
                                onRegister = { navController.navigate("register") },
                                onForgotPassword = { navController.navigate("forgotPassword") }
                            )
                        }
                        composable("register") {
                            RegistrationScreen(onRegister = { email, password -> authViewModel.register(email, password) })
                        }
                        composable("forgotPassword") {
                            ForgotPasswordScreen(onNavigateBack = { navController.popBackStack() }, onResetPassword = {})
                        }
                        composable(AppScreen.Home.route) {
                            val role = (authState as? com.example.savings.ui.auth.AuthState.SignedIn)?.role ?: UserRole.MEMBER
                            MainScreen(
                                userRole = role,
                                navController = navController,
                                groupViewModel = groupViewModel,
                                memberViewModel = memberViewModel,
                                transactionViewModel = transactionViewModel
                            )
                        }
                        composable(AppScreen.Profile.route) {
                            EditProfileScreen(
                                onNavigateBack = { navController.navigate(AppScreen.Home.route) },
                                onToggleDarkMode = { isDarkMode = it },
                                isDarkMode = isDarkMode,
                                onLogout = { authViewModel.signOut() },
                                onChangePasswordClicked = { navController.navigate("changePassword") },
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
                            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId") ?: "-1"
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
                            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId") ?: "-1"
                            val group by groupViewModel.groups.collectAsState(initial = emptyList())
                            val role = (authState as? com.example.savings.ui.auth.AuthState.SignedIn)?.role ?: UserRole.MEMBER
                            GroupDetailsScreen(
                                group = group.find { it.id == groupId },
                                onNavigateBack = { navController.popBackStack() },
                                onMembersClicked = { navController.navigate("members/$groupId") },
                                onTransactionsClicked = { navController.navigate("transactions/$groupId") },
                                onReportsClicked = { navController.navigate("reports/$groupId") },
                                isAdmin = role == UserRole.ADMIN
                            )
                        }
                        composable(
                            route = "members/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId") ?: "-1"
                            val members by memberViewModel.getMembersForGroup(groupId).collectAsState(initial = emptyList())
                            val role = (authState as? com.example.savings.ui.auth.AuthState.SignedIn)?.role ?: UserRole.MEMBER
                            MembersScreen(
                                members = members,
                                onMemberClicked = { member -> navController.navigate("memberDetails/${member.groupId}/${member.id}") },
                                onAddMemberClicked = { navController.navigate("addMember/$groupId") },
                                onNavigateBack = { navController.popBackStack() },
                                isAdmin = role == UserRole.ADMIN
                            )
                        }
                        composable(
                            route = "transactions/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId") ?: "-1"
                            val transactions by transactionViewModel.getAllTransactionsForGroup(groupId).collectAsState(initial = emptyList())
                            TransactionsScreen(transactions = transactions, onNavigateBack = { navController.popBackStack() })
                        }
                        composable(
                            route = "reports/{groupId}",
                            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId") ?: "-1"
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
                            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId") ?: "-1"
                            AddMemberScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onSaveMember = { name, phone ->
                                    memberViewModel.addMember(name, phone, groupId)
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = "memberDetails/{groupId}/{memberId}",
                            arguments = listOf(
                                navArgument("groupId") { type = NavType.StringType },
                                navArgument("memberId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val memberId = backStackEntry.arguments?.getString("memberId") ?: "-1"
                            val groupId = backStackEntry.arguments?.getString("groupId") ?: "-1"
                            val member by memberViewModel.getMemberById(groupId, memberId).collectAsState(initial = null)

                            if (member != null) {
                                val memberTransactions by transactionViewModel.getTransactionsForMember(groupId, memberId).collectAsState(initial = emptyList())
                                val savingsTransactions = remember(memberTransactions) { memberTransactions.filter { it.type == TransactionType.DEPOSIT } }
                                val loanTransactions = remember(memberTransactions) { memberTransactions.filter { it.type == TransactionType.LOAN || it.type == TransactionType.LOAN_REPAYMENT } }

                                MemberDetailsScreen(
                                    member = member!!,
                                    savingsTransactions = savingsTransactions,
                                    loanTransactions = loanTransactions,
                                    onNavigateBack = { navController.popBackStack() },
                                    onAddTransaction = { transactionType ->
                                        navController.navigate("addTransaction/${transactionType.name}/${member!!.groupId}/$memberId")
                                    }
                                )
                            }
                        }
                        composable(
                            route = "addTransaction/{type}/{groupId}/{memberId}",
                            arguments = listOf(
                                navArgument("type") { type = NavType.StringType },
                                navArgument("groupId") { type = NavType.StringType },
                                navArgument("memberId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val memberId = backStackEntry.arguments?.getString("memberId")
                            val groupId = backStackEntry.arguments?.getString("groupId")
                            val typeString = backStackEntry.arguments?.getString("type")

                            val transactionType = try {
                                typeString?.let { TransactionType.valueOf(it) }
                            } catch (_: IllegalArgumentException) {
                                null
                            }
                            
                            if (memberId != null && transactionType != null && groupId != null) {
                                val member by memberViewModel.getMemberById(groupId, memberId).collectAsState(initial = null)

                                if (member != null) {
                                    AddTransactionScreen(
                                        memberName = member!!.name,
                                        transactionType = transactionType.name,
                                        onNavigateBack = { navController.popBackStack() },
                                        onSave = { amount, date, type ->
                                            transactionViewModel.addTransaction(memberId, amount, type, date, "Transaction for ${member!!.name}", groupId)
                                            navController.popBackStack()
                                        }
                                    )
                                }
                            }
                        }
                        composable("notifications") {
                            NotificationsScreen(onNavigateBack = { navController.popBackStack() }, onMarkAsRead = {}, onDelete = {})
                        }
                        composable("changePassword") {
                            ChangePasswordScreen(
                                onNavigateBack = { navController.popBackStack() },
                                profileViewModel = profileViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
