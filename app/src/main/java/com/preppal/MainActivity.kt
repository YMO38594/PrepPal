package com.preppal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.preppal.navigation.AppNavHost
import com.preppal.ui.theme.PrepPalTheme
import com.preppal.ui.theme.screens.home.HomeScreen
import com.preppal.ui.theme.screens.notes.NotesScreen
import com.preppal.ui.theme.screens.profile.ProfileScreen
import com.preppal.ui.theme.screens.login.LoginScreen
import com.preppal.ui.theme.screens.registration.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        setContent {
            PrepPalTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigator()
                }
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController = navController)}
            composable("notes") { NotesScreen() }
            composable("profile") { ProfileScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            }
        }
        }




@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentDestination == "home",
            onClick = {
                if (currentDestination != "home") {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Notes") },
            label = { Text("Notes") },
            selected = currentDestination == "notes",
            onClick = {
                if (currentDestination != "notes") {
                    navController.navigate("notes") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}


