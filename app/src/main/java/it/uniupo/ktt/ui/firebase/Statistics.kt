package it.uniupo.ktt.ui.firebase

import android.util.Log
import com.google.firebase.Timestamp
import it.uniupo.ktt.ui.model.Task
import java.time.LocalDate
import java.time.ZoneId

object StatisticsRepository {

                                    // BUBBLE DIAGRAM
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







                                    // AVG BAR
        // OK -> AVG BAR
    fun getAllDailyCompletedTaskByUid(
            role: String,
            uid: String,
            onSuccess: (List<Task>) -> Unit = {},
            onError: (Exception) -> Unit = {}
    ) {
        val today = LocalDate.now() // "LocalDate.now()" data odierna (2025-04-04), mancano le ore che devo aggiungere
        val dayStart = Timestamp(today.atStartOfDay(ZoneId.systemDefault()).toInstant()) // "today.atStartOfDay(ZoneId.systemDefault())" -> (2025-04-04T00:00:00), ".toInstant()"-> trasforma in un TimeStamp
        val dayEnd = Timestamp(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1))

        BaseRepository.db
            .collection("tasks")
            .whereEqualTo(role, uid)
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

        // OK -> AVG BAR
    fun getGeneralCompletedTaskByUid(
            role: String,
            uid: String,
            onSuccess: (List<Task>) -> Unit = {},
            onError: (Exception) -> Unit = {}
    ) {

        BaseRepository.db
            .collection("tasks")
            .whereEqualTo(role, uid)
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


                                    // TODAY BAR  &  BUBBLE CHART

    // DA TESTARE -> all Daily Tasks By: Role, Uid
    fun getAllDailyTaskByUid(
        role: String,
        uid: String,
        onSuccess: (List<Task>) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val today = LocalDate.now()
        val dayStart = Timestamp(today.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val dayEnd = Timestamp(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1))

        BaseRepository.db
            .collection("tasks")
            .whereEqualTo(role, uid)
            .whereGreaterThanOrEqualTo("timeStampEnd", dayStart)
            .whereLessThanOrEqualTo("timeStampEnd", dayEnd)
            .get()
            .addOnSuccessListener { snapshot ->
                val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                Log.d("DEBUG", "Totale task oggi: ${tasks.size}")
                onSuccess(tasks)
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG", "Errore getTodayTotalTaskCountByUid", e)
                onError(e)
            }
    }


                                    // CIRCLE DIAGRAM

    // DA TESTARE -> CIRCLE DIAGRAM
    fun getAllYearlyTasksCompletedByUid(
        uid: String,
        onSuccess: (List<Task>) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val now = LocalDate.now()
        val yearStart = now.withDayOfYear(1)
        val yearEnd = yearStart.plusYears(1).minusDays(1)

        val tsStart = Timestamp(yearStart.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val tsEnd = Timestamp(yearEnd.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant())

        BaseRepository.db
            .collection("tasks")
            .whereEqualTo("employee", uid)
            .whereEqualTo("status", "completed")
            .whereGreaterThanOrEqualTo("timeStampEnd", tsStart)
            .whereLessThanOrEqualTo("timeStampEnd", tsEnd)
            .get()
            .addOnSuccessListener { snapshot ->
                val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                Log.d("DEBUG", "Totale task COMPLETED this YEAR: ${tasks.size}")
                onSuccess(tasks)
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG", "Errore getAllYearlyTasksCompletedByUid", e)
                onError(e)
            }
    }




}