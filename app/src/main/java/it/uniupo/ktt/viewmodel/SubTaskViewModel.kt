package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.model.SubTask
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

}