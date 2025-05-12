package it.uniupo.ktt.ui.model

data class SubTask(
    val id: String,
    val listNumber: Int = 0,
    val description: String = "",

    val employeeComment: String = "",
    val caregiverComment: String = "",

    // GUARDA -> CARICAMENTO FOTO SU FIREBASE STORAGE + OTTENIMENTO STRINGA DESTINAZIONE
    val descriptionImgStorageLocation: String = "",
    val employeeImgStorageLocation: String = "",
    val caregiverImgStorageLocation: String = "",

    val status: String = "", //avilable, ready, running, completed
){
    init {
        //validazione campi
        require(listNumber > 0) { "Numero della Lista invalido." }
        require(description.isNotBlank()) { "descrizione non inserita." }

        require(status.isNotBlank()) { "statoTask non inserito." }
    }
}