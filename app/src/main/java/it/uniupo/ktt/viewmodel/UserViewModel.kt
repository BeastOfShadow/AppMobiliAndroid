package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.model.Contact
import it.uniupo.ktt.ui.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow() // Osservabile dai @Composable

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Avatar
    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl.asStateFlow()


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



        // OK
    // (ottieni l'avatar attuale : downloadUrl + Coil )
    fun loadUserByUid(uid: String) {
        _isLoading.value = true

        BaseRepository.db.collection("users")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                val document = result.documents.firstOrNull()

                // USER ricevuto
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    _user.value = user
                    Log.d("DEBUG", "USER RICEVUTO: ${_user.value}")

                    // controllo "user" e "avatar" -> crash se effettuo una ricerca con path vuoto
                    if (user != null && user.avatar.isNotBlank()) {
                        /*
                        *   Chiamata a STORAGE per ottenere il "DownloadUrl" dell'immagine
                        *   del Path salvato nello USER. Notiamo che è ancora un rif. deve
                        *   ancora subire una traformazione
                        */
                        BaseRepository.storage.reference
                        .child(user.avatar)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.d("DEBUG-AVATAR", "Url Avatar non trovato: $uri")
                            _avatarUrl.value = uri.toString()
                            _isLoading.value = false
                        }
                        .addOnFailureListener {
                            Log.e("DEBUG-AVATAR", "Url Avatar non trovato")
                            _avatarUrl.value = null
                            _isLoading.value = false
                        }
                    } else {
                        _avatarUrl.value = null
                        _isLoading.value = false
                    }

                }
                // USER non ricevuto
                else {
                    Log.e("DEBUG", "Errore recupero utente")
                    _errorMessage.value = "User not found"
                    _isLoading.value = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG", "Errore recupero utente", e)
                _errorMessage.value = "Errore recupero utente"
                _isLoading.value = false
            }
    }

//    fun loadAllAvatar(){
//
//    }


}