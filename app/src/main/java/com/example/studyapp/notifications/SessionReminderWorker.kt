package com.example.studyapp.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class SessionReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val sessionId = inputData.getString(KEY_SESSION_ID) ?: return Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: "Study session"
        val course = inputData.getString(KEY_COURSE).orEmpty()
        val building = inputData.getString(KEY_BUILDING).orEmpty()
        val room = inputData.getString(KEY_ROOM).orEmpty()

        createNotificationChannel(applicationContext)
        if (!hasNotificationPermission(applicationContext)) return Result.success()

        val location = listOf(building, room)
            .filter { it.isNotBlank() }
            .joinToString(", ")
        val details = listOf(course, location)
            .filter { it.isNotBlank() }
            .joinToString(" • ")

        val notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID_FOR_WORKER
        )
            .setSmallIcon(com.example.studyapp.R.mipmap.ic_launcher)
            .setContentTitle("Your study session is starting")
            .setContentText(if (details.isBlank()) title else "$title • $details")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    listOf(title, details).filter { it.isNotBlank() }.joinToString("\\n")
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(notificationId(sessionId), notification)
        return Result.success()
    }

    companion object {
        const val KEY_SESSION_ID = "session_id"
        const val KEY_TITLE = "session_title"
        const val KEY_COURSE = "session_course"
        const val KEY_BUILDING = "session_building"
        const val KEY_ROOM = "session_room"
    }
}

private const val CHANNEL_ID_FOR_WORKER = "study_session_updates"
