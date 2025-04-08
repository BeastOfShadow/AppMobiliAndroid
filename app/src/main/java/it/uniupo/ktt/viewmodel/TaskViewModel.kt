package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.uniupo.ktt.ui.firebase.BaseRepository.db
import it.uniupo.ktt.ui.model.SubTask
import it.uniupo.ktt.ui.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun addTaskAndSubtasks(task: Task, /* subtasks: List<SubTask> */) {
        viewModelScope.launch {
            try {
                val documentReference = db.collection("tasks").add(task).await()
                val taskId = documentReference.id
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error adding task: ${e.message}")
            }
        }
    }
}