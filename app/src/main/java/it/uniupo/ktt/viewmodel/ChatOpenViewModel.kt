package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.ChatRepository
import it.uniupo.ktt.ui.firebase.ChatUtils
import it.uniupo.ktt.ui.model.Chat
import it.uniupo.ktt.ui.model.Message
import it.uniupo.ktt.ui.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatOpenViewModel @Inject constructor() : ViewModel() {

    // User + avatarUrl
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Listener REF per ascoltare il DB_RealTime
    private var messageListener: ValueEventListener? = null
    private var chatIdInUse: String? = null


    /*
        la Fun effettua la CREAZIONE DI UN LISTENER al DB REALTIME:
        se la chatId esiste CREO LISTENER, all'uscita (popBackStack()) viene CHIUSO.
        Il Listener crea una connessione WebSocket client-DB, per tutto il suo tempo
        di vita avrò un response RealTime.

        NB: si usa ad esigenza, per evitare impatto su RAM etc
    */
    fun loadMessages(chatId: String) {
        _isLoading.value = true

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
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _messageList.value = emptyList()
                _errorMessage.value = error.message
                _isLoading.value = false
            }
        }

        // Apertura WebSocket Listener(app) to DB
        dbRef.addValueEventListener(messageListener as ValueEventListener)
    }

        // OK
    fun sendMessage(chatId: String, text: String) {
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

                // crea newChat, updateChatId
                val newChat = Chat(
                    caregiver = BaseRepository.currentUid()!!,
                    employee = uidContact
                )

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

        // OK
    fun setUidContact(uid: String){
        uidContact = uid
        isLoadingContact.value = true

        Log.d("DEBUG", "UIDContact SET ricevuto: $uid")


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