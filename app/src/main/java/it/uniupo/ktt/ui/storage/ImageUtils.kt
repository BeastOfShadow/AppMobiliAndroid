package it.uniupo.ktt.ui.storage

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// In it.uniupo.ktt.ui.storage
suspend fun uploadImageToStorage(localPath: String, storagePath: String) {
    withContext(Dispatchers.IO) {
        try {
            Log.d("FirebaseUpload", "Start upload: $localPath → $storagePath")

            val uri = Uri.parse(localPath)
            val storageRef = FirebaseStorage.getInstance().reference.child(storagePath)

            storageRef.putFile(uri).await()
            Log.d("FirebaseUpload", "Upload completato con successo: $storagePath")
        } catch (e: kotlinx.coroutines.CancellationException) {
            Log.e("FirebaseUpload", "Upload cancellato per $storagePath: ${e.message}")
            throw e
        } catch (e: Exception) {
            Log.e("FirebaseUpload", "Errore durante l'upload di $storagePath: ${e.message}", e)
        }
    }
}