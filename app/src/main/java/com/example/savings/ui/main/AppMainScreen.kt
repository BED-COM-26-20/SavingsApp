package com.example.savings.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.savings.ui.group.GroupSelectionScreen
import com.example.savings.ui.group.GroupViewModel
import com.example.savings.ui.profile.EditProfileScreen
import com.example.savings.ui.profile.ProfileViewModel

sealed class AppScreen(val route: String, val label: String, val icon: ImageVector) {
    object Groups : AppScreen("groups", "Groups", Icons.Default.Groups)
    object Profile : AppScreen("profile", "Profile", Icons.Default.Person)
}

@Composable
fun AppMainScreen(
    navController: NavController,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    groupViewModel: GroupViewModel,
    profileViewModel: ProfileViewModel
) {
    val appNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by appNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(AppScreen.Groups, AppScreen.Profile)
                items.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = { 
                            appNavController.navigate(screen.route) {
                                popUpTo(appNavController.graph.findStartDestination().id) {
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
        NavHost(navController = appNavController, startDestination = AppScreen.Groups.route, modifier = Modifier.padding(innerPadding)) {
            composable(AppScreen.Groups.route) {
                GroupSelectionScreen(
                    groups = groupViewModel.groups.collectAsState(initial = emptyList()).value,
                    onGroupSelected = { groupId -> navController.navigate("groupDetails/$groupId") },
                    onCreateGroup = { navController.navigate("createGroup") },
                    onLogout = {
                        // TODO: Clear data on logout
                        navController.navigate("login") {
                            popUpTo("appMain") { inclusive = true }
                        }
                     },
                    onNotificationsClicked = { navController.navigate("notifications") } // Add this line
                )
            }
            composable(AppScreen.Profile.route) {
                EditProfileScreen(
                    onNavigateBack = { appNavController.popBackStack() },
                    onToggleDarkMode = onToggleDarkMode,
                    isDarkMode = isDarkMode,
                    onLogout = {
                        // TODO: Clear data on logout
                        navController.navigate("login") {
                            popUpTo("appMain") { inclusive = true }
                        }
                    },
                    profileViewModel = profileViewModel
                )
            }
        }
    }
}
