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

class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun addTaskAndSubtasks(task: Task, subtasks: List<SubTask>) {
        viewModelScope.launch {
            try {
                db.collection("tasks").document(task.id).set(task).await()

                subtasks.forEach { subtask ->
                    val taskId = task.id
                    val subtaskRef = db.collection("tasks").document(taskId)
                        .collection("subtasks").document(subtask.id)

                    // Upload immagini su Firebase Storage (se ci sono)
                    val updatedSubtask = subtask.copy(
                        descriptionImgStorageLocation = subtask.descriptionImgStorageLocation.takeIf { it.isNotBlank() }
                            ?.let { uploadImageToStorage(it, "${ImageLocationFolders.DESCRIPTION}/${subtask.id}") } ?: "",
                        employeeImgStorageLocation = subtask.employeeImgStorageLocation.takeIf { it.isNotBlank() }
                            ?.let { uploadImageToStorage(it, "${ImageLocationFolders.EMPLOYEE}/${subtask.id}") } ?: "",
                        caregiverImgStorageLocation = subtask.caregiverImgStorageLocation.takeIf { it.isNotBlank() }
                            ?.let { uploadImageToStorage(it, "${ImageLocationFolders.CAREGIVER}/${subtask.id}") } ?: ""
                    )

                    // Salva il subtask aggiornato (con path immagini)
                    subtaskRef.set(updatedSubtask).await()
                }
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error adding task: ${e.message}")
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
                Log.d("TaskViewModel", "Task $taskId rating and comment updated")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error updating task rating and comment: ${e.message}")
            }
        }
    }
}