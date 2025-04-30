package com.preppal.ui.theme.screens.classes

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit


@Composable
fun ReminderScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var delayInMinutes by remember { mutableStateOf("") }
    val context= LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Class/Lesson Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = delayInMinutes,
            onValueChange = { delayInMinutes = it },
            label = { Text("Reminder in minutes") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val delay = delayInMinutes.toLongOrNull() ?: 0L

                scheduleReminder(
                    context = context,
                    title = title,
                    delayInMinutes = delay
                )
                navController.popBackStack() // or navigate to confirmation screen
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Set Reminder")
        }
    }
}

fun scheduleReminder(context: Context, title: String, delayInMinutes: Long) {
    val data = workDataOf("TITLE" to title)

    val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInputData(data)
        .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
        .build()

    WorkManager.getInstance(context).enqueue(reminderRequest)
}



@Preview(showBackground = true)
@Composable
fun ReminderScreenPreview() {
    ReminderScreen(navController = rememberNavController())
}


