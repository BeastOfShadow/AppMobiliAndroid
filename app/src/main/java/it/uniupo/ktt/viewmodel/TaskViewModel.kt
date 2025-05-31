package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
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
import com.google.firebase.storage.FirebaseStorage
import it.uniupo.ktt.ui.firebase.TaskRepository


class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()



        // OK
    fun addTaskAndSubtasks(
        task: Task,
        subtasks: List<SubTask>
    ) {
        TaskRepository.saveTask(
            task = task,
            onSuccess = {

                subtasks.forEach { subTask ->
                    var updated = subTask

                    // SubTask con Foto
                    if (subTask.descriptionImgStorageLocation.isNotBlank()) {

                        val path = "subtaskImages/${task.id}_${subTask.id}.jpg"

                        uploadImageToStorage(
                            localPath = subTask.descriptionImgStorageLocation,
                            storagePath = path,
                            onSuccess = {
                                updated = subTask.copy(descriptionImgStorageLocation = path)
                                TaskRepository.saveSubtask(task.id, updated, {}, {
                                    Log.e("DEBUG", "Errore salvataggio subtask ${updated.id}: ${it.message}")
                                })
                            },
                            onError = {
                                Log.e("DEBUG", "Errore upload img subtask ${subTask.id}: ${it.message}")
                                TaskRepository.saveSubtask(task.id, updated.copy(descriptionImgStorageLocation = ""), {}, {
                                    Log.e("DEBUG", "Errore salvataggio subtask ${updated.id} (dopo upload fallito): ${it.message}")
                                })
                            }
                        )
                    }
                    // SubTask senza Foto
                    else {
                        TaskRepository.saveSubtask(task.id, updated, {}, {
                            Log.e("DEBUG", "Errore salvataggio subtask ${updated.id}: ${it.message}")
                        })
                    }
                }
            },
            onError = {
                Log.e("DEBUG", "Errore salvataggio Task: ${it.message}")
            }
        )
    }

        //  DATO un Subtask, ritorna la sua ImgUrl pronta
    fun getSubtaskImageUrl(
        subtask: SubTask,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val path = subtask.descriptionImgStorageLocation

        if (path.isBlank()) {
            onError(Exception("Path immagine vuoto"))
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference.child(path)
        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    fun getEmployeeImageUrl(
        subtask: SubTask,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val path = subtask.employeeImgStorageLocation

        if (path.isBlank()) {
            onError(Exception("Path immagine vuoto"))
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference.child(path)
        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    fun getCaregiverImageUrl(
        subtask: SubTask,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val path = subtask.caregiverImgStorageLocation

        if (path.isBlank()) {
            onError(Exception("Path immagine vuoto"))
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference.child(path)
        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
            .addOnFailureListener { exception ->
                onError(exception)
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

    fun updateLocation(taskId: String, selectedLatLng: LatLng) {
        viewModelScope.launch {
            try {
                val taskRef = db.collection("tasks").document(taskId)
                val firestoreGeoPoint = com.google.firebase.firestore.GeoPoint(selectedLatLng.latitude, selectedLatLng.longitude)

                taskRef.update("location", firestoreGeoPoint).await()

                Log.d("TaskViewModel", "Task $taskId rating and comment updated")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error updating task rating and comment: ${e.message}")
            }
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        viewModelScope.launch {
            try {
                val taskRef = db.collection("tasks").document(taskId)
                val snapshot = taskRef.get().await()
                val task = snapshot.toObject(Task::class.java)

                val startTimestamp = task?.timeStampStart
                val endTimestamp = com.google.firebase.Timestamp.now()

                val completionTimeActual = if (startTimestamp != null) {
                    (endTimestamp.seconds - startTimestamp.seconds) // differenza in secondi
                } else {
                    null
                }

                val updates = mutableMapOf<String, Any>(
                    "status" to newStatus.toString(),
                    "timeStampEnd" to endTimestamp,
                )
                completionTimeActual?.let {
                    updates["completionTimeActual"] = it
                }

                // Aggiorna il documento
                taskRef.update(updates).await()

                Log.d("TaskViewModel", "✅ Task $taskId status updated to ${newStatus.name} with completion time $completionTimeActual seconds")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "❌ Error updating task status: ${e.message}")
            }
        }
    }

}