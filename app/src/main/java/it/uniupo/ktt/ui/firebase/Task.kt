package it.uniupo.ktt.ui.firebase

import it.uniupo.ktt.ui.model.Task
import kotlinx.coroutines.tasks.await

suspend fun getTasksByStatusSuspend(uid: String, status: String): List<Task> {
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
