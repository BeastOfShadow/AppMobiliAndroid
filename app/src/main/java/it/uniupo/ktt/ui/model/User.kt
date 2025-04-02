package it.uniupo.ktt.ui.model

data class User(
    val role: String = "",

    val email: String = "",
    val name: String = "",
    val surname: String = ""
){
    init {
        //validazione campi
        require(role.isNotBlank()) { "ruolo non inserito." }

        require(email.isNotBlank()) { "L'email non inserita." }
        require(name.isNotBlank()) { "Il nome non inserito." }
        require(surname.isNotBlank()) { "Il cognome non inserito." }
    }
}