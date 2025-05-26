package it.uniupo.ktt.ui.storage

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

fun uploadImageToStorage(
    localPath: String,
    storagePath: String,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {

    val uri = Uri.parse(localPath)
    val storageRef = FirebaseStorage.getInstance().reference.child(storagePath)

    storageRef.putFile(uri)
        .addOnSuccessListener {
            Log.d("FirebaseUpload", "Upload completato con successo: $storagePath")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e("FirebaseUpload", "Errore durante upload: ${e.message}", e)
            onError(e)
        }
}
