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
    Log.d("commitSubtask", "▶️ Inizio upload: uri=$uri -> storagePath=$storagePath")
    val storageRef = FirebaseStorage.getInstance().reference.child(storagePath)

    storageRef.putFile(uri)
        .addOnSuccessListener {
            Log.d("commitSubtask", "✅ Upload completato con successo: $storagePath")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e("commitSubtask", "❌ Errore durante upload: ${e.message}", e)
            onError(e)
        }
        .addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            Log.d("commitSubtask", "⏳ Upload in corso: ${"%.2f".format(progress)}%")
        }
        .addOnPausedListener {
            Log.d("commitSubtask", "⏸ Upload messo in pausa")
        }
}
