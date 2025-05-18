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
import it.uniupo.ktt.ui.firebase.ChatUtils
import it.uniupo.ktt.ui.firebase.UserRepository
import it.uniupo.ktt.ui.model.EnrichedChat
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel(){
    /*
    *   "MutableStateFlow" è l'equivalente di "MutableStateOf" ma nativo
    */

    // val Privata modificiabile solo dal ViewModel che mantiene la Lista
    private val _enrichedChatList = MutableStateFlow<List<EnrichedChat>>(emptyList())
    //versione PUBBLICA SOLO LETTURA (accessibile per i @Composable) della val "_chatList" (".asStateFlow()" -> blocca le modifiche dall'esterno)
    val enrichedChatList: StateFlow<List<EnrichedChat>> = _enrichedChatList.asStateFlow() // Osservabile dai @Composalbe

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


                                        // CHAT PAGE

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
            role = "caregiver",
            onSuccess = { chats ->
                Log.d("DEBUG", "Trovate ${chats.size} chats dato uid: $uid")
                Log.d("DEBUG", "Lista Chats: ${chats.joinToString(separator = "\n")}")

                enrichAllChats(chats)
            },
            onError = { error ->
                _enrichedChatList.value = emptyList()  // Best practise -> se ho errore pulisco la lista
                _errorMessage.value = error.message
                _isLoading.value = false
            }
        )
    }

        //OK
    fun enrichAllChats(chats: List<Chat>){
        // caso emptyList -> set "_isLoading" & return
        if (chats.isEmpty()) {
            _enrichedChatList.value = emptyList()
            _isLoading.value = false
            return
        }


        // Lista Chats con aggiunta di "name" e "Surname"
        val chatsListEnriched = mutableListOf<EnrichedChat>()

        // Counter
        var completedCalls = 0
        // Check
        fun checkIfAllDone() {
            completedCalls++
            if (completedCalls == chats.size) {
                _enrichedChatList.value = chatsListEnriched.toList()
                _isLoading.value = false
            }
        }

        chats.forEach { chat ->
            ChatUtils.getUserAndAndAvatarByUid(
                uidUser = chat.employee,
                onSuccess = { user, downloadUrl ->

                    if(user!= null){

                        // impreziosire la lista
                        val chatEnriched = EnrichedChat(
                            chat = chat.copy(),

                            name = user.name,
                            surname = user.surname,
                            avatarUrl = downloadUrl ?: ""
                        )


                        // aggiungo a lista temporanea la chat arricchita
                        chatsListEnriched.add(chatEnriched)
                    }
                    else{
                        //aggiungo la chat normale in caso di user == null
                        chatsListEnriched.add(EnrichedChat(chat = chat, avatarUrl = ""))
                    }

                    // check
                    checkIfAllDone()
                },
                onError = { e ->
                    Log.e("DEBUG", "Errore durante arricchimento della Chatlist: ${e.message}")

                    // check (anche se alcune call andassero male)
                    checkIfAllDone()
                }

           )
        }

    }


                                        // NEW CHAT PAGE

        // OK
    fun searchChatByUidEmployee(uid: String): EnrichedChat? {
        return _enrichedChatList.value.firstOrNull { enrichedChat ->
            Log.d("DEBUG", "Chat trovata ${enrichedChat.chat.chatId}")
            enrichedChat.chat.employee == uid
        }
    }

}