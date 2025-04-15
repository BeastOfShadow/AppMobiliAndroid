package it.uniupo.ktt.ui.model

import com.google.firebase.Timestamp


data class Message(
    val sender: String = "",
    val text: String = "",

    val seen: Boolean = false,
    val timeStamp: Long = System.currentTimeMillis() // RealTimeDB non supporta "timeStamp"
){
    fun isValid(): Boolean {
        return sender.isNotBlank() &&
                text.isNotBlank()
    }
}