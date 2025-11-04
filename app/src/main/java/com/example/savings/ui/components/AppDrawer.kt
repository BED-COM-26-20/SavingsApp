package com.example.savings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class DrawerItem(val icon: ImageVector, val label: String, val route: String)

@Composable
fun AppDrawer(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    onLogout: () -> Unit
) {
    val items = listOf(
        DrawerItem(Icons.Default.Home, "Home", "home"),
        DrawerItem(Icons.Default.Groups, "Members", "members"),
        DrawerItem(Icons.Default.SyncAlt, "Transactions", "transactions"),
        DrawerItem(Icons.Default.Assessment, "Reports", "reports"),
        DrawerItem(Icons.Default.Notifications, "Notifications", "notifications")
    )

    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Savings App", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))

            items.forEach { item ->
                NavigationDrawerItem(
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(item.label) },
                    selected = item.route == navController.currentBackStackEntryAsState().value?.destination?.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                label = { Text("Logout") },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    onLogout()
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
