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


    // -------------------------------- ASCOLTO GLOBALE (updated CHAT) ---------------------------------
    private val _highlightedChat = MutableStateFlow<EnrichedChat?>(null)
    val highlightedChat: StateFlow<EnrichedChat?> = _highlightedChat.asStateFlow()
    // -------------------------------- ASCOLTO GLOBALE (updated CHAT) ---------------------------------


    // -------------------------------- ENRICHED CHAT GLOBALE  -----------------------------------------
    private val _userChatsList = MutableStateFlow<List<Chat>>(emptyList()) // LISTA CHATS GLOBALE
    val userChatsList: StateFlow<List<Chat>> = _userChatsList.asStateFlow()

    private val _enrichedUserChatsList = MutableStateFlow<List<EnrichedChat>>(emptyList()) // LISTA CHAT GLOBALE ARRICCHITA (nome, cognome, avatar)
    val enrichedUserChatsList: StateFlow<List<EnrichedChat>> = _enrichedUserChatsList.asStateFlow()

    private val _isLoadingEnrichedChats = MutableStateFlow(false)     // Wait Flag (caricamento EnrichedChats)
    val isLoadingEnrichedChats: StateFlow<Boolean> = _isLoadingEnrichedChats.asStateFlow()
    // -------------------------------- ENRICHED CHAT GLOBALE  -----------------------------------------




    // -------------------------------- LISTENER CHAT GLOBALE  -----------------------------------------
    private var chatListener: ListenerRegistration? = null

        // OK
    fun observeUserChats(uid: String) {

        // Evito creazione multipla del Listener
        if (chatListener != null) {
            Log.d("Lifecycle", "GIA LOGGATO-> Listener già attivo (non creato nuovamente).")
            return
        }

        Log.d("Lifecycle", "LOGIN-> Listener creato.")
        _isLoadingEnrichedChats.value = true

        chatListener = ChatRepository.listenToUserChatsChanges(
            userId = uid,

            /*
             *   1) Ad ogni cambiamento rilevato dal LISTENER delle chats viene richiamata
             *      la CALLBACK "onChatChanged" direttamente dal LISTENER generato nella
             *      fun "listenToUserChatsChanges".
             *
             *  NB: attualmente abbiamo un incredibile Update con Arricchimento MIRATO
             *      che punta al risparmio di risorse, quindi aggiorno & arricchisco esclusivamente
             *      le Liste modificate nel DB
             */
            onChatChanged = { newChatList, changedChats ->

                // Salva NewCHatList (SEMPLICI)
                _userChatsList.value = newChatList

                // ARRICCHIMENTO MIRATO
                // 1) NewCHatList EMPTY -> Svuota tutto
                if(newChatList.isEmpty()){
                    _enrichedUserChatsList.value = emptyList()
                    _isLoadingEnrichedChats.value = false
                }
                // 2) NewCHatList NOT EMPTY
                else{
                    val currentUid = BaseRepository.currentUid()
                    if(currentUid != null){

                        // 2.1) Primo Caricamento (Lista Locale EMPTY, NewCHatList NOT EMPTY)
                        if(_enrichedUserChatsList.value.isEmpty()){
                            enrichAllChats(newChatList, currentUid)

                            Log.d("globalChats", "PRIMO CARICAMENTO (totale):\n" + newChatList.joinToString("\n") {
                                "chatId=${it.chatId}, members=${it.members}, lastMsg=${it.lastMsg}"
                            })
                        }
                        // 2.2.) Post Primo Caricamento, UPDATE MIRATO (Lista Locale e NewCHatList NOT EMPTY)
                        else{

                            // Scorro Lista UpdatedChats -> Arricchisco, immetto nella EnrichedChatList, poi mando UpdateBadge
                            changedChats.forEach { changedChat ->

                                val otherUid = changedChat.members.firstOrNull { it != currentUid } ?: return@forEach

                                ChatUtils.getUserAndAndAvatarByUid(
                                    uidUser = otherUid,
                                    onSuccess = { user, avatar ->
                                        if (user != null) {

                                            // 1) ARRICCHISCI CHAT
                                            val newEnrichedChat = EnrichedChat(
                                                chat = changedChat,
                                                name = user.name,
                                                surname = user.surname,
                                                avatarUrl = avatar ?: ""
                                            )

                                            // 2) SWITCH or ADD della UpdatedEnrichedChat nella attuale ENRICHED CHAT LIST (ricerca mirata tramite ChatID)
                                            val currentList = _enrichedUserChatsList.value.toMutableList()

                                            val index = currentList.indexOfFirst { it.chat.chatId == newEnrichedChat.chat.chatId }

                                            // se esiste la rimpiazzo
                                            if (index >= 0) {
                                                currentList[index] = newEnrichedChat
                                            }
                                            // se non esiste la aggiungo
                                            else {
                                                currentList.add(newEnrichedChat)
                                            }

                                            // aggiornamento
                                            _enrichedUserChatsList.value = currentList
                                            Log.d("globalChats", "UPDATE MIRATO:\n" + currentList.joinToString("\n") {
                                                "chatId=${it.chat.chatId}, members=${it.chat.members}, lastMsg=${it.chat.lastMsg}"
                                            })

                                            // 3) NOTIFICA UPDATE BADGE (FOREGROUND)
                                            if (changedChat.uidLastSender != currentUid) {
                                                notifyNewMessage(newEnrichedChat)
                                            }

                                            _isLoadingEnrichedChats.value = false
                                        }
                                    },
                                    onError = { e ->
                                        Log.e("DEBUG", "Errore arricchimento chat aggiornata: ${e.message}")
                                    }
                                )
                            }
                        }

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

    fun stopObservingChats() {
        chatListener?.remove()
        chatListener = null
    }

    fun notifyNewMessage(chat: EnrichedChat) {
        _highlightedChat.value = chat
    }

    fun clearHighlightedChat() {
        _highlightedChat.value = null
    }
    // -------------------------------- LISTENER CHAT GLOBALE  -----------------------------------------


    // OK
    fun searchChatByUid(uid: String): EnrichedChat? {
        return _enrichedUserChatsList.value.firstOrNull { enrichedChat ->
            Log.d("DEBUG", "Chat trovata ${enrichedChat.chat.chatId}")
            (enrichedChat.chat.employee == uid || enrichedChat.chat.caregiver == uid)
        }
    }



}