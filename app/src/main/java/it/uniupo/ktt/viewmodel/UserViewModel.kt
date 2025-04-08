package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.ktt.ui.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    suspend fun getUidByEmail(email: String): String? {
        val db = FirebaseFirestore.getInstance()
        return try {
            val result = db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            result.documents.firstOrNull()?.id
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error getting user UID", e)
            null
        }
    }
}