package it.uniupo.ktt.ui.firebase

import android.util.Log
import com.google.firebase.Timestamp
import it.uniupo.ktt.ui.model.Task
import java.time.LocalDate
import java.time.ZoneId

object StatisticsRepository {

        // OK
    fun getTaskCountsByStatus(
        uid: String,
        onResult: (ready: Int, ongoing: Int, completed: Int) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        val db = BaseRepository.db
        var ready = 0
        var ongoing = 0
        var completed = 0

        // Counter per sapere quando ho finito tutte le chiamate
        var completedCalls = 0

        // mi permette di eseguire le 3 query in parallelo (senza ".await"), sfruttando un contatore
        // per regolare l'attesa del return (che ritorna una tupla di 3 valori)
        fun checkIfAllDone() {
            completedCalls++
            if (completedCalls == 3) {
                onResult(ready, ongoing, completed)
            }
        }

        // prima query per i readyTasks
        db.collection("tasks")
            .whereEqualTo("caregiver", uid)
            .whereEqualTo("status", "ready")
            .get()
            .addOnSuccessListener { snapshot -> // Snap VUOTO se non ho DOC, e la sua ".size()" è "0"
                ready = snapshot.size()
                checkIfAllDone()
            }
            .addOnFailureListener { onError(it) }

        // seconda query per gli ongoingTasks
        db.collection("tasks")
            .whereEqualTo("caregiver", uid)
            .whereEqualTo("status", "ongoing")
            .get()
            .addOnSuccessListener { snapshot ->
                ongoing = snapshot.size()
                checkIfAllDone()
            }
            .addOnFailureListener { onError(it) }

        // terza query per i completedTasks
        db.collection("tasks")
            .whereEqualTo("caregiver", uid)
            .whereEqualTo("status", "completed")
            .get()
            .addOnSuccessListener { snapshot ->
                completed = snapshot.size()
                checkIfAllDone()
            }
            .addOnFailureListener { onError(it) }
    }

        // OK
    fun getAllDailyCompletedTaskByCaregiverUid(
        uid: String,
        onSuccess: (List<Task>) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val today = LocalDate.now() // "LocalDate.now()" data odierna (2025-04-04), mancano le ore che devo aggiungere
        val dayStart = Timestamp(today.atStartOfDay(ZoneId.systemDefault()).toInstant()) // "today.atStartOfDay(ZoneId.systemDefault())" -> (2025-04-04T00:00:00), ".toInstant()"-> trasforma in un TimeStamp
        val dayEnd = Timestamp(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1))

        BaseRepository.db
            .collection("tasks")
            .whereEqualTo("caregiver", uid)
            .whereEqualTo("status", "completed")
            .whereGreaterThanOrEqualTo("timeStampEnd", dayStart)
            .whereLessThanOrEqualTo("timeStampEnd", dayEnd)
            .get()
            .addOnSuccessListener { snapshot -> //ritorna una lista di Tasks
                val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                Log.d("DEBUG", "Trovati ${tasks.size} Daily Completed Tasks dato Caregiver: $uid")
                onSuccess(tasks)
            }
            .addOnFailureListener { e ->
                Log.e("DEGUB", "QUERY-Error in StatisticsRepo -> getAllDailyCompletedTaskByUid", e)
                onError(e)
            }
    }

        //OK
    fun getGeneralCompletedTaskByCaregiverUid(
    uid: String,
    onSuccess: (List<Task>) -> Unit = {},
    onError: (Exception) -> Unit = {}
    ) {

        BaseRepository.db
            .collection("tasks")
            .whereEqualTo("caregiver", uid)
            .whereEqualTo("status", "completed")
            .get()
            .addOnSuccessListener { snapshot -> //ritorna una lista di Tasks
                val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                Log.d("DEBUG", "Trovati ${tasks.size} General Completed Tasks dato Caregiver: $uid")
                onSuccess(tasks)
            }
            .addOnFailureListener { e ->
                Log.e("DEGUB", "QUERY-Error in StatisticsRepo -> getAllDailyCompletedTaskByUid", e)
                onError(e)
            }
    }

        // OK
    fun getGenAndDailyTaskDoneByCaregiverUid(
        uid: String,
        onSuccess: (dailyTasks: List<Task>, generalTasks: List<Task>) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        var dailyTasks: List<Task> = emptyList()
        var generalTasks: List<Task> = emptyList()
        var completedCalls = 0

        // counter di attesa della doppia query in parallelo
        fun checkIfAllDone() {
            completedCalls++
            if (completedCalls == 2) {
                onSuccess(dailyTasks, generalTasks)
            }
        }

        getAllDailyCompletedTaskByCaregiverUid(
            uid = uid,
            onSuccess = { tasks ->
                dailyTasks = tasks
                checkIfAllDone()
            },
            onError = { e -> onError(e) }
        )

        getGeneralCompletedTaskByCaregiverUid(
            uid = uid,
            onSuccess = { tasks ->
                generalTasks = tasks
                checkIfAllDone()
            },
            onError = { e -> onError(e) }
        )
    }





}