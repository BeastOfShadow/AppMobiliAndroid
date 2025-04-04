package it.uniupo.ktt.ui.model

data class Task(
    val caregiver: String = "",
    val employee: String = "",

    val title: String = "",
    val description: String = "",
    val duration: String = "",
    val location: String = "",

    val overallComment: String = "",
    val overallRating: Int = 0,

    val status: String = "" //ready, ongoing, completed
){
    init {
        //validazione campi
        require(caregiver.isNotBlank()) { "careGiverId non inserito." }
        require(employee.isNotBlank()) { "employeeId non inserito." }

        require(title.isNotBlank()) { "titolo non inserito." }
        require(description.isNotBlank()) { "descrizione non inserita." }
        require(duration.isNotBlank()) { "durata non inserita." }
        require(location.isNotBlank()) { "Il location non inserita." }

        require(status.isNotBlank()) { "statoTask non inserito." }
    }
}