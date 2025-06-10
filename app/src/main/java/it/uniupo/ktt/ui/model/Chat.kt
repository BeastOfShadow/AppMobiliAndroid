package it.uniupo.ktt.ui.model

import com.google.firebase.Timestamp

data class Chat(
    val chatId: String= "",
    val caregiver: String = "",
    val employee: String = "",
    val members: List<String> = emptyList(), // NEW method -> utilizzo del Global Listener

    val lastMsg: String = "",
    val uidLastSender: String = "",
    val lastTimeStamp: Timestamp = Timestamp.now(),

    // UnreadMessage (Map: Uid-> lastOpenedTimeStamp)
    val lastOpenedBy: Map<String, Timestamp> = emptyMap()
    ){
    fun isValid(): Boolean {
        return caregiver.isNotBlank() &&
                employee.isNotBlank()
    }
}