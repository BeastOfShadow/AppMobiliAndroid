package it.uniupo.ktt.ui.firebase

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.ListenerRegistration
import it.uniupo.ktt.ui.model.Chat
import it.uniupo.ktt.ui.model.Contact
import it.uniupo.ktt.ui.model.Message
import it.uniupo.ktt.ui.model.User
import kotlinx.coroutines.tasks.await

//realTime-DB (RealTime-DB works with "Long" mentre FireStore-DB works with "timeStamp", we need to convert with Date)
import java.util.Date

object ChatRepository {

    // REF al RealTime DB (SINGLETON del BaseRepository)
    private val realtimeDb = BaseRepository.dbRealTime

        //OK
    // blocca il thread e necessito di coroutine per poterla richiamare ("scope" nei Button & "LaunchedEffect" nei composable)
    suspend fun getUserByEmail(email: String): User?{
        return try {
            val snapshot = BaseRepository.db
                .collection("users")
                .whereEqualTo("email", email.lowercase())
                .get()
                .await()

            // return "User" al completo
            snapshot.documents.firstOrNull()?.toObject(User::class.java)
        }
        catch (e: Exception){
            null
        }
    }

        //OK
    // non blocca il Thread -> usa firestore async, quindi non necessito di coroutine per usarla ma solo di un "LaunchedEffect(){}"
    fun postNewContact(
        newContact: Contact,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        BaseRepository.db
            .collection("contacts")
            .add(newContact)
            .addOnSuccessListener {
                Log.d("Firestore", "Contatto aggiunto con successo")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Errore nell'aggiunta contatto", e)
                onError(e)
            }
    }

        // OK
    fun getAllContactsByUid(
        uid: String,
        onSuccess: (List<Contact>) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        BaseRepository.db
            .collection("contacts")
            .whereEqualTo("uidPersonal", uid)
            .get()
            .addOnSuccessListener { snapshot -> //ritorna una lista di Contact Brooonti
                val contacts = snapshot.documents.mapNotNull { it.toObject(Contact::class.java) }
                Log.d("Firestore", "Trovati ${contacts.size} contatti dato uid: $uid")
                onSuccess(contacts)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Errore durante la query getAllConntactsByUid", e)
                onError(e)
            }
    }

        // OK -> Uid : return all chats
    fun getAllChatsByUid(
        uid: String,
        onSuccess: (List<Chat>) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {

        val allChatsList = mutableListOf<Chat>()

        // First Query -> (caregiver == uid)
        BaseRepository.db
            .collection("chats")
            .whereEqualTo("caregiver", uid)
            .get()
            .addOnSuccessListener { snapshotFirstQuery ->
                allChatsList.addAll(snapshotFirstQuery.documents.mapNotNull { it.toObject(Chat::class.java) })

                // Second QUERY -> (employee == uid)
                BaseRepository.db
                    .collection("chats")
                    .whereEqualTo("employee", uid)
                    .get()
                    .addOnSuccessListener { snapshotSecondQuery ->
                        allChatsList.addAll(snapshotSecondQuery.documents.mapNotNull { it.toObject(Chat::class.java) })

                        onSuccess(allChatsList.distinctBy { it.chatId })
                    }
                    .addOnFailureListener { error ->
                        onError(error)
                    }

            }
            .addOnFailureListener { error ->
                onError(error)
            }


    }

        // OK
    fun postNewChat(
        newChat: Chat,
        onSuccess: (String) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ){
        BaseRepository.db
            .collection("chats")
            .add(newChat)
            .addOnSuccessListener { documentReference ->
                val newChatId = documentReference.id

                // UPDATE chatID by DOCReference.id
                documentReference
                    .update("chatId", newChatId)
                    .addOnSuccessListener {
                        Log.d("DEBUG", "chatId updated, newChatId: $newChatId")
                        onSuccess(newChatId)
                    }
                    .addOnFailureListener { updateException ->
                        Log.e("DEBUG", "chatId errore nel suo Update", updateException)
                        onError(updateException)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG", "Errore nell'aggiunta della Chat", e)
                onError(e)
            }
    }

        // OK
    fun updateChatByChatId(
        chatId: String,
        message: Message,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val updates = mapOf(
            "lastMsg" to message.text,
            "uidLastSender" to message.sender,
            "lastTimeStamp" to Timestamp(Date(message.timeStamp))

        )

        BaseRepository.db
            .collection("chats")
            .document(chatId)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }


                                            // LISTENER CHATS GLOBALE (app Aperta)
        // OK -> Crea un Listener sulla Lista Chats anche se vuota (dato che in futuro potrebbe popolarsi)
    fun listenToUserChatsChanges(
        userId: String,
        onChatChanged: (List<Chat>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {

        return BaseRepository.db.collection("chats")
            .whereArrayContains("members", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val chats = snapshot.documents.mapNotNull { it.toObject(Chat::class.java) }
                    onChatChanged(chats)
                }
            }
    }


                                            // REAL-TIME-DB FUNCTION (app Aperta: solo "ChatOpen")
        // OK
    fun sendMessageRealtime(
            chatId: String,
            message: Message,
            onSuccess: () -> Unit = {},
            onError: (DatabaseError) -> Unit = {}
    ) {

        // generazione del NODO in "message" nel RealTime DB (non posta il message, ma crea Nodo + IDMessage)
        val messageId = realtimeDb.child("messages").child(chatId).push().key

        // Post message nel Nodo con IDMessage generato
        if (messageId != null) {
            realtimeDb.child("messages")
                .child(chatId)
                .child(messageId)
                .setValue(message) // scrittura Message
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e ->
                    if (e is DatabaseError) {
                        onError(e)
                    }
                }
        } else {
            onError(DatabaseError.fromException(Exception("Failed to generate message ID")))
        }
    }

}