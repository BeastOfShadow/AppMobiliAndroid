package it.uniupo.ktt.ui.model

data class Message(
    val uidSender: String = "",
    val text: String = "",

    val seen: String = "", // come devo passare un Boolean?
    val timeStamp: String = "" //come devo passare un TimeStamp?
){
    init {
        //validazione campi
        require(uidSender.isNotBlank()) { "mandante non inserito." }
        require(text.isNotBlank()) { "testo non inserito." }
        require(uidSender.isNotBlank()) { "attributo lettura non inserito." }
        require(timeStamp.isNotBlank()) { "tempo di invio non inserito." }
    }
}