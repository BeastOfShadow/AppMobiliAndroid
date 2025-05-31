package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.UserRepository
import it.uniupo.ktt.ui.model.Contact
import it.uniupo.ktt.ui.model.User
import it.uniupo.ktt.ui.roles.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    val auth = BaseRepository.auth

                                    // USER
    // User
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow() // Osservabile dai @Composable

    private val _isLoadingUser = MutableStateFlow(false)
    val isLoadingUser: StateFlow<Boolean> = _isLoadingUser.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // UserAvatar
    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl.asStateFlow()

                                 // AVATAR-LIST
    // AvatarList
    private val _avatarUrlsList = MutableStateFlow<List<String>>(emptyList())
    val avatarUrlsList: StateFlow<List<String>> = _avatarUrlsList.asStateFlow()

    private val _isLoadingAvatars = MutableStateFlow(false)
    val isLoadingAvatar: StateFlow<Boolean> = _isLoadingAvatars.asStateFlow()


        // OK
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


                                // REGISTER SCREEN

        // GET DeviceToken + Post NewUser
    fun postNewUser(
        email: String,
        name: String,
        surname: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val uid = auth.currentUser?.uid

                    if (uid != null) {

                        // PASSO 1) : ottieni DEVICE TOKEN (FCM)
                        FirebaseMessaging.getInstance().token
                            .addOnCompleteListener { tokenTask ->
                                if (tokenTask.isSuccessful) {
                                    val token = tokenTask.result ?: ""


                                    // PASSO 2) : crea User e Post on DB
                                    val user = User(
                                        uid = uid,
                                        email = email.lowercase(),
                                        role = UserRole.EMPLOYEE.toString(),
                                        name = name.lowercase().replaceFirstChar { it.uppercase() },
                                        surname = surname.lowercase().replaceFirstChar { it.uppercase() },
                                        avatar = "avatar/Screenshot 2025-04-29 alle 17.42.53.png",
                                        userPoint = 0,
                                        deviceToken = token
                                    )

                                    UserRepository.postUser(
                                        uid = uid,
                                        user = user,
                                        onSuccess = onSuccess,
                                        onFailure = {
                                                e -> onError("Failed to save user: ${e.localizedMessage}")
                                        }
                                    )
                                }
                                else{
                                    onError("Impossibile ottenere il device token: ${tokenTask.exception?.message}")
                                }
                            }

                    }

                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error occurred"
                    onError(errorMessage)
                }
            }
    }


                                // LOGIN SCREEN

    /*
    *   LOGIN & Update DeviceToken (update che faccio sempre nel caso in cui cambi
    *   faccio direttamente così per risparmiare call a DB:
    *
    *               solitamente si dovrebbe fare GET al DB per verificare se il DeviceToken è uguale
    *               e se è cambiato fare un update, invece così faccio sempre massimo 1 call a DB
    */
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        // PASSO 1) : LOGIN con Auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    // PASSO 2) : ottieni DEVICE TOKEN (FCM) per fare l'update nel DB
                    FirebaseMessaging.getInstance().token
                        .addOnCompleteListener { tokenTask ->

                            val currentUid = BaseRepository.currentUid()
                            val token = tokenTask.result ?: ""

                            if(currentUid != null){
                                UserRepository.updateDeviceToken(
                                    uid = currentUid,
                                    token = token,
                                    onSuccess = {
                                        Log.d("Login", "Token FCM aggiornato.")
                                        onSuccess()
                                    },
                                    onError = { e ->
                                        Log.e("Login", "Errore aggiornamento Token: ${e.message}")
                                        onSuccess() // Continua comunque
                                    }
                                )
                            }

                        }

                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error occurred"
                    onError(errorMessage)
                }
            }
    }


                                // HOME SCREEN
        // OK
    // (ottieni User + UserAvatar(downloadUrl + Coil) )
    fun loadUserByUid(uid: String) {
        _isLoadingUser.value = true

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
                            Log.d("DEBUG-AVATAR", "Url Avatar trovato: $uri")
                            _avatarUrl.value = uri.toString()
                            _isLoadingUser.value = false
                        }
                        .addOnFailureListener {
                            Log.e("DEBUG-AVATAR", "Url Avatar non trovato")
                            _avatarUrl.value = null
                            _isLoadingUser.value = false
                        }
                    }
                    else {
                        _avatarUrl.value = null
                        _isLoadingUser.value = false
                    }

                }
                // USER non ricevuto
                else {
                    Log.e("DEBUG", "Errore recupero utente")
                    _errorMessage.value = "User not found"
                    _isLoadingUser.value = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG", "Errore recupero utente", e)
                _errorMessage.value = "Errore recupero utente"
                _isLoadingUser.value = false
            }
    }

        // OK
    fun loadAllAvatars() {
        _isLoadingAvatars.value = true

        UserRepository.getAllAvatar(
            onSuccess = { listAvatarUrls ->
                _avatarUrlsList.value = listAvatarUrls
                _isLoadingAvatars.value = false
            },
            onError = {
                _isLoadingAvatars.value = false
            }
        )
    }

        // OK
    fun updateAvatar(newAvatarUrl: String) {
        // Update Locale
        _avatarUrl.value = newAvatarUrl

        // Update FireStoreDB (decodifica Url per ottenere il Name del percorso)
        val decodedUrl = URLDecoder.decode(newAvatarUrl, "UTF-8")
        val fileName = decodedUrl.substringAfterLast("/").substringBefore("?")
        val path = "avatar/$fileName"
        //Log.d("DEBUG", "Nome del PATH: $path")

        UserRepository.updateUserAvatar(path)
    }

}