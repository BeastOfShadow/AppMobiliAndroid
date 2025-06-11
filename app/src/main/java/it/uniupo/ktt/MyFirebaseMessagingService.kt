package it.uniupo.ktt

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Data: ${remoteMessage.data}")
        }


        // FILTRO: APP NOT IN FOREGROUND
        val appIsInForeground = isAppInForeground(applicationContext)

        remoteMessage.data.let { data ->

            if (!appIsInForeground) {
                val title = data["title"] ?: "Titolo di default"
                val body = data["body"] ?: "Messaggio di default"
                sendNotification(title, body)
            }

        }


    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "default_channel_id"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // ------------------------------- CREAZIONE NOTIFICA (se non esistente) -------------------------------
        val notification = NotificationCompat.Builder(this, channelId)

            .setSmallIcon(R.drawable.main_icon_push_notify) // svg- image nell'Assets

            .setContentTitle(title)
            .setContentText(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.main_icon_circle))

            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        // ------------------------------- CREAZIONE NOTIFICA (se non esistente) -------------------------------


        // ------------------------------- CREAZIONE CHANNEL (se non esistente) -------------------------------
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "KTT",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(defaultSoundUri, null)
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
                description = "Canale notifiche per nuovi messaggi"
            }

            notificationManager.createNotificationChannel(channel)
        }
        // ------------------------------- CREAZIONE CHANNEL (se non esistente) -------------------------------

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "Nuovo token FCM: $token")

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            Firebase.firestore.collection("users").document(uid)
                .update("deviceToken", token)
                .addOnSuccessListener {
                    Log.d("FCM", "Token aggiornato con successo")
                }
                .addOnFailureListener {
                    Log.e("FCM", "Errore aggiornamento token: ${it.message}")
                }
        }
    }

    // Filtro per evitare di mostrare le PushNotification quando "APP==FOREGROUND"
    fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == packageName
            ) {
                return true
            }
        }
        return false
    }
}