package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.TaskRepository
import it.uniupo.ktt.ui.model.SubTask
import it.uniupo.ktt.ui.storage.uploadImageToStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class SubTaskViewModel : ViewModel() {
    private val _subtasks = MutableStateFlow<List<SubTask>>(emptyList())
    val subtasks: StateFlow<List<SubTask>> = _subtasks.asStateFlow()

    suspend fun getSubtaskById(taskId: String, subtaskId: String): SubTask? {
        return try {
            val doc = BaseRepository.db.collection("tasks")
                .document(taskId)
                .collection("subtasks")
                .document(subtaskId)
                .get()
                .await()

            if (doc.exists()) {
                doc.toObject(SubTask::class.java)?.copy(id = doc.id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("SubTaskViewModel", "Error getting subtask: ${e.message}")
            null
        }
    }

    suspend fun updateSubtaskStatus(taskId: String, subtaskId: String, newStatus: String): Boolean {
        return try {
            BaseRepository.db.collection("tasks")
                .document(taskId)
                .collection("subtasks")
                .document(subtaskId)
                .update("status", newStatus)
                .await()
            Log.d("SubTaskViewModel", "✅ Subtask status updated to $newStatus")
            true
        } catch (e: Exception) {
            Log.e("SubTaskViewModel", "❌ Error updating subtask status: ${e.message}")
            false
        }
    }

    fun commitSubtask(
        taskId: String,
        subtask: SubTask,
        newStatus: String,
        comment: String,
        localImagePath: String?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        var updatedSubtask = subtask.copy(
            status = newStatus,
            employeeComment = comment
        )

        if (!localImagePath.isNullOrBlank()) {
            val imagePath = "employeeCommentSubtaskImages/${taskId}_${subtask.id}.jpg"
            Log.d("commitSubtask", "📷 Immagine selezionata. Avvio upload verso: $imagePath (da $localImagePath)")

            uploadImageToStorage(
                localPath = localImagePath,
                storagePath = imagePath,
                onSuccess = {
                    Log.d("commitSubtask", "✅ Upload completato, aggiorno subtask con path immagine: $imagePath")
                    updatedSubtask = updatedSubtask.copy(employeeImgStorageLocation = imagePath)

                    Log.d("commitSubtask", "💾 Salvo subtask aggiornata con immagine...")
                    TaskRepository.saveSubtask(taskId, updatedSubtask, {
                        Log.d("commitSubtask", "✅ Subtask salvata con immagine")
                        onSuccess()
                    }, onError)
                },
                onError = { e ->
                    Log.e("commitSubtask", "❌ Upload immagine fallito: ${e.message}")
                    updatedSubtask = updatedSubtask.copy(employeeImgStorageLocation = "")

                    Log.d("commitSubtask", "💾 Salvo comunque subtask SENZA immagine")
                    TaskRepository.saveSubtask(taskId, updatedSubtask, {
                        Log.d("commitSubtask", "✅ Subtask salvata senza immagine")
                        onSuccess()
                    }, onError)
                }
            )
        } else {
            Log.d("commitSubtask", "📎 Nessuna immagine selezionata. Salvo subtask direttamente.")
            TaskRepository.saveSubtask(taskId, updatedSubtask, {
                Log.d("commitSubtask", "✅ Subtask salvata senza immagine (niente upload richiesto)")
                onSuccess()
            }, onError)
        }
    }

    suspend fun deleteTaskById(taskId: String, subtaskId: String)
    {
        try {
            val subtaskRef = BaseRepository.db.collection("tasks")
                .document(taskId)
                .collection("subtasks")
                .document(subtaskId)

            val snapshot = subtaskRef.get().await()
            if (!snapshot.exists()) {
                Log.w("deleteTaskById", "⚠️ Subtask non trovato")
                return
            }

            val subtask = snapshot.toObject(SubTask::class.java) ?: return
            val deletedNumber = subtask.listNumber
            val imagePath = subtask.employeeImgStorageLocation

            // 1. Elimina immagine associata se presente
            if (!imagePath.isNullOrBlank()) {
                try {
                    FirebaseStorage.getInstance().getReference(imagePath).delete().await()
                    Log.d("deleteTaskById", "✅ Immagine eliminata")
                } catch (e: Exception) {
                    Log.e("deleteTaskById", "❌ Errore eliminazione immagine: ${e.message}")
                }
            }

            // 2. Elimina il subtask dal database
            subtaskRef.delete().await()
            Log.d("deleteTaskById", "✅ Subtask eliminato dal DB")

            // 3. Recupera tutti i subtasks con numero maggiore
            val subtasksToShift = BaseRepository.db.collection("tasks")
                .document(taskId)
                .collection("subtasks")
                .whereGreaterThan("listNumber", deletedNumber)
                .get()
                .await()

            Log.d("deleteTaskById", "📦 Subtasks to shift (${subtasksToShift.size()}) trovati:")

            for (doc in subtasksToShift.documents) {
                Log.d("deleteTaskById", "🧾 ${doc.id} => listNumber = ${doc.getLong("listNumber")}, descrizione = ${doc.getString("description")}")
            }

            for (doc in subtasksToShift.documents) {
                val st = doc.toObject(SubTask::class.java) ?: continue
                val updated = st.copy(listNumber = st.listNumber - 1)
                BaseRepository.db.collection("tasks")
                    .document(taskId)
                    .collection("subtasks")
                    .document(doc.id)
                    .set(updated)
                    .await()
                Log.d("deleteTaskById", "🔁 Shiftato subtask ${doc.id} a numero ${updated.listNumber}")
            }

            Log.d("deleteTaskById", "✅ Shift completato per i subtasks successivi")

        } catch (e: Exception) {
            Log.e("deleteTaskById", "❌ Errore in deleteTaskById: ${e.message}")
        }
    }

    suspend fun fetchSubtask(taskId: String): List<SubTask>
    {
        val db = FirebaseFirestore.getInstance()
        return try {
            val snapshot = db.collection("tasks")
                .document(taskId)
                .collection("subtasks")
                .orderBy("listNumber", Query.Direction.ASCENDING)
                .get()
                .await()  // await per sospendere fino al risultato

            snapshot.documents.mapNotNull { doc ->
                try {
                    SubTask(
                        id = doc.id,
                        listNumber = (doc.getLong("listNumber") ?: 0).toInt(),
                        description = doc.getString("description") ?: "",
                        employeeComment = doc.getString("employeeComment") ?: "",
                        caregiverComment = doc.getString("caregiverComment") ?: "",
                        descriptionImgStorageLocation = doc.getString("descriptionImgStorageLocation") ?: "",
                        employeeImgStorageLocation = doc.getString("employeeImgStorageLocation") ?: "",
                        caregiverImgStorageLocation = doc.getString("caregiverImgStorageLocation") ?: "",
                        status = doc.getString("status") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun updateSubtask(
        taskId: String,
        updatedSubtask: SubTask,
        storageLocation: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val storage = FirebaseStorage.getInstance()
        val subtaskRef = BaseRepository.db.collection("tasks")
            .document(taskId)
            .collection("subtasks")
            .document(updatedSubtask.id)

        Log.d("updateSubtask", "Inizio updateSubtask per subtask ${updatedSubtask.id} del task $taskId")

        // Prima prendi il subtask attuale dal DB per sapere com’era l’immagine
        subtaskRef.get()
            .addOnSuccessListener { doc ->
                Log.d("updateSubtask", "Documento subtask recuperato, esiste: ${doc.exists()}")

                if (!doc.exists()) {
                    Log.e("updateSubtask", "Subtask non trovato nel DB")
                    onError(Exception("Subtask non trovato"))
                    return@addOnSuccessListener
                }

                val oldSubtask = doc.toObject(SubTask::class.java)
                val oldImagePath = oldSubtask?.descriptionImgStorageLocation ?: ""
                val newImagePath = updatedSubtask.descriptionImgStorageLocation ?: ""

                Log.d("updateSubtask", "Vecchia immagine: '$oldImagePath', Nuova immagine: '$newImagePath'")

                fun saveSubtask() {
                    Log.d("updateSubtask", "Salvataggio subtask senza modificare immagine")
                    subtaskRef.set(updatedSubtask)
                        .addOnSuccessListener {
                            Log.d("updateSubtask", "Subtask salvato con successo")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("updateSubtask", "Errore salvataggio subtask", e)
                            onError(e)
                        }
                }

                // Caso 1: immagine è stata rimossa
                if (newImagePath.isBlank() && oldImagePath.isNotBlank()) {
                    Log.d("updateSubtask", "Immagine rimossa, cancellazione immagine precedente")
                    storage.getReference(oldImagePath).delete()
                        .addOnSuccessListener {
                            Log.d("updateSubtask", "Vecchia immagine cancellata")
                            val toSave = updatedSubtask.copy(descriptionImgStorageLocation = "")
                            subtaskRef.set(toSave)
                                .addOnSuccessListener {
                                    Log.d("updateSubtask", "Subtask salvato dopo cancellazione immagine")
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("updateSubtask", "Errore salvataggio subtask dopo cancellazione immagine", e)
                                    onError(e)
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.e("updateSubtask", "Errore cancellazione vecchia immagine", e)
                            onError(e)
                        }

                    return@addOnSuccessListener
                }

                // Caso 2: immagine è cambiata
                if (newImagePath.isNotBlank() && newImagePath != oldImagePath) {
                    Log.d("updateSubtask", "Immagine cambiata, gestisco upload e cancellazione")

                    val deleteOldImageTask = if (oldImagePath.isNotBlank()) {
                        Log.d("updateSubtask", "Cancellazione vecchia immagine esistente")
                        storage.getReference(oldImagePath).delete()
                    } else null

                    deleteOldImageTask?.addOnCompleteListener {
                        Log.d("updateSubtask", "Vecchia immagine cancellata o non esisteva, upload nuova immagine")
                        val storagePath = "${storageLocation}/${taskId}_${updatedSubtask.id}.jpg"

                        uploadImageToStorage(
                            localPath = newImagePath,
                            storagePath = storagePath,
                            onSuccess = {
                                Log.d("updateSubtask", "Upload immagine completato, salvo subtask")
                                val toSave = updatedSubtask.copy(descriptionImgStorageLocation = storagePath)
                                subtaskRef.set(toSave)
                                    .addOnSuccessListener {
                                        Log.d("updateSubtask", "Subtask salvato dopo upload immagine")
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("updateSubtask", "Errore salvataggio subtask dopo upload immagine", e)
                                        onError(e)
                                    }
                            },
                            onError = { e ->
                                Log.e("updateSubtask", "Errore upload immagine", e)
                                onError(e)
                            }
                        )
                    } ?: run {
                        Log.d("updateSubtask", "Nessuna vecchia immagine da cancellare, procedo con upload")
                        val storagePath = "${storageLocation}/${taskId}_${updatedSubtask.id}.jpg"
                        uploadImageToStorage(
                            localPath = newImagePath,
                            storagePath = storagePath,
                            onSuccess = {
                                Log.d("updateSubtask", "Upload immagine completato, salvo subtask")
                                val toSave = updatedSubtask.copy(descriptionImgStorageLocation = storagePath)
                                subtaskRef.set(toSave)
                                    .addOnSuccessListener {
                                        Log.d("updateSubtask", "Subtask salvato dopo upload immagine")
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("updateSubtask", "Errore salvataggio subtask dopo upload immagine", e)
                                        onError(e)
                                    }
                            },
                            onError = { e ->
                                Log.e("updateSubtask", "Errore upload immagine", e)
                                onError(e)
                            }
                        )
                    }

                    return@addOnSuccessListener
                }

                // Caso 3: immagine non è cambiata, salvo solo dati
                Log.d("updateSubtask", "Immagine non cambiata, salvo solo dati")
                saveSubtask()
            }
            .addOnFailureListener { e ->
                Log.e("updateSubtask", "Errore recupero subtask", e)
                onError(e)
            }
    }

    fun addSubtask(
        taskId: String,
        description: String,
        imageUri: String?, // può essere null o content:// URI
        listNumber: Int,
        storageLocation: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val db = BaseRepository.db
        val storage = FirebaseStorage.getInstance()
        val subtasksRef = db.collection("tasks").document(taskId).collection("subtasks")
        val newSubtaskId = subtasksRef.document().id

        val subtaskDoc = subtasksRef.document(newSubtaskId)

        if (imageUri.isNullOrBlank() || !imageUri.startsWith("content://")) {
            // Nessuna immagine da caricare o è già un path Firebase
            val newSubtask = SubTask(
                id = newSubtaskId,
                description = description,
                descriptionImgStorageLocation = imageUri ?: "",
                listNumber = listNumber
            )

            subtaskDoc.set(newSubtask)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
        } else {
            // È un URI locale, carica su Firebase e poi salva
            val storagePath = "${storageLocation}/${taskId}_${newSubtaskId}.jpg"

            uploadImageToStorage(
                localPath = imageUri,
                storagePath = storagePath,
                onSuccess = {
                    val newSubtask = SubTask(
                        id = newSubtaskId,
                        description = description,
                        descriptionImgStorageLocation = storagePath,
                        listNumber = listNumber
                    )

                    subtaskDoc.set(newSubtask)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError(it) }
                },
                onError = { e ->
                    onError(e)
                }
            )
        }
    }

    fun commentSubtask(
        taskId: String,
        subtaskId: String,
        newComment: String,
        newImg: String?,              // immagine selezionata, può essere null
        removeImg: Boolean,           // se true, rimuovere immagine esistente
        storageLocation: String,      // percorso Firebase Storage
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val db = BaseRepository.db
        val storage = FirebaseStorage.getInstance()
        val subtaskRef = db.collection("tasks").document(taskId).collection("subtasks").document(subtaskId)

        Log.d("commentSubtask", "Fetching subtask...")

        subtaskRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                Log.e("commentSubtask", "Subtask non trovato.")
                onError(Exception("Subtask non trovato"))
                return@addOnSuccessListener
            }

            val subtask = doc.toObject(SubTask::class.java)
            if (subtask == null) {
                Log.e("commentSubtask", "Errore parsing subtask.")
                onError(Exception("Errore parsing subtask"))
                return@addOnSuccessListener
            }

            Log.d("commentSubtask", "Subtask trovato: $subtask")

            var updatedSubtask = subtask.copy(caregiverComment = newComment)

            val oldImg = subtask.caregiverImgStorageLocation ?: ""
            val newUri = newImg ?: ""

            Log.d("commentSubtask", "Old image: $oldImg")
            Log.d("commentSubtask", "New image: $newUri")
            Log.d("commentSubtask", "Remove image? $removeImg")

            when {
                // CASE 1: Rimozione immagine
                oldImg.isNotBlank() && removeImg -> {
                    Log.d("commentSubtask", "→ Removing old image from storage: $oldImg")

                    storage.getReference(oldImg).delete().addOnSuccessListener {
                        Log.d("commentSubtask", "Image deleted successfully.")
                        updatedSubtask = updatedSubtask.copy(caregiverImgStorageLocation = "")
                        subtaskRef.set(updatedSubtask)
                            .addOnSuccessListener {
                                Log.d("commentSubtask", "Subtask updated (image removed).")
                                onSuccess()
                            }
                            .addOnFailureListener {
                                Log.e("commentSubtask", "Error saving subtask after image removal", it)
                                onError(it)
                            }
                    }.addOnFailureListener {
                        Log.e("commentSubtask", "Error deleting image", it)
                        onError(it)
                    }
                }

                // CASE 2: Aggiunta nuova immagine
                oldImg.isBlank() && newUri.isNotBlank() -> {
                    val newPath = "$storageLocation/${taskId}_${subtaskId}_caregiver.jpg"
                    Log.d("commentSubtask", "→ Uploading new image to $newPath")

                    uploadImageToStorage(newUri, newPath,
                        onSuccess = {
                            Log.d("commentSubtask", "Image uploaded successfully.")
                            updatedSubtask = updatedSubtask.copy(caregiverImgStorageLocation = newPath)
                            subtaskRef.set(updatedSubtask)
                                .addOnSuccessListener {
                                    Log.d("commentSubtask", "Subtask updated with new image.")
                                    onSuccess()
                                }
                                .addOnFailureListener {
                                    Log.e("commentSubtask", "Error saving subtask with new image", it)
                                    onError(it)
                                }
                        },
                        onError = {
                            Log.e("commentSubtask", "Upload image failed", it)
                            onError(it)
                        }
                    )
                }

                // CASE 3: Sostituzione immagine
                oldImg.isNotBlank() && newUri.isNotBlank() && newUri != oldImg -> {
                    Log.d("commentSubtask", "→ Replacing image. Deleting old image first.")

                    storage.getReference(oldImg).delete().addOnCompleteListener {
                        val newPath = "$storageLocation/${taskId}_${subtaskId}_caregiver.jpg"
                        Log.d("commentSubtask", "Uploading new image to: $newPath")

                        uploadImageToStorage(newUri, newPath,
                            onSuccess = {
                                Log.d("commentSubtask", "New image uploaded. Updating subtask.")
                                updatedSubtask = updatedSubtask.copy(caregiverImgStorageLocation = newPath)
                                subtaskRef.set(updatedSubtask)
                                    .addOnSuccessListener {
                                        Log.d("commentSubtask", "Subtask updated with replaced image.")
                                        onSuccess()
                                    }
                                    .addOnFailureListener {
                                        Log.e("commentSubtask", "Error saving updated subtask", it)
                                        onError(it)
                                    }
                            },
                            onError = {
                                Log.e("commentSubtask", "Upload failed after deletion", it)
                                onError(it)
                            }
                        )
                    }
                }

                // CASE 4: Solo commento aggiornato
                else -> {
                    Log.d("commentSubtask", "→ No image changes. Updating only comment.")
                    subtaskRef.set(updatedSubtask)
                        .addOnSuccessListener {
                            Log.d("commentSubtask", "Subtask updated (only comment).")
                            onSuccess()
                        }
                        .addOnFailureListener {
                            Log.e("commentSubtask", "Error saving comment-only update", it)
                            onError(it)
                        }
                }
            }
        }.addOnFailureListener {
            Log.e("commentSubtask", "Error fetching subtask", it)
            onError(it)
        }
    }

}