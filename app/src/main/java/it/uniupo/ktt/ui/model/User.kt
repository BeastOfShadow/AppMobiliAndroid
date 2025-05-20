package it.uniupo.ktt.ui.model

data class User(
    val uid : String = "",
    val role: String = "",

    val email: String = "",
    val name: String = "",
    val surname: String = "",

    val avatar: String = "",
    val userPoint: Int = 0,

    val deviceToken: String= "" // DeviceToken per le notifiche Chat
){

    /* Non metto controlli "require" dato che per la creazione è otttimo, ma per
    la deserializzazione è un casino dato che ".toObject" passa un
    oggetto serializzato che non ha campi esatti fino alla deserializzazione
    quindi: 1) deserializzo 2)controllo i campi con fun "isValid" */
    fun isValid(): Boolean {
        return role.isNotBlank() &&
                email.isNotBlank() &&
                name.isNotBlank() &&
                surname.isNotBlank() &&
                avatar.isNotBlank() &&
                userPoint >= 0 &&
                deviceToken.isNotBlank()
    }
}