package it.uniupo.ktt.ui.model

import com.google.firebase.Timestamp

data class Chat(
    val chatId: String= "",
    val caregiver: String = "",
    val employee: String = "",
    // 2 campi extra che aggiungo per comodità per ChatPage
    val name: String= "",
    val surname: String= "",

    val lastMsg: String = "",
    val uidLastSender: String = "",
    val lastTimeStamp: Timestamp = Timestamp.now()
    ){
    fun isValid(): Boolean {
        return caregiver.isNotBlank() &&
                employee.isNotBlank()
    }
}