package com.preppal.ui.theme.screens.profile

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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

            ProfileSettingItem(
                icon = Icons.Default.Email,
                text = "Change Email",
                onClick = { /* TODO */ }
            )
            ProfileSettingItem(
                icon = Icons.Default.Lock,
                text = "Change Password",
                onClick = { /* TODO */ }
            )
            ProfileSettingItem(
                icon = Icons.Default.Delete,
                text = "Delete Account",
                color = MaterialTheme.colorScheme.error,
                onClick = { /* TODO */ }
            )
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