package com.preppal.ui.theme.screens.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class ProfileViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val database = Firebase.database.reference
}