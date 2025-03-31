package it.uniupo.ktt.ui.firebase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun getRoleByUserId(): String {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var role by remember { mutableStateOf("Loading...") }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val document = db.collection("users").document(userId).get().await()
                role = document.getString("role").toString()
            } catch (e: Exception) {
                role = "Error loading role"
            }
        }
    }

    return role
}

@Composable
fun getNameSurnameByUserId(): String {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var userName by remember { mutableStateOf("Loading...") }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val document = db.collection("users").document(userId).get().await()
                val name = document.getString("name")?.replaceFirstChar { it.uppercase() } ?: "Unknown"
                val surname = document.getString("surname")?.replaceFirstChar { it.uppercase() } ?: "User"
                userName = "$name $surname"
            } catch (e: Exception) {
                userName = "Error loading name"
            }
        }
    }

    return userName
}
