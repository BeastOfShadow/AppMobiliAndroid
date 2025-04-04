package it.uniupo.ktt.ui.model

data class User(
    val uid : String = "",
    val role: String = "",

    val email: String = "",
    val name: String = "",
    val surname: String = ""
){

    /* Non metto controlli "require" dato che per la creazione è otttimo, ma per
    la deserializzazione è un casino dato che ".toObject" passa un
    oggetto serializzato che non ha campi esatti fino alla deserializzazione
    quindi: 1) deserializzo 2)controllo i campi con fun "isValid" */
    fun isValid(): Boolean {
        return role.isNotBlank() &&
                email.isNotBlank() &&
                name.isNotBlank() &&
                surname.isNotBlank()
    }
}