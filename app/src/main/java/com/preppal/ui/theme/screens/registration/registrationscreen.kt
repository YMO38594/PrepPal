package com.preppal.ui.theme.screens.registration

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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.preppal.navigation.ROUTE_HOME
import com.preppal.navigation.ROUTE_LOGIN

@Composable
fun RegisterScreen(navController: NavHostController) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val auth = Firebase.auth
    val database = Firebase.database.reference

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Error message display
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Username field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email field
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

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Register Button
        Button(
            onClick = {
                errorMessage = validateInputs(
                    username = username,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword
                )

                if (errorMessage == null) {
                    isLoading = true
                    registerUser(
                        username = username,
                        email = email,
                        password = password,
                        auth = auth,
                        database = database,
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                            navController.navigate(ROUTE_HOME) {
                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        TextButton(
            onClick = { navController.navigate(ROUTE_LOGIN) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Already have an account? Login")
        }
    }
}


private fun validateInputs(
    username: String,
    email: String,
    password: String,
    confirmPassword: String
): String? {
    return when {
        username.isEmpty() -> "Username cannot be empty"
        username.length < 3 -> "Username must be at least 3 characters"
        email.isEmpty() -> "Email cannot be empty"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
        password.isEmpty() -> "Password cannot be empty"
        password.length < 6 -> "Password must be at least 6 characters"
        password != confirmPassword -> "Passwords do not match"
        else -> null
    }
}

// Registration function with Firebase integration
private fun registerUser(
    username: String,
    email: String,
    password: String,
    auth: FirebaseAuth,
    database: com.google.firebase.database.DatabaseReference,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                // Save user data to Realtime Database
                val userData = hashMapOf(
                    "username" to username,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )

                database.child("users").child(userId).setValue(userData)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        // If database fails, delete the created user to maintain consistency
                        auth.currentUser?.delete()
                        onError(e.message ?: "Failed to save user data")
                    }
            } else {
                onError(task.exception?.message ?: "Registration failed")
            }
        }
}