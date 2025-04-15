package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.ChatRepository
import it.uniupo.ktt.ui.model.Chat
import it.uniupo.ktt.ui.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel(){
    /*
    *   "MutableStateFlow" è l'equivalente di "MutableStateOf" ma nativo
    */

    // val Privata modificiabile solo dal ViewModel che mantiene la Lista
    private val _chatList = MutableStateFlow<List<Chat>>(emptyList())
    //versione PUBBLICA SOLO LETTURA (accessibile per i @Composable) della val "_chatList" (".asStateFlow()" -> blocca le modifiche dall'esterno)
    val chatList: StateFlow<List<Chat>> = _chatList.asStateFlow() // Osservabile dai @Composalbe

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    /*
    *    Nuovo meccanismo regolato dal Boolean "_isLoading":
    *       - per ogni nuova chiamata diventa "true" (in attesa dell'arrivo data)
    *       - quando arrivano i data, update ValPriv (con conseguente trigger UI che osserva), e flag messo "false"
    *
    *       NB: è necessario per sapere se la query è ancora in corso o è terminata
    */
        //OK
    fun loadChats(uid: String) {
        _isLoading.value = true
        ChatRepository.getAllChatsByUid(
            uid,
            onSuccess = { chats ->
                Log.d("DEBUG", "Trovate ${chats.size} chats dato uid: $uid")
                Log.d("DEBUG", "Lista Chats: ${chats.joinToString(separator = "\n")}")

                addNamesToChats(chats)
            },
            onError = { error ->
                _chatList.value = emptyList()  // Best practise -> se ho errore pulisco la lista
                _errorMessage.value = error.message
                _isLoading.value = false
            }
        )
    }

        //OK
    fun addNamesToChats(chats: List<Chat>){
        // Lista Chats con aggiunta di "name" e "Surname"
        val chatsListEnriched = mutableListOf<Chat>()

        // Counter
        var completedCalls = 0
        // Check
        fun checkIfAllDone() {
            completedCalls++
            if (completedCalls == chats.size) {
                _chatList.value = chatsListEnriched.toList()
                _isLoading.value = false
            }
        }

        chats.forEach { chat ->
            getUserByUid(
               chat.employee,
                onSuccess = { user ->

                    if(user!= null){

                        // impreziosire la lista
                        val chatEnriched = chat.copy(
                            name = user.name,
                            surname = user.surname
                        )

                        // aggiungo a lista temporanea la chat arricchita
                        chatsListEnriched.add(chatEnriched)
                    }
                    else{
                        //aggiungo la chat normale in caso di user == null
                        chatsListEnriched.add(chat)
                    }

                    // check
                    checkIfAllDone()
                },
                onError = { e ->
                    Log.e("DEBUG", "Errore durante l'aggiunta su lista: ${e.message}")

                    // check (anche se alcune call andassero male)
                    checkIfAllDone()
                }

           )
        }

    }

        //OK
    fun getUserByUid(
        uidUser: String,
        onSuccess: (User?) -> Unit= {},
        onError: (Exception) -> Unit = {}
    ){
        BaseRepository.db
            .collection("users")
            .whereEqualTo("uid", uidUser)
            .get()
            .addOnSuccessListener { snapshot -> //ritorna contatto
                val userFound = snapshot.documents.firstOrNull()?.toObject(User::class.java)
                //Log.d("DEBUG", "User trovato $userFound")
                onSuccess(userFound)
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG", "Errore durante la query getUserByUid", e)
                onError(e)
            }
    }

        // OK
    fun searchChatByUidEmployee(uid: String): Chat? {
        return _chatList.value.firstOrNull { chat ->
            Log.d("DEBUG", "Chat trovata ${chat.chatId}")
            chat.employee == uid
        }
    }

}