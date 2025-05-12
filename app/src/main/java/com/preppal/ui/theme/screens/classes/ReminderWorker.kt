import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.preppal.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            withContext(Dispatchers.IO) {
                val title = inputData.getString("TITLE") ?: "Class Reminder"
                showNotification(title)
                Result.success()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(title: String) {
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "class_reminders",
                "Class Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for class reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build notification
        val notification = NotificationCompat.Builder(applicationContext, "class_reminders")
            .setContentTitle("Upcoming Class")
            .setContentText(title)
            .setSmallIcon(R.drawable.pp2)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
class NotificationCompat {

}