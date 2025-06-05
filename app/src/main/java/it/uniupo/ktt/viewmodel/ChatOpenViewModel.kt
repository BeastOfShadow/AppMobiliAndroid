package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.ChatRepository
import it.uniupo.ktt.ui.firebase.ChatUtils
import it.uniupo.ktt.ui.firebase.UserRepository
import it.uniupo.ktt.ui.model.Chat
import it.uniupo.ktt.ui.model.Message
import it.uniupo.ktt.ui.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.Date


@HiltViewModel
class ChatOpenViewModel @Inject constructor() : ViewModel() {

    // PersonalInfo
    var userRole: String = "unknown"

    // ContactUser + avatarUrl + deviceToken
    var contactUser = mutableStateOf<User?>(null)
    var avatarUrl = mutableStateOf<String?>(null)
    var isLoadingContact = mutableStateOf(false)

    private var uidContact: String = ""

    /*
    * Saved Chat:
    *   se sono in "ChatOpen" con "ChatId == notFound"...
    *   dopo il post del primo message, devo ricordarmi che ora ho una Chat creata.
    *   Dato che non voglio ri-creare una nuova (uguale) chat per ogni nuovo messaggio
    *   mi salvo l'id della nuova chat Creata qui.
    */
    var savedChatId = mutableStateOf("notFound")


    // Osservabili Lista MESSAGGI
    private val _messageList = MutableStateFlow<List<Message>>(emptyList())
    val messageList: StateFlow<List<Message>> = _messageList.asStateFlow()

    private val _isLoadingMessages = MutableStateFlow(false)
    val isLoadingMessages: StateFlow<Boolean> = _isLoadingMessages.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    // LOADING UNIFICATO (loadMessages + setUidContact)
    val isLoading = derivedStateOf {
        isLoadingContact.value || _isLoadingMessages.value
    }


    // Listener REF per ascoltare il DB_RealTime
    private var messageListener: ValueEventListener? = null
    private var chatIdInUse: String? = null

    fun getRoleByUid(uid: String) {
        UserRepository.getUserByUid(
            uid,
            onSuccess = { user ->
                userRole = user?.role ?: "unknown"
                // Log.d("DEBUG", "Ruolo utente loggato: $userRole")
            },
            onError = { error ->
                Log.e("DEBUG", "Errore nel recupero del ruolo utente: ${error.message}")
            }
        )
    }

    fun updateChatSession(chatId: String) {

        val uid = BaseRepository.currentUid()!!
        ChatRepository.updateChatLastOpened(chatId, uid, Timestamp.now())
    }


    /*
        la Fun effettua la CREAZIONE DI UN LISTENER al DB REALTIME:
        se la chatId esiste CREO LISTENER, all'uscita (popBackStack()) viene CHIUSO.
        Il Listener crea una connessione WebSocket client-DB, per tutto il suo tempo
        di vita avrò un response RealTime.

        in sintesi, scarica tutti i messaggi di una CHatID e genera un RealTime Listener
        e finchè sono in ChatOpen ho messaggi in RealTime responsive, appena esco viene chiuso
        il Listener. Il Listener viene creato solo se "ChatId != null" e vengono evitati possibili
        duplicati tramite "chatIdInUse"

        NB: si usa ad esigenza, per evitare impatto su RAM etc
    */
    fun loadMessages(chatId: String) {
        _isLoadingMessages.value = true

        // se la chat non esiste non creo il LISTENER
        if (chatId == "notFound") {
            Log.d("DEBUG", "Chat non esistente, LISTENER non creato.")
            return

            // nessun problema: _listMessage rimane VUOTO
        }
        // -> richiamando loadMessage evito LISTENER DUPLICATO
        if(chatIdInUse == chatId) {
            Log.d("DEBUG", "Listener già creato in precedenza per la chat $chatId")
            return
        }
        else{
            chatIdInUse = chatId
        }

        val dbRef = BaseRepository.dbRealTime.child("messages").child(chatId)

        messageListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // IMPORTANTE : Se il nodo non esiste ancora, set lista vuota e chiudi loading altrimenti ciclo wait infinito
                if (!snapshot.exists()) {
                    Log.d("DEBUG", "Nessun nodo messaggi esistente per questa chat.")
                    _messageList.value = emptyList()
                    _isLoadingMessages.value = false
                    return
                }

                val messages = mutableListOf<Message>()
                snapshot.children.forEach { child ->
                    try {
                        val message = child.getValue(Message::class.java)
                        if (message != null) {
                            messages.add(message)
                        }
                    } catch (e: Exception) {
                        Log.e("DEBUG", "Errore nella deserializzazione del messaggio: ${e.message}")
                    }
                }
                _messageList.value = messages.sortedBy { it.timeStamp }
                _isLoadingMessages.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _messageList.value = emptyList()
                _errorMessage.value = error.message
                _isLoadingMessages.value = false
            }
        }

        // Apertura WebSocket Listener(app) to DB
        dbRef.addValueEventListener(messageListener as ValueEventListener)
    }

        // OK
    fun sendMessage(
            chatId: String,
            text: String,
            deviceToken: String,
            senderName: String
    ) {
        val senderUid = BaseRepository.currentUid() ?: return

        val message = Message(
            sender = senderUid,
            text = text,
            seen = false,
            timeStamp = System.currentTimeMillis()
        )

        // Chat esistente sin dall'inizio -> OK
        if(chatId != "notFound") {

            // POST MSG + UPDATE CHAT FIELDS
            ChatRepository.sendMessageRealtime(
                chatId = chatId,
                message = message,
                onSuccess = {
                    Log.d("ChatOpenViewModel", "Messaggio inviato con successo.")

                    ChatRepository.updateChatByChatId(
                        chatId = chatId,
                        message = message,
                        onSuccess = {
                            Log.d("DEBUG", "Chat Aggiornata con successo (ChatOpenViewModel-> caso 1).")

                            // PUSH NOTIFICATION -> caso 1 (chat pre-esistente)
                            sendPushNotification(deviceToken, senderName, text)
                        },
                        onError = { error ->
                            Log.e("DEBUG", "Errore durante l'UPDATE della Chat: ${error.message} (ChatOpenViewModel-> caso 1)")
                            _errorMessage.value = error.message
                        }
                    )
                },
                onError = { error ->
                    Log.e("ChatOpenViewModel", "Errore durante l'invio del messaggio: ${error.message}")
                    _errorMessage.value = error.message
                }
            )
        }
        else{

            // chat già creata durante la permanenza nella ChatOpen
            if(savedChatId.value != "notFound"){

                // POST MSG + UPDATE CHAT FIELDS
                ChatRepository.sendMessageRealtime(
                    chatId = savedChatId.value,
                    message = message,
                    onSuccess = {
                        Log.d("ChatOpenViewModel", "Messaggio inviato con successo.")

                        ChatRepository.updateChatByChatId(
                            chatId = savedChatId.value,
                            message = message,
                            onSuccess = {
                                Log.d("DEBUG", "Chat Aggiornata con successo (ChatOpenViewModel-> caso 2).")

                                // PUSH NOTIFICATION -> caso 2 (chat creata durante la permanenza nella chatOpen)
                                sendPushNotification(deviceToken, senderName, text)
                            },
                            onError = { error ->
                                Log.e("DEBUG", "Errore durante l'UPDATE della Chat: ${error.message} (ChatOpenViewModel-> caso 2)")
                                _errorMessage.value = error.message
                            }
                        )
                    },
                    onError = { error ->
                        Log.e("ChatOpenViewModel", "Errore durante l'invio del messaggio: ${error.message}")
                        _errorMessage.value = error.message
                    }
                )
            }

            // chat non esistente in generale, devo fare tutto -> OK
            else{

                // crea newChat (in base al Role) poi updateChatId
                val currentUid = BaseRepository.currentUid()!!

                // UnreadMessage Manager
                val now = Timestamp.now() // -> mio
                val oneSecondBefore = Timestamp(Date(now.toDate().time - 1000)) // -> contatto

                val lastOpenedMap = mapOf(
                    currentUid to now,  // my lastOpenedChat
                    uidContact to oneSecondBefore   // contact lastOpenedChat
                )

                val newChat = if (userRole.lowercase() == "caregiver") {
                    Chat(
                        caregiver = currentUid,
                        employee = uidContact,
                        members = listOf(currentUid, uidContact),
                        lastOpenedBy = lastOpenedMap
                    )
                } else {
                    Chat(
                        caregiver = uidContact,
                        employee = currentUid,
                        members = listOf(currentUid, uidContact),
                        lastOpenedBy = lastOpenedMap
                    )
                }

                // NEW CHAT + ...
                ChatRepository.postNewChat(
                    newChat = newChat,
                    onSuccess = { newChatId ->
                        //Log.d("ChatOpenViewModel", "Messaggio inviato con successo.")
                        savedChatId.value = newChatId

//  --------------------------------- GENERATE LISTENER ----------------------------
                        loadMessages(savedChatId.value)
//  --------------------------------------------------------------------------------

                        // POST MSG + UPDATE CHAT FIELDS
                        ChatRepository.sendMessageRealtime(
                            chatId = savedChatId.value,
                            message = message,
                            onSuccess = {
                                Log.d("ChatOpenViewModel", "Messaggio inviato con successo.")

                                ChatRepository.updateChatByChatId(
                                    chatId = newChatId,
                                    message = message,
                                    onSuccess = {
                                        Log.d("DEBUG", "Chat Aggiornata con successo (ChatOpenViewModel-> caso 3).")

                                        // PUSH NOTIFICATION -> caso 3 (chat ancora non esistente in generale)
                                        sendPushNotification(deviceToken, senderName, text)
                                    },
                                    onError = { error ->
                                        Log.e("DEBUG", "Errore durante l'UPDATE della Chat: ${error.message} (ChatOpenViewModel-> caso 3)")
                                        _errorMessage.value = error.message
                                    }
                                )
                            },
                            onError = { error ->
                                Log.e("ChatOpenViewModel", "Errore durante l'invio del messaggio: ${error.message}")
                                _errorMessage.value = error.message
                            }
                        )
                    },
                    onError = { error ->
                        Log.e(
                            "ChatOpenViewModel",
                            "Errore durante l'invio del messaggio: ${error.message}"
                        )
                        _errorMessage.value = error.message
                    }
                )
            }

        }


    }


    // PUSH notification
    private fun sendPushNotification(
        deviceToken: String,
        senderName: String,
        text: String
    ) {
        val json = JSONObject().apply {
            put("token", deviceToken)
            put("title", "Message from $senderName")
            put("body", text)
        }

        val requestBody = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://us-central1-keepthetime-69c7a.cloudfunctions.net/sendFCM")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("DEBUG-FCM", "Errore di rete durante l'invio notifica: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("DEBUG-FCM", "Notifica inviata con successo")
                } else {
                    Log.e("DEBUG-FCM", "Errore HTTP: ${response.code}, messaggio: ${response.body?.string()}")
                }
            }
        })
    }


    // OK
    override fun onCleared() {
        super.onCleared()
        // Distruzione del Listener in seguito all'uscita dalla pagina
        if (chatIdInUse != null && messageListener != null) {
            val dbRef = BaseRepository.dbRealTime.child("messages").child(chatIdInUse!!)
            dbRef.removeEventListener(messageListener as ValueEventListener)
            Log.d("DEBUG", "Listener ELIMINATO!.")
        }
    }

        // OK (setta uidContact + get User&AvatarUserUrl)
    fun setUidContact(uid: String){
        uidContact = uid
        isLoadingContact.value = true

        Log.d("DEBUG", "UIDContact SET ricevuto: $uid")

        //
        ChatUtils.getUserAndAndAvatarByUid(
            uidUser = uid,
            onSuccess = { user, url ->
                contactUser.value = user
                avatarUrl.value = url

                isLoadingContact.value = false
            },
            onError = {
                Log.e("DEBUG", "Errore nel recupero avatar/user", it)

                isLoadingContact.value = false
            }
        )
    }

}