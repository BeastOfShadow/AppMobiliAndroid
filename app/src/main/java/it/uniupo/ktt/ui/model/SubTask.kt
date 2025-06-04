package it.uniupo.ktt.ui.model

import androidx.compose.runtime.MutableState

data class SubTask(
    val id: String = "",
    val listNumber: Int = 0,
    val description: String = "",

    val employeeComment: String = "",
    val caregiverComment: String = "",

    // GUARDA -> CARICAMENTO FOTO SU FIREBASE STORAGE + OTTENIMENTO STRINGA DESTINAZIONE
    var descriptionImgStorageLocation: String = "",
    val employeeImgStorageLocation: String = "",
    var caregiverImgStorageLocation: String = "",

    val status: String = "", //avilable, ready, running, completed
) {

}