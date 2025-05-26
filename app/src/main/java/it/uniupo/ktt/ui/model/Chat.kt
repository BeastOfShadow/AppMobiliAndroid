package it.uniupo.ktt.ui.model

import com.google.firebase.Timestamp

data class Chat(
    val chatId: String= "",
    val caregiver: String = "",
    val employee: String = "",

    val lastMsg: String = "",
    val uidLastSender: String = "",
    val lastTimeStamp: Timestamp = Timestamp.now()
    ){
    fun isValid(): Boolean {
        return caregiver.isNotBlank() &&
                employee.isNotBlank()
    }
}