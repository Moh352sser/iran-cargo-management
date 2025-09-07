package com.irancargocompany.logistics.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.irancargocompany.logistics.R
import com.irancargocompany.logistics.ui.main.MainActivity

class FirebaseMessagingService : FirebaseMessagingService() {
    
    companion object {
        private const val CHANNEL_ID = "trip_notifications"
        private const val NOTIFICATION_ID = 2001
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Handle FCM messages here
        remoteMessage.notification?.let { notification ->
            showNotification(
                notification.title ?: getString(R.string.trip_notification_title),
                notification.body ?: ""
            )
        }
        
        // Handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            handleDataPayload(remoteMessage.data)
        }
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to your server
        sendTokenToServer(token)
    }
    
    private fun handleDataPayload(data: Map<String, String>) {
        // Handle custom data payload
        val tripId = data["trip_id"]
        val action = data["action"]
        
        when (action) {
            "trip_updated" -> {
                showNotification(
                    getString(R.string.trip_notification_title),
                    data["message"] ?: "Trip has been updated"
                )
            }
            "trip_status_changed" -> {
                showNotification(
                    getString(R.string.trip_notification_title),
                    data["message"] ?: "Trip status has changed"
                )
            }
        }
    }
    
    private fun showNotification(title: String, body: String) {
        createNotificationChannel()
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_trips),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for trip updates"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun sendTokenToServer(token: String) {
        // Implement sending token to your server
        // This would typically involve making an API call
    }
}