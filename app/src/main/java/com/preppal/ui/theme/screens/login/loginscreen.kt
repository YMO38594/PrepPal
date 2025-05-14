package com.preppal.ui.theme.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.preppal.navigation.ROUTE_HOME
import com.preppal.navigation.ROUTE_REGISTER

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = Firebase.auth
    val database = Firebase.database.reference

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    isLoading = true
                    loginUser(
                        email = email,
                        password = password,
                        auth = auth,
                        database = database,
                        onSuccess = { userId ->
                            // Fetch additional user data from database
                            database.child("users").child(userId)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val userData = snapshot.getValue(UserData::class.java)
                                        Toast.makeText(
                                            context,
                                            "Welcome back, ${userData?.username ?: "User"}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate(ROUTE_HOME) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(
                                            context,
                                            "Error fetching user data",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate(ROUTE_HOME) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                })
                        },
                        onError = { error ->
                            Toast.makeText(context, "Login failed: $error", Toast.LENGTH_SHORT).show()
                            isLoading = false
                        }
                    )
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate(ROUTE_REGISTER) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account? Register")
        }
    }
}

private fun loginUser(
    email: String,
    password: String,
    auth: FirebaseAuth,
    database: com.google.firebase.database.DatabaseReference,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                onSuccess(userId)
            } else {
                onError(task.exception?.message ?: "Authentication failed")
            }
        }
}

// Data class for user data structure in Realtime Database
data class UserData(
    val username: String = "",
    val email: String = "",
    val createdAt: Long = 0
)