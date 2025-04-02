package it.uniupo.ktt.ui.model

data class Chat(
    val caregiver: String = "",
    val employee: String = "",

    val lastMsg: String = "",
    val uidLastSender: String = "",
    val lastTimeStamp: String = "" //come devo passare un TimeStamp?
    ){
    init {
        //validazione campi
        require(caregiver.isNotBlank()) { "caregiver non inserito." }
        require(employee.isNotBlank()) { "employee non inserito." }
    }
}