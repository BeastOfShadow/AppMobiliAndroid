package it.uniupo.ktt.ui.firebase

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot


object ChatRepository {


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
    fun createChat(
        chatId: String,
        caregiverPath: String,
        employeePath: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val uid = BaseRepository.currentUid()
        val db = BaseRepository.db

        val chatData = hashMapOf(
            "chatId" to chatId,
            "caregiver" to caregiverPath,
            "employee" to employeePath,
            "lastMsg" to "",
            "lastSender" to "",
            "lastTimeStamp" to Timestamp.now()
        )

        db.collection("chats").document(chatId)
            .set(chatData)
            .addOnSuccessListener {
                Log.d("Firestore", "Chat $chatId creata")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Errore creazione chat", e)
                onFailure(e)
            }
    }




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