package it.uniupo.ktt.ui.model

data class Message(
    val uidSender: String = "",
    val text: String = "",

    val seen: String = "", // come devo passare un Boolean?
    val timeStamp: String = "" //come devo passare un TimeStamp?
){
    fun isValid(): Boolean {
        return uidSender.isNotBlank() &&
                text.isNotBlank() &&
                seen.isNotBlank() &&
                timeStamp.isNotBlank()
    }
}