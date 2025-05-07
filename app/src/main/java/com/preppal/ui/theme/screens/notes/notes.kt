package com.preppal.ui.theme.screens.notes

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun NotesScreen() {
    val context = LocalContext.current
    var noteUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input field for the notes URL
        OutlinedTextField(
            value = noteUrl,
            onValueChange = { noteUrl = it },
            label = { Text("Paste your notes URL") },
            placeholder = { Text("https://docs.google.com/document/...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to open the notes
        Button(
            onClick = {
                if (noteUrl.isNotBlank()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(noteUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Handle invalid URL
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Notes")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick links to popular note services
        Text("Popular note services:", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))

        NoteServiceButton("EasyElimu FORM 1", "https://app.easyelimu.com/lessons/6-high-school/6-form-1")
        NoteServiceButton("Teacher.co.ke FORM 1", "https://teacher.co.ke/form-1-notes-all-subjects/")
        NoteServiceButton("EasyElimu FORM 2", "https://app.easyelimu.com/lessons/6-high-school/16-form-2")
        NoteServiceButton("Teacher.co.ke FORM 2", "https://teacher.co.ke/form-2-notes-all-subjects/")
        NoteServiceButton("EasyElimu FORM 3", "https://app.easyelimu.com/lessons/6-high-school/17-form-3")
        NoteServiceButton("Teacher.co.ke FORM 3", "https://teacher.co.ke/form-3-notes-all-subjects/#google_vignette")
        NoteServiceButton("EasyElimu FORM 4", "https://app.easyelimu.com/lessons/6-high-school/18-form-4")
        NoteServiceButton("Teacher.co.ke FORM 4", "https://teacher.co.ke/form-4-notes-all-subjects/")
    }
}

@Composable
fun NoteServiceButton(name: String, url: String) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(name)
    }
}