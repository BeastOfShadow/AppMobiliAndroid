package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.uniupo.ktt.imagelocation.ImageLocationFolders
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.BaseRepository.db
import it.uniupo.ktt.ui.model.SubTask
import it.uniupo.ktt.ui.model.Task
import it.uniupo.ktt.ui.storage.uploadImageToStorage
import it.uniupo.ktt.ui.taskstatus.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query


class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()


    fun addTaskAndSubtasks(task: Task, subtasks: List<SubTask>) {
        viewModelScope.launch {
            try {
                Log.d("DEBUG_FIRESTORE", "Salvo task ${task.id}")
                db.collection("tasks")
                    .document(task.id)
                    .set(task)
                    .await()

                for (sub in subtasks) {
                    Log.d("DEBUG_SUBTASK", "Processing subtask ${sub.id}, imgPath=${sub.descriptionImgStorageLocation}")

                    var updated = sub
                    if (sub.descriptionImgStorageLocation.isNotBlank()) {
                        try {
                            uploadImageToStorage(sub.descriptionImgStorageLocation, "${ImageLocationFolders.DESCRIPTION}/${sub.id}")
                            updated = sub.copy(descriptionImgStorageLocation = "${ImageLocationFolders.DESCRIPTION}/${sub.id}")
                        } catch (e: Exception) {
                            Log.e("DEBUG_FIRESTORE", "Upload fallito per subtask ${sub.id}", e)
                            throw e  // temporaneamente rilancio per debug
                        }
                    }

                    try {
                        Log.d("DEBUG_FIRESTORE", "Salvo subtasks/${updated.id}")
                        db.collection("tasks")
                            .document(task.id)
                            .collection("subtasks")
                            .document(updated.id)
                            .set(updated)
                            .await()
                    } catch (e: Exception) {
                        Log.e("DEBUG_FIRESTORE", "Errore salvataggio subtask ${updated.id}", e)
                        throw e
                    }
                }

            } catch (e: Exception) {
                Log.e("TaskViewModel", "Errore generale", e)
                // Qui puoi anche mostrarti un toast o un snackbar per avvertire l'utente
            }
        }
    }


    suspend fun getTasksByStatus(uid: String, status: String): List<Task> {
        val db = BaseRepository.db
        return try {
            val snapshot = db.collection("tasks")
                .whereEqualTo("caregiver", uid)
                .whereEqualTo("status", status)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
        } catch (e: Exception) {
            emptyList() // o throw e
        }
    }

    suspend fun getTasksByEmployeeId(uid: String): List<Task> {
        val db = BaseRepository.db
        return try {
            val snapshot = db.collection("tasks")
                .whereEqualTo("employee", uid)
                .whereIn("status", listOf(TaskStatus.ONGOING.toString(), TaskStatus.COMPLETED.toString()))
                //.orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun startTask(taskId: String) {
        viewModelScope.launch {
            try {
                val taskRef = db.collection("tasks").document(taskId)
                taskRef.update("status", TaskStatus.ONGOING).await()
                Log.d("TaskViewModel", "Task $taskId status updated to in_progress")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error updating task status: ${e.message}")
            }
        }
    }

    fun startTaskEmployee(taskId: String) {
        viewModelScope.launch {
            try {
                val taskRef = db.collection("tasks").document(taskId)
                taskRef.update("active", true).await()
                taskRef.update("timeStampStart", com.google.firebase.Timestamp.now()).await()
                Log.d("TaskViewModel", "Task $taskId status updated to in_progress")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error updating task status: ${e.message}")
            }
        }
    }

    @Composable
    fun getTaskById(taskId: String): Task? {
        val taskState = remember { mutableStateOf<Task?>(null) }

        LaunchedEffect(taskId) {
            try {
                val snapshot = db.collection("tasks").document(taskId).get().await()
                val task = snapshot.toObject(Task::class.java)
                taskState.value = task
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Errore nel recuperare il task: ${e.message}")
            }
        }

        return taskState.value
    }

    @Composable
    fun getSubtasksByTaskId(taskId: String): List<SubTask> {
        val subtasksState = remember { mutableStateOf<List<SubTask>>(emptyList()) }

        LaunchedEffect(taskId) {
            try {
                val snapshot = db.collection("tasks")
                    .document(taskId)
                    .collection("subtasks")
                    .orderBy("listNumber", Query.Direction.ASCENDING)
                    .get()
                    .await()

                val subtasks = snapshot.documents.mapNotNull { doc ->
                    try {
                        val subtask = SubTask(
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
                        Log.d("SubTaskParsed", "✅ $subtask")
                        subtask
                    } catch (e: Exception) {
                        Log.e("SubTaskParseError", "❌ ${e.message}")
                        null
                    }
                }

                subtasksState.value = subtasks
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Errore nel recuperare i subtask: ${e.message}")
            }
        }

        return subtasksState.value
    }

    fun rateTask(taskId: String, comment: String, rating: Int) {
        viewModelScope.launch {
            try {
                val taskRef = db.collection("tasks").document(taskId)
                taskRef.update("overallComment", comment).await()
                taskRef.update("overallRating", rating).await()
                taskRef.update("status", TaskStatus.RATED).await()
                Log.d("TaskViewModel", "Task $taskId rating and comment updated")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error updating task rating and comment: ${e.message}")
            }
        }
    }
}