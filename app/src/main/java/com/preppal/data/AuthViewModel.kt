package com.preppal.data

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.preppal.model.User
import com.preppal.navigation.ROUTE_HOME
import com.preppal.navigation.ROUTE_LOGIN
import com.preppal.navigation.ROUTE_REGISTER

class AuthViewModel(
    private val navController: NavHostController,
    private val context: Context
) {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signup(email: String, pass: String, confpass: String) {
        if (email.isBlank() || pass.isBlank() || confpass.isBlank()) {
            Toast.makeText(context, "Email and password cannot be blank", Toast.LENGTH_LONG).show()
            return
        }

        if (pass != confpass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser?.uid
                if (userId != null) {
                    val userData = User(email, pass, userId)
                    val regRef = FirebaseDatabase.getInstance().getReference("Users/$userId")

                    regRef.setValue(userData).addOnCompleteListener { regTask ->
                        if (regTask.isSuccessful) {
                            Toast.makeText(context, "Registration successful", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_LOGIN)
                        } else {
                            Toast.makeText(context, "Failed to save user data: ${regTask.exception?.message}", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_REGISTER)
                        }
                    }
                } else {
                    Toast.makeText(context, "User ID is null", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_REGISTER)
                }
            } else {
                Toast.makeText(context, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(context, "Email and password cannot be blank", Toast.LENGTH_LONG).show()
            return
        }

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Successfully logged in", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_HOME)
            } else {
                Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_LOGIN)
            }
        }
    }

    fun logout() {
        mAuth.signOut()
        navController.navigate(ROUTE_LOGIN)
    }

    fun isLoggedIn(): Boolean {
        return mAuth.currentUser != null
    }
}






