package com.preppal.ui.theme.navigation

sealed class Screen(val route: String) {
    object Profile : Screen("profile")
    object Login : Screen("login")
}