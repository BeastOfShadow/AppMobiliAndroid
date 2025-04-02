package it.uniupo.ktt.ui.model

data class Contact(
    val email: String = "",
    val name: String = "",
    val surname: String = "",

    //da passare già BBRonti
    val uidPersonal: String = "",
    val uidContact: String = ""
    ){
        init {
            //validazione campi
            require(email.isNotBlank()) { "email non inserita." }
            require(name.isNotBlank()) { "nome non inserito." }
            require(surname.isNotBlank()) { "cognome non inserito." }

            require(uidPersonal.isNotBlank()) { "uid Personale non inserito." }
            require(uidContact.isNotBlank()) { "uid Contatto non inserito." }
        }
}