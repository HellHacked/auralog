package com.example.auralog.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.auralog.data.UserStats
import com.example.auralog.data.UserStatsDataStore
import com.example.auralog.network.AiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MigrainePredictionManager {

    private const val CHANNEL_ID = "migraine_notifications"
    private const val NOTIF_ID = 2001
    private var lastWarningTimestamp = 0L

    suspend fun runPrediction(context: Context, showNotification: Boolean = true) {
        try {
            // Save latest user stats first
            UserStatsDataStore.save(context)

            // Build prompt and run AI prediction
            val prompt = UserStats.buildPrompt()
            val result = withContext(Dispatchers.IO) {
                AiRepository().predictMigraine(prompt)
            }

            // Update Compose state
            UserStats.predictionResult = result

            // Show notification if probability >= 50%, but throttle to once every 6h
            val now = System.currentTimeMillis()
            //if (showNotification && result >= 50 && now - lastWarningTimestamp > 6 * 60 * 60 * 1000) {
            //    lastWarningTimestamp = now
            //}
            if (result >= 50) {
                sendWarningNotification(context, result)
            }

            Log.d("MigrainePrediction", "Prediction result: $result")
        } catch (e: Exception) {
            Log.e("MigrainePrediction", "Error running prediction", e)
        }
    }

    @SuppressLint("MissingPermission", "NotificationPermission")
    private fun sendWarningNotification(context: Context, prob: Int) {
        val manager = NotificationManagerCompat.from(context)

        // Ensure notification channel exists
        val channel = NotificationChannelCompat.Builder(
            CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName("Migraine Alerts")
            .build()
        manager.createNotificationChannel(channel)

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("High Migraine Risk")
            .setContentText("Your migraine probability is $prob%")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Only post if permission granted
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            manager.notify(NOTIF_ID, notif)
        } else {
            Log.d("MigrainePrediction", "Notification permission not granted")
        }
    }
}
