package it.uniupo.ktt.ui.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object BaseRepository {

                                    //SINGLETON (istanze immutabili)
    // "SINGLETON" FireBase Auth
    val auth: FirebaseAuth = FirebaseAuth.getInstance() //update automatico al LogOut

    // "SINGLETON" Storage
    val storage = FirebaseStorage.getInstance()
                        // Accesso all'immagine
                        // BaseRepository.storage.reference.child("avatar/")
                        // BaseRepository.avatarRef.listAll()

    // "SINGLETON" FireBase FireStore Db
    @Suppress("StaticFieldLeak") //"FirebaseFirestore.getInstance()" è un singleton ufficiale gestito internamente da Firebase, non crea memory leak
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // "SINGLETON" Firebase Realtime Database
    val dbRealTime: DatabaseReference = FirebaseDatabase.getInstance().reference

    // return UID del CurrentUser(se si è loggati), OR null
    fun currentUid(): String? = auth.currentUser?.uid

    // return TRUE se l'utente è loggato, OR FALSE
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // return auth OBJ User
    fun currentUser() = auth.currentUser
}