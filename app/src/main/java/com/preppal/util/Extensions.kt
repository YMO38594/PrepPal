package com.preppal.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

fun Context.showToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, message, duration).show()
}


fun Context.showToast(
    @StringRes messageRes: Int,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, messageRes, duration).show()
}


@Composable
fun rememberToast(): (String) -> Unit {
    val context = LocalContext.current
    return { message -> context.showToast(message) }
}

@Composable
fun ProfileScreen(navController: NavController) {
    val showToast = rememberToast() // Get the toast function

    Button(onClick = {
        showToast("Logged out successfully")
    }) {
        Text("Log Out")
    }
}