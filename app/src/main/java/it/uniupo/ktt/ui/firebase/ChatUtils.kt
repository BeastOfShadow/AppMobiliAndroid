package it.uniupo.ktt.ui.firebase

import android.util.Log
import it.uniupo.ktt.ui.model.User

object ChatUtils{

        //OK -> utilizzata da "ChatViewModel" e da "NewChatViewModel" per arricchire le chat e i contact con AvatarUrl
    fun getUserAndAndAvatarByUid(
        uidUser: String,
        onSuccess: (User?, String?) -> Unit,
        onError: (Exception) -> Unit
    ){
        Log.d("DEBUG-AVATAR", "Chiamata a getUserByUid per $uidUser")

        UserRepository.getUserByUid(
            uid = uidUser,
            onSuccess = { user ->
                Log.d("DEBUG-AVATAR", "Utente ricevuto: ${user?.uid}")

                if (user != null && user.avatar.isNotBlank()) {

                    // Ottieni DownloadUrl da Firebase Storage
                    BaseRepository.storage.reference
                        .child(user.avatar)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.d("DEBUG-AVATAR", "Download avatar riuscito")
                            onSuccess(user, uri.toString())
                        }
                        .addOnFailureListener { e ->
                            Log.e("DEBUG-AVATAR", "Errore download URL avatar", e)
                            onSuccess(user, null) // ritorna user e "null" (al posto dell'immagine)
                        }
                }
                else {
                    Log.d("DEBUG-AVATAR", "Utente nullo o avatar assente")
                    onSuccess(user, null) // user null o avatar non presente
                }
            },
            onError = {
                Log.e("DEBUG-AVATAR", "Errore getUserByUid", it)
                onError(it)
            }
        )

    }

}