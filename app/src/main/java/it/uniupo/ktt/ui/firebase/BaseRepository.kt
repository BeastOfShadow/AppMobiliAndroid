package it.uniupo.ktt.ui.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object BaseRepository {

                                    //SINGLETON (istanze immutabili)
    // "SINGLETON" FireBase Auth
    val auth: FirebaseAuth = FirebaseAuth.getInstance() //update automatico al LogOut

    // "SINGLETON" FireBase Db
    @Suppress("StaticFieldLeak") //"FirebaseFirestore.getInstance()" è un singleton ufficiale gestito internamente da Firebase, non crea memory leak
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()



    // return UID del CurrentUser(se si è loggati), OR null
    fun currentUid(): String? = auth.currentUser?.uid

    // return TRUE se l'utente è loggato, OR FALSE
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // return OBJ User (per intero, posso accedere a tutti i suoi campi)
    fun currentUser() = auth.currentUser
}