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

    // -------------------------------- CACHE MSG GLOBALE (Foregoround Notify) ---------------------------------
    private val shownNotifications = mutableSetOf<String>() // cacheNotificationId = chatId + timestamp
    // -------------------------------- CACHE MSG GLOBALE (Foregoround Notify) ---------------------------------

    // --------------------------------- CHAT OPEN STATUS (Foregoround Notify) ---------------------------------
    private val _isInChatOpen = MutableStateFlow(false)
    val isInChatOpen: StateFlow<Boolean> get() = _isInChatOpen
    // --------------------------------- CHAT OPEN STATUS (Foregoround Notify) ---------------------------------


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
            onChatChanged = { newChatList, changedChats, shouldNotify ->

                Log.d("globalChats DEBUG-TEMP", "onChatChanged(): changedChats.size = ${changedChats.size}, enrichedListEmpty = ${_enrichedUserChatsList.value.isEmpty()}")


                // Salva NewCHatList (SEMPLICI-> not Enriched)
                _userChatsList.value = newChatList

                                            // ARRICCHIMENTO MIRATO

                // 1) NewCHatList EMPTY -> Svuota tutto (tutte le chat sono state cancellate o non esistono)
                if(newChatList.isEmpty()){
                    _enrichedUserChatsList.value = emptyList()
                    _isLoadingEnrichedChats.value = false
                }
                // 2) NewCHatList NOT EMPTY
                else{
                    val currentUid = BaseRepository.currentUid()
                    if(currentUid != null){

                        // 2.1) Primo Caricamento (Lista Locale EMPTY, NewCHatList NOT EMPTY) -> (prima volta che compongo il listener o listener svuotato)
                        if(_enrichedUserChatsList.value.isEmpty()){
                            enrichAllChats(newChatList, currentUid)

                            Log.d("globalChats", "PRIMO CARICAMENTO (totale):\n" + newChatList.joinToString("\n") {
                                "chatId=${it.chatId}, members=${it.members}, lastMsg=${it.lastMsg}"
                            })
                        }
                        // 2.2.) Post Primo Caricamento, UPDATE MIRATO (Lista Locale e NewCHatList NOT EMPTY)
                        else{

                            // ------------------ MANAGMENT "_isLoadingEnrichedChats" ------------------
                            val total = changedChats.size
                            var completed = 0

                            fun checkIfAllDone() {
                                completed++
                                Log.e("globalChats", "UPDATE checkIfAllDone counter, Counter: $completed")


                                if (completed == total) {
                                    _isLoadingEnrichedChats.value = false
                                    Log.e("globalChats", "UPDATE isLoadingEnrichedChats to false")

                                }
                            }
                            // ------------------ MANAGMENT "_isLoadingEnrichedChats" ------------------


                            // Scorro Lista UpdatedChats -> Arricchisco, immetto nella EnrichedChatList, poi mando UpdateBadge
                            changedChats.forEach { changedChat ->


                                val otherUid = changedChat.members.firstOrNull { it != currentUid } ?: return@forEach

                                ChatUtils.getUserAndAndAvatarByUid(
                                    uidUser = otherUid,
                                    onSuccess = { user, avatar ->
                                        if (user != null) {
                                            //Log.e("globalChats USER", "User OK")

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


                                            // -------------------------------- NOTIFICA UPDATE BADGE (FOREGROUND) ---------------------------------
                                            // 3) NOTIFICA UPDATE BADGE (FOREGROUND)
                                            /*
                                            *           MOSTRA LA NOTIFICA SOLO SE:
                                            *
                                            *           1 - BASE : l'utente corrente è il destinatario
                                            *           2 - VISUALIZZAZIONE CHAT :
                                            *                   il destinatario non ha ancora mai aperto la chat (nuovo msg da nuova chat) OR
                                            *                   il destinatario ha aperto l'ultima volta la chat prima dell'arrivo del messaggio
                                            *           3 - SHOULD-NOTIFY :
                                            *                   flag sfruttabile, ci dice se il change è avvenuto solo su Sessione (false)
                                            *                   quindi NON notifica OR altrimenti notifica (change su LastMessage)
                                            *           3 - CACHE MESSAGGI :
                                            *                   il destinatario se non vuole aprire la chat con il nuovo messaggio, non vuole ri-
                                            *                   ricevere la notifica se il mandante riapre la chat (viene aggiornata la sessione
                                            *                   che triggera l'update delle chat).
                                            *
                                            *                                       ----> SOLUZIONE: Controllo CACHE messaggi
                                            *
                                            *               Funzionamento CACHE MESSAGES: ad ogni nuovo cambiamento della chat viene triggerato
                                            *               l'update della chat, che triggera poi la notifica, questa volta salviamo in una KEY
                                            *               <ChatID, LastTimeStamp> dell'ultimo change.. e se "!shownNotifications.contains(key)"
                                            *               ovvero la KEY non è ancora presente nella cache allora mostra notifica e aggiungi
                                            *               la KEY alla CACHE.
                                            *
                                            *               NB: non salvo il messaggio ma una KEY ASSOCIATA all'ultimo messaggio il quale ha un
                                            *                   UNIVOCO TIMESTAMP! :)
                                            *
                                            */
                                            val lastOpenedTime = changedChat.lastOpenedBy[currentUid]?.toDate()
                                            val lastMessageTime = changedChat.lastTimeStamp.toDate()

                                            val key = "${changedChat.chatId}_${changedChat.lastTimeStamp.seconds}" // KEY

                                            // GUARD only "lastOpenedBy" List changes -> (don't generate notify)
                                            if (!shouldNotify) {
                                                Log.d("DEBUG-FLAG", "Cambio solo sessione, niente notify.")
                                                return@getUserAndAndAvatarByUid
                                            }

                                            // GUARD se sono in CatOpen -> (don't generate notify)
                                            if (isInChatOpen.value) {
                                                Log.d("DEBUG-FLAG", "Sono in ChatOpen, niente notify.")
                                                return@getUserAndAndAvatarByUid
                                            }

                                            if (
                                                changedChat.uidLastSender != currentUid &&
                                                (lastOpenedTime == null || lastMessageTime.after(lastOpenedTime)) &&
                                                !shownNotifications.contains(key) // check se la KEY appena generata non è nella cache messages
                                            ) {
                                                shownNotifications.add(key)
                                                notifyNewMessage(newEnrichedChat)
                                            }
                                            // -------------------------------- NOTIFICA UPDATE BADGE (FOREGROUND) ---------------------------------


                                            checkIfAllDone()        // _isLoadingEnrichedChats MANAGMENT
                                        }
                                        else{
                                            Log.e("globalChats USER", "User ERROR")
                                        }
                                    },
                                    onError = { e ->
                                        Log.e("globalChats DEBUG", "Errore arricchimento chat aggiornata: ${e.message}")

                                        checkIfAllDone()           // _isLoadingEnrichedChats MANAGMENT
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


                // ------------------------ NOTIFY FIRST CHAT MSG ------------------------
                val currentUid = BaseRepository.currentUid()

                // Trova la newChat con il NewMsg unread
                val firstUnreadChat = chatsListEnriched.firstOrNull { enrichedChat ->
                    val chat = enrichedChat.chat
                    val lastOpened = chat.lastOpenedBy[currentUid]?.toDate()
                    val lastMessage = chat.lastTimeStamp.toDate()
                    val key = "${chat.chatId}_${chat.lastTimeStamp.seconds}"

                    chat.uidLastSender != currentUid &&
                            (lastOpened == null || lastMessage.after(lastOpened)) &&
                            !shownNotifications.contains(key)
                }
                // Se c'è mostralo e aggiungilo alla cache
                firstUnreadChat?.let {
                    val key = "${it.chat.chatId}_${it.chat.lastTimeStamp.seconds}"
                    shownNotifications.add(key)
                    notifyNewMessage(it)
                }
                // ------------------------ NOTIFY FIRST CHAT MSG ------------------------

            }
        }

        chats.forEach { chat ->

            /*
             *     SAFE GUARD:
             *
             *       dato che la FIRST sendMessage è un operazione lunga con più call
             *       (postNewChat(con LastMsg=NULL) + loadMessage + sendMessageRT + updateChatByChatId)
             *       e invece il LISTENER reagisce direttamente alla "postNewChat", viene passata una
             *       Chat da arricchire che non è ancora pronta..
             *
             *       Questa SAFE GUARD serve ad ignorare questi primi aggiornamenti laddove riconosce
             *       una Chat ancora prematura (1 operazione su 4 fatte), evitando così la creazione
             *       di Badge Vuoti.
             *
             */
            if (chat.lastMsg.isBlank()) {
                checkIfAllDone()
                return@forEach
            }

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

        // Pulisco
        _enrichedUserChatsList.value = emptyList()
        _isLoadingEnrichedChats.value = false
    }

    fun notifyNewMessage(chat: EnrichedChat) {
        Log.d("DEBUG-NOTIFY", "EnrichedChat ricevuta:\nchatId = ${chat.chat.chatId}\n" +
                "uidLastSender = ${chat.chat.uidLastSender}\n" +
                "lastMsg = ${chat.chat.lastMsg}\n" +
                "timestamp = ${chat.chat.lastTimeStamp}\n" +
                "lastOpenedBy = ${chat.chat.lastOpenedBy}\n" +
                "name = ${chat.name}\n" +
                "surname = ${chat.surname}\n" +
                "avatarUrl = ${chat.avatarUrl}"
        )

        _highlightedChat.value = chat
    }

    fun clearHighlightedChat() {
        _highlightedChat.value = null
    }

    // -------------------------------- LISTENER CHAT GLOBALE  -----------------------------------------


    // ------------------------------------ CACHE && STATUS --------------------------------------------
    fun clearNotificationCache() {
        shownNotifications.clear()
    }

    fun setChatOpen(isOpen: Boolean) {
        _isInChatOpen.value = isOpen
    }
    // ------------------------------------ CACHE && STATUS --------------------------------------------


    // OK
    fun searchChatByUid(uid: String): EnrichedChat? {
        return _enrichedUserChatsList.value.firstOrNull { enrichedChat ->
            Log.d("DEBUG", "Chat trovata ${enrichedChat.chat.chatId}")
            (enrichedChat.chat.employee == uid || enrichedChat.chat.caregiver == uid)
        }
    }



}