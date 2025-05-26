package it.uniupo.ktt.ui.firebase

import it.uniupo.ktt.ui.model.SubTask
import it.uniupo.ktt.ui.model.Task

object TaskRepository {

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