package com.preppal.navigation

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.preppal.navigation.ROUTE_LOGIN
import com.preppal.navigation.ROUTE_REGISTER
import com.preppal.navigation.ROUTE_START
import com.preppal.ui.theme.screens.classes.ReminderScreen
import com.preppal.ui.theme.screens.home.HomeScreen
import com.preppal.ui.theme.screens.login.LoginScreen
import com.preppal.ui.theme.screens.registration.RegisterScreen
import com.preppal.ui.theme.screens.splash.SplashScreen


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_HOME
) {

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(navController)
        }
        composable(ROUTE_REGISTER) {
            RegisterScreen(navController)
        }

        composable(ROUTE_HOME) {
            HomeScreen(navController)
        }

        composable(ROUTE_START) {
            SplashScreen(navController)
        }
        composable (ROUTE_CLASS){
            ReminderScreen (navController)
        }
    }
}

