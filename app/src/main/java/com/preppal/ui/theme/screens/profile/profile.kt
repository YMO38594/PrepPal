package com.preppal.ui.theme.screens.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.preppal.R
import com.preppal.navigation.ROUTE_LOGIN
import com.preppal.ui.theme.screens.components.ConfirmationDialog
import com.preppal.ui.theme.navigation.Screen
import com.preppal.util.rememberToast
import com.preppal.util.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val database = Firebase.database.reference

    var username by remember { mutableStateOf("$currentUser") }
    var email by remember { mutableStateOf("$currentUser") }
    var bio by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val showToast = rememberToast()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { userId ->
            database.child("users").child(userId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        username = snapshot.child("username").value.toString()
                        email = currentUser.email ?: "No email"
                        bio = snapshot.child("bio").value?.toString() ?: "No bio yet"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        context.showToast("Failed to load profile data: ${error.message}")

                    }

         }
            )
        }
    }



 fun logout() {
        auth.signOut()
        navController.navigate(Screen.Login.route) {
            popUpTo(0)
        }
        context.showToast("Logged out successfully")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "My Profile",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = {navController.navigate(ROUTE_LOGIN)}
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.pp2),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { isEditing = false })
                )
            } else {
                Text(
                    text = username,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "About Me",
                        style = MaterialTheme.typography.titleMedium
                    )

                    IconButton(
                        onClick = { isEditing = !isEditing },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Save" else "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Bio") },
                        maxLines = 3
                    )
                } else {
                    Text(
                        text = bio.ifEmpty { "No bio yet" },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Account Settings",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var showEmailDialog by remember { mutableStateOf(false) }
            var showPasswordDialog by remember { mutableStateOf(false) }
            var showDeleteDialog by remember { mutableStateOf(false) }
            val auth = Firebase.auth
            val context = LocalContext.current

            // Change Email
            ProfileSettingItem(
                icon = Icons.Default.Email,
                text = "Change Email",
                onClick = { showEmailDialog = true }
            )

            if (showEmailDialog) {
                var newEmail by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(false) }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                AlertDialog(
                    onDismissRequest = { showEmailDialog = false },
                    title = { Text("Change Email") },
                    text = {
                        Column {
                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage!!,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            OutlinedTextField(
                                value = newEmail,
                                onValueChange = { newEmail = it },
                                label = { Text("New Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Current Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newEmail.isEmpty() || password.isEmpty()) {
                                    errorMessage = "Please fill all fields"
                                    return@Button
                                }

                                isLoading = true
                                val user = auth.currentUser
                                val credential = EmailAuthProvider
                                    .getCredential(user?.email ?: "", password)

                                user?.reauthenticate(credential)?.addOnCompleteListener { reauthTask ->
                                    if (reauthTask.isSuccessful) {
                                        user.updateEmail(newEmail)
                                            .addOnCompleteListener { updateTask ->
                                                isLoading = false
                                                if (updateTask.isSuccessful) {
                                                    Toast.makeText(context, "Email updated successfully", Toast.LENGTH_SHORT).show()
                                                    showEmailDialog = false
                                                } else {
                                                    errorMessage = updateTask.exception?.message ?: "Failed to update email"
                                                }
                                            }
                                    } else {
                                        isLoading = false
                                        errorMessage = "Authentication failed: ${reauthTask.exception?.message}"
                                    }
                                }
                            },
                            enabled = !isLoading
                        ) {
                            if (isLoading) CircularProgressIndicator() else Text("Update")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showEmailDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Change Password
            ProfileSettingItem(
                icon = Icons.Default.Lock,
                text = "Change Password",
                onClick = { showPasswordDialog = true }
            )

            if (showPasswordDialog) {
                var currentPassword by remember { mutableStateOf("") }
                var newPassword by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(false) }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                AlertDialog(
                    onDismissRequest = { showPasswordDialog = false },
                    title = { Text("Change Password") },
                    text = {
                        Column {
                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage!!,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            OutlinedTextField(
                                value = currentPassword,
                                onValueChange = { currentPassword = it },
                                label = { Text("Current Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("New Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm New Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newPassword != confirmPassword) {
                                    errorMessage = "Passwords don't match"
                                    return@Button
                                }
                                if (newPassword.length < 6) {
                                    errorMessage = "Password must be at least 6 characters"
                                    return@Button
                                }

                                isLoading = true
                                val user = auth.currentUser
                                val credential = EmailAuthProvider
                                    .getCredential(user?.email ?: "", currentPassword)

                                user?.reauthenticate(credential)?.addOnCompleteListener { reauthTask ->
                                    if (reauthTask.isSuccessful) {
                                        user.updatePassword(newPassword)
                                            .addOnCompleteListener { updateTask ->
                                                isLoading = false
                                                if (updateTask.isSuccessful) {
                                                    Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                                    showPasswordDialog = false
                                                } else {
                                                    errorMessage = updateTask.exception?.message ?: "Failed to update password"
                                                }
                                            }
                                    } else {
                                        isLoading = false
                                        errorMessage = "Authentication failed: ${reauthTask.exception?.message}"
                                    }
                                }
                            },
                            enabled = !isLoading
                        ) {
                            if (isLoading) CircularProgressIndicator() else Text("Update")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showPasswordDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Delete Account
            ProfileSettingItem(
                icon = Icons.Default.Delete,
                text = "Delete Account",
                color = MaterialTheme.colorScheme.error,
                onClick = { showDeleteDialog = true }
            )

            if (showDeleteDialog) {
                var password by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(false) }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Account") },
                    text = {
                        Column {
                            Text("This action cannot be undone. All your data will be permanently deleted.")
                            Spacer(modifier = Modifier.height(16.dp))
                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage!!,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Enter Password to Confirm") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (password.isEmpty()) {
                                    errorMessage = "Please enter your password"
                                    return@Button
                                }

                                isLoading = true
                                val user = auth.currentUser
                                val credential = EmailAuthProvider
                                    .getCredential(user?.email ?: "", password)

                                user?.reauthenticate(credential)?.addOnCompleteListener { reauthTask ->
                                    if (reauthTask.isSuccessful) {
                                        // First delete user data from database
                                        Firebase.database.reference.child("users").child(user.uid).removeValue()
                                            .addOnSuccessListener {
                                                // Then delete auth account
                                                user.delete()
                                                    .addOnCompleteListener { deleteTask ->
                                                        isLoading = false
                                                        if (deleteTask.isSuccessful) {
                                                            Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                                                            navController.navigate(ROUTE_LOGIN) {
                                                                popUpTo(0) { inclusive = true }
                                                            }
                                                        } else {
                                                            errorMessage = deleteTask.exception?.message ?: "Failed to delete account"
                                                        }
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                isLoading = false
                                                errorMessage = "Failed to delete user data: ${e.message}"
                                            }
                                    } else {
                                        isLoading = false
                                        errorMessage = "Authentication failed: ${reauthTask.exception?.message}"
                                    }
                                }
                            },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            if (isLoading) CircularProgressIndicator() else Text("Delete Permanently")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
    if (showLogoutDialog) {
        ConfirmationDialog(
            title = "Log Out",
            message = "Are you sure you want to log out?",
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                logout()
            }
        )
    }
}

@Composable
fun ProfileStat(count: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun ProfileSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = color
            )
        }
    }
}