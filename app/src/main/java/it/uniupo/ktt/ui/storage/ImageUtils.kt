package it.uniupo.ktt.ui.storage

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

suspend fun uploadImageToStorage(localPath: String, storagePath: String): String {
    return try {
        val storageRef = FirebaseStorage.getInstance().reference.child(storagePath)
        val uri = Uri.parse(localPath)
        storageRef.putFile(uri).await()
        storageRef.downloadUrl.await().toString()
    } catch (e: Exception) {
        Log.e("FirebaseUpload", "Errore upload immagine: ${e.message}")
        ""
    }
}
