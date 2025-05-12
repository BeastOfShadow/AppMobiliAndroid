package it.uniupo.ktt.ui.model

data class Contact(
    val email: String = "",
    val name: String = "",
    val surname: String = "",

    //da passare già BBRonti
    val uidPersonal: String = "",
    val uidContact: String = ""
    ){
        /* Non metto controlli "require" dato che anche se per la creazione va bene, per
        la deserializzazione è problematico dato che ".toObject" passa un
        oggetto serializzato che non ha campi esatti fino alla deserializzazione
        quindi: 1) deserializzo 2)controllo i campi con fun "isValid" */

    fun isValid(): Boolean {
        return email.isNotBlank() &&
                name.isNotBlank() &&
                surname.isNotBlank() &&
                uidPersonal.isNotBlank() &&
                uidContact.isNotBlank()
    }
}