package it.uniupo.ktt.ui.firebase

import com.google.firebase.firestore.ListenerRegistration
import it.uniupo.ktt.ui.model.SubTask
import it.uniupo.ktt.ui.model.Task
import it.uniupo.ktt.ui.model.User
import it.uniupo.ktt.viewmodel.UserViewModel

object TaskRepository {

    fun listenToUserTasksChanges(
        userId: String,
        role: String,
        onTasksChanged: (List<Task>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {

        val field = when(role.lowercase()) {
            "employee" -> "employee"
            "caregiver" -> "caregiver"
            else -> throw IllegalArgumentException("Ruolo non supportato: $role")
        }

        return BaseRepository.db.collection("tasks")
            .whereEqualTo(field, userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                    onTasksChanged(tasks)
                }
            }
    }


    fun saveTask(
        task: Task,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        BaseRepository.db.collection("tasks")
            .document(task.id)
            .set(task)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun saveSubtask(
        taskId: String,
        subtask: SubTask,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        BaseRepository.db.collection("tasks")
            .document(taskId)
            .collection("subtasks")
            .document(subtask.id)
            .set(subtask)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

}