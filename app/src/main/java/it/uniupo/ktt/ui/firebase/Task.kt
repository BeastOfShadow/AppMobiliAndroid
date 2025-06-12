package it.uniupo.ktt.ui.firebase

import com.google.firebase.firestore.ListenerRegistration
import it.uniupo.ktt.ui.model.SubTask
import it.uniupo.ktt.ui.model.Task

object TaskRepository {

    fun listenToUserTasksChanges(
        userId: String,
        onTasksChanged: (List<Task>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return BaseRepository.db.collection("tasks")
            .whereEqualTo("employee", userId)  // o la proprietà che identifica l'utente
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