package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
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
}