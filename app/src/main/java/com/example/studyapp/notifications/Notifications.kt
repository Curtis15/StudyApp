package com.example.studyapp.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import java.util.concurrent.TimeUnit
import com.example.studyapp.data.StudySession
import com.example.studyapp.R

private const val CHANNEL_ID = "study_session_updates"
private const val CHANNEL_NAME = "Study session reminders"
private const val REMINDER_PREFIX = "study_session_reminder_"

fun reminder(context: Context, session: StudySession) {
    if (session.isEnded) return

    createNotificationChannel(context)

    val delayMillis = (session.startTime - System.currentTimeMillis()).coerceAtLeast(0L)
    val input = Data.Builder()
        .putString(SessionReminderWorker.KEY_SESSION_ID, session.id)
        .putString(SessionReminderWorker.KEY_TITLE, session.title)
        .putString(SessionReminderWorker.KEY_COURSE, session.course)
        .putString(SessionReminderWorker.KEY_BUILDING, session.building)
        .putString(SessionReminderWorker.KEY_ROOM, session.room)
        .build()

    val request = OneTimeWorkRequestBuilder<SessionReminderWorker>()
        .setInputData(input)
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
        REMINDER_PREFIX + session.id,
        ExistingWorkPolicy.REPLACE,
        request
    )
}

fun showCancel(context: Context, session: StudySession) {
    WorkManager.getInstance(context.applicationContext)
        .cancelUniqueWork(REMINDER_PREFIX + session.id)

    createNotificationChannel(context)
    if (!hasNotificationPermission(context)) return

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(com.example.studyapp.R.mipmap.ic_launcher)
        .setContentTitle("Study session cancelled")
        .setContentText("${session.title} (${session.course}) has been cancelled.")
        .setStyle(
            NotificationCompat.BigTextStyle().bigText(
                "${session.title} for ${session.course} at ${session.building}" +
                        (session.room.takeIf { it.isNotBlank() }?.let { ", room $it" } ?: "") +
                        " has been cancelled."
            )
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(notificationId(session.id), notification)
}

internal fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = android.app.NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            android.app.NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Reminders and updates about ASTATE Study App sessions"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as android.app.NotificationManager
        manager.createNotificationChannel(channel)
    }
}

fun hasNotificationPermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
}

internal fun notificationId(sessionId: String): Int = sessionId.hashCode()
