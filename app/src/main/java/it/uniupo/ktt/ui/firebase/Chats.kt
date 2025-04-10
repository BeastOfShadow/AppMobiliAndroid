package it.uniupo.ktt.ui.firebase

import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import it.uniupo.ktt.ui.model.Chat
import it.uniupo.ktt.ui.model.Contact
import it.uniupo.ktt.ui.model.User
import kotlinx.coroutines.tasks.await


object ChatRepository {

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

        // OK
    fun getAllChatsByUid(
        uid: String,
        onSuccess: (List<Chat>) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        BaseRepository.db
            .collection("chats")
            .whereEqualTo("caregiver", uid)
            .get()
            .addOnSuccessListener { snapshot -> //ritorna una lista di Chats
                val chats = snapshot.documents.mapNotNull { it.toObject(Chat::class.java) }
                Log.d("Firestore", "Trovate ${chats.size} chats dato uid: $uid")
                onSuccess(chats)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Errore durante la query getAllChatsByUid", e)
                onError(e)
            }
    }





//    suspend fun getAllContactsByUid(
//        uid: String,
//    ): User?{
//        return try {
//            val snapshot = BaseRepository.db
//                .collection("contacts")
//                .whereEqualTo("uidPersonal", uid)
//                .get()
//                .await()
//
//            snapshot.documents.firstOrNull()?.toObject(User::class.java)
//        }
//        catch (e: Exception){
//            null
//        }
//    }

                                // GET ALL CHATS by uid
//    fun fetchChatsForUser(
//        userPath: String,
//        onResult: (List<QueryDocumentSnapshot>) -> Unit,
//        onFailure: (Exception) -> Unit = {}
//    ) {
//        db.collection("chats")
//            .whereEqualTo("caregiver", userPath)
//            .get()
//            .addOnSuccessListener { caregiverChats ->
//                db.collection("chats")
//                    .whereEqualTo("employee", userPath)
//                    .get()
//                    .addOnSuccessListener { employeeChats ->
//                        val combined = caregiverChats.documents + employeeChats.documents
//                        onResult(combined)
//                    }
//            }
//            .addOnFailureListener { e ->
//                Log.w("Firestore", "Errore recupero chat", e)
//                onFailure(e)
//            }
//    }


                                    //POST NEW CHAT
//    fun createChat(
//        caregiverPath: String,
//        employeePath: String,
//        onSuccess: () -> Unit = {},
//        onFailure: (Exception) -> Unit = {}
//    ) {
//        val uid = BaseRepository.currentUid()
//        val db = BaseRepository.db
//
//        val chatData = hashMapOf(
//            "caregiver" to caregiverPath,
//            "employee" to employeePath,
////            "lastMsg" to "",
////            "lastSender" to "",
////            "lastTimeStamp" to Timestamp.now()
//        )
//
//        db.collection("chats").document(chatId)
//            .set(chatData)
//            .addOnSuccessListener {
//                Log.d("Firestore", "Chat $chatId creata")
//                onSuccess()
//            }
//            .addOnFailureListener { e ->
//                Log.w("Firestore", "Errore creazione chat", e)
//                onFailure(e)
//            }
//    }




                     //GET ALL MSG by chatId (Whole chat by Id)
//    fun fetchMessages(
//        chatId: String,
//        onResult: (List<QueryDocumentSnapshot>) -> Unit,
//        onFailure: (Exception) -> Unit = {}
//    ) {
//        db.collection("chats").document(chatId)
//            .collection("messages")
//            .orderBy("timeStamp")
//            .get()
//            .addOnSuccessListener { result ->
//                onResult(result.documents)
//            }
//            .addOnFailureListener { e ->
//                Log.w("Firestore", "Errore recupero messaggi", e)
//                onFailure(e)
//            }
//    }


                                    //POST NEW MSG
    fun sendMessage(
        chatId: String,
        msgId: String,
        senderPath: String,
        text: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val uid = BaseRepository.currentUid()
        val db = BaseRepository.db

        val msgData = hashMapOf(
            "msgId" to msgId,
            "sender" to senderPath,
            "text" to text,
            "timeStamp" to Timestamp.now(),
            "seen" to false
        )

        db.collection("chats").document(chatId)
            .collection("messages")
            .document(msgId)
            .set(msgData)
            .addOnSuccessListener {
                Log.d("Firestore", "Messaggio inviato in $chatId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Errore invio messaggio", e)
                onFailure(e)
            }

        // ✅ opzionale: aggiorna metadati della chat
        db.collection("chats").document(chatId)
            .update(
                mapOf(
                    "lastMsg" to text,
                    "lastSender" to senderPath,
                    "lastTimeStamp" to Timestamp.now()
                )
            )
    }









}