package it.uniupo.ktt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.ChatRepository
import it.uniupo.ktt.ui.firebase.ChatUtils
import it.uniupo.ktt.ui.model.Chat
import it.uniupo.ktt.ui.model.EnrichedChat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : ViewModel(){

    // LISTA CHATS GLOBALE
    private val _userChatsList = MutableStateFlow<List<Chat>>(emptyList())
    val userChatsList: StateFlow<List<Chat>> = _userChatsList.asStateFlow()

    // LISTA CHAT GLOBALE ARRICCHITA (nome, cognome, avatar)
    private val _enrichedUserChatsList = MutableStateFlow<List<EnrichedChat>>(emptyList())
    val enrichedUserChatsList: StateFlow<List<EnrichedChat>> = _enrichedUserChatsList.asStateFlow()

    // Wait Observable for ChatLoadingList
    private val _isLoadingEnrichedChats = MutableStateFlow(false)
    val isLoadingEnrichedChats: StateFlow<Boolean> = _isLoadingEnrichedChats.asStateFlow()


                                // LISTENER CHATS GLOBALE

    private var chatListener: ListenerRegistration? = null

    fun observeUserChats(uid: String) {
        _isLoadingEnrichedChats.value = true
        chatListener?.remove() // in caso sia già attivo

        chatListener = ChatRepository.listenToUserChatsChanges(
            userId = uid,

            /*  ad ogni cambiamento nel Db delle chats viene richiamata la CallBack
             *  "onChatChanges" che aggiorna la Lista Globale Locale "_userChatsList"
             *  che poi verrà ri-arricchita e salvata in "_enrichedUserChatsList".
             *
             *  NB: attualmente per ogni cambiamento viene riaggiornata e riarricchita
             *      tutta la lista delle chat (update NON MIRATO)
             */
            onChatChanged = { updatedChats ->

                // 1) Salva Lista Chats
                _userChatsList.value = updatedChats

                // 2) Arricchisci Lista Chats solo se non vuota
                if(updatedChats.isEmpty()){
                    _enrichedUserChatsList.value = emptyList()
                    _isLoadingEnrichedChats.value = false
                }
                else{
                    val currentUid = BaseRepository.currentUid()
                    if(currentUid != null){
                        enrichAllChats(updatedChats, currentUid)
                    }
                }


            },
            onError = {
                Log.e("ChatListener", "Errore nel listener delle chat: ${it.message}")
            }
        )
    }

    //OK
    fun enrichAllChats(chats: List<Chat>, uid : String){

        if (chats.isEmpty()) {
            _enrichedUserChatsList.value = emptyList()
            _isLoadingEnrichedChats.value = false
            return
        }

        // 1) Lista Chats arricchita con "name", "surname", "avatarUrl" del contatto
        val chatsListEnriched = mutableListOf<EnrichedChat>()

        // Counter
        var completedCalls = 0
        // Check
        fun checkIfAllDone() {
            completedCalls++
            if (completedCalls == chats.size) {
                _enrichedUserChatsList.value = chatsListEnriched.toList()
                _isLoadingEnrichedChats.value = false
            }
        }

        chats.forEach { chat ->

            // arricchisco con le info dell'altro utente
            val otherUid = if (uid == chat.caregiver) chat.employee else chat.caregiver

            ChatUtils.getUserAndAndAvatarByUid(
                uidUser = otherUid,
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

    // OK
    fun searchChatByUid(uid: String): EnrichedChat? {
        return _enrichedUserChatsList.value.firstOrNull { enrichedChat ->
            Log.d("DEBUG", "Chat trovata ${enrichedChat.chat.chatId}")
            (enrichedChat.chat.employee == uid || enrichedChat.chat.caregiver == uid)
        }
    }

    fun stopObservingChats() {
        chatListener?.remove()
        chatListener = null
    }

}