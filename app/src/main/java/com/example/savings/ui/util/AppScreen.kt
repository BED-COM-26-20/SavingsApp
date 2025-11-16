package com.example.savings.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreen(val route: String, val label: String, val icon: ImageVector) {
    object Home : AppScreen("home", "Home", Icons.Default.Home)
    object Profile : AppScreen("profile", "Profile", Icons.Default.Person)
}
