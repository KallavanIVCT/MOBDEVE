package com.example.mobdeve

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra("TODO_ID", -1)
        val todoTitle = intent.getStringExtra("TODO_TITLE") ?: "Todo Reminder"

        val builder = NotificationCompat.Builder(context, "TODO_CHANNEL")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Todo Reminder")
            .setContentText(todoTitle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(todoId, builder.build())
    }
}