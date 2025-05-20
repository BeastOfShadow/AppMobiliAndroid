package it.uniupo.ktt

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.UserRepository

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuovo token ricevuto: $token")

        val currentUid = BaseRepository.currentUid()
        if (currentUid != null) {
            UserRepository.updateDeviceToken(
                uid = currentUid,
                token = token,
                onSuccess = { Log.d("FCM", "Token aggiornato nel DB") },
                onError = { e -> Log.e("FCM", "Errore update token: ${e.message}") }
            )
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "Nuovo messaggio"
        val body = remoteMessage.notification?.body ?: "Hai ricevuto un nuovo messaggio"

        Log.d("FCM", "Notifica ricevuta: $title - $body")

        // TODO: Costruisci una notifica visibile con NotificationCompat.Builder (te la posso generare)
    }
}