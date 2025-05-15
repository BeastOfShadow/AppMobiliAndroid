package it.uniupo.ktt.ui.firebase

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.ktt.ui.model.Contact
import it.uniupo.ktt.ui.model.User
import kotlinx.coroutines.tasks.await

object UserRepository {

    fun postUser(
        uid: String,
        user: User,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        BaseRepository.db
            .collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

        // OK
    fun getAllAvatar(
        onSuccess: (List<String>) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        BaseRepository.storage.reference
            .child("avatar")
            .listAll()
            .addOnSuccessListener { avatars ->

                var avatarCount = avatars.items.size
                val avatarUrlList = mutableListOf<String>()

                // "0" AVATAR presenti/ ricevuti
                if (avatarCount == 0) {
                    Log.d("FIRESTORE-DEBUG-AVATAR", "Nessun avatar presente/ ricevuto.")
                    onSuccess(emptyList())
                }

                // "1+" AVATAR presenti/ ricevuti
                avatars.items.forEach { avatar ->
                    avatar.downloadUrl
                        .addOnSuccessListener { uri ->
                            avatarUrlList.add(uri.toString())

                            // Quando hai raccolto tutte le immagini
                            if (avatarUrlList.size == avatarCount) {
                                Log.d("DEBUG-AVATAR", "Lista avatar caricata: $avatarUrlList")
                                onSuccess(avatarUrlList)
                            }
                        }
                        .addOnFailureListener { e ->
                            avatarCount-- //nel caso in cui fallisco diminuisco il Counter per evitare ciclo infinito
                            Log.e("FIRESTORE-DEBUG-AVATAR", "Errore downloadUrl avatar", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE-DEBUG-AVATAR", "Errore caricamento lista avatar", e)
                onError(e)
            }
    }

        // OK
    fun updateUserAvatar(
        newPath : String
    ){
        val currentUid = BaseRepository.currentUid()

        if(currentUid != null){
            BaseRepository.db.collection("users")
                .document(currentUid)
                .update("avatar", newPath)
                .addOnSuccessListener {
                    Log.d("DEBUG-USER-REPOSITORY", "Avatar aggiornato con successo")
                }
                .addOnFailureListener { e ->
                    Log.e("DEBUG-USER-REPOSITORY", "Errore aggiornamento avatar", e)
                }
        }
        else {
            Log.e("DEBUG-USER-REPOSITORY", "UID corrente nullo")
        }

    }

        // OK
    fun getUserPointsByUid(
        uid: String,
        onSuccess: (Int) -> Unit,
        onError: (Exception) -> Unit
    ) {
        BaseRepository.db.collection("users")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                val document = result.documents.firstOrNull()
                if (document != null) {
                    val userPoints = document.getLong("userPoint")?.toInt() ?: 0
                    onSuccess(userPoints)
                } else {
                    onError(Exception("Utente non trovato"))
                }
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG", "Errore recupero punti utente", e)
                onError(e)
            }
    }


    @Composable
    fun getEmployeeName(uid: String): String {
        var employeeName by remember(uid) { mutableStateOf("Loading...") }
        val db = FirebaseFirestore.getInstance()

        LaunchedEffect(uid) {
            try {
                val document = db.collection("users").document(uid).get().await()
                val name = document.getString("name")?.replaceFirstChar { it.uppercase() } ?: "Unknown"
                val surname = document.getString("surname")?.replaceFirstChar { it.uppercase() } ?: "User"
                employeeName = "$name $surname"
            } catch (e: Exception) {
                employeeName = "Error loading name"
            }
        }

        return employeeName
    }
}
