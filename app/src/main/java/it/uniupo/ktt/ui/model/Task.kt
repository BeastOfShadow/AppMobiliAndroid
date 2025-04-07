package it.uniupo.ktt.ui.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Task(
    val caregiver: String = "",
    val employee: String = "",

    val title: String = "",
    val description: String = "",
    val completionTimeEstimate: Int = 0,
    val completionTimeActual: Int = 0, // da inserire a task finito arrontondando a Int

    val overallComment: String = "", // da inserire a task finito
    val overallRating: Int = 0, // da inserire a task finito
    val timeStampStart: Timestamp = Timestamp.now(), // da inserire all'inizio del task -> l'employee quando preme "start" (posta il timeStampStart dell current Task)
    val timeSTampEnd: Timestamp = Timestamp.now(), // da inserire a task finito -> l'employee quando finisce e fa il commit all'ultimo subtask della lista (posta il timeStampEnd del currentTask)
    val location: GeoPoint= GeoPoint(0.0, 0.0), // da inserire a task in corso -> postato dall'employee al completamento del SubTask Location

    val status: String = ""                 // Possibili stati: ready, ongoing, completed
){

    fun isValid(): Boolean {
        return caregiver.isNotBlank() &&
                //employee.isNotBlank() &&

                //title.isNotBlank() &&
                //description.isNotBlank() &&
                //completionTimeEstimate > 0 &&

                status.isNotBlank()

    }
}