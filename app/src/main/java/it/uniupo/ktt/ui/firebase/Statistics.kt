package it.uniupo.ktt.ui.firebase

import android.util.Log
import com.google.firebase.Timestamp
import it.uniupo.ktt.ui.model.Task
import java.time.LocalDate
import java.time.ZoneId

object StatisticsRepository {

                                    // BUBBLE CHART
    fun getReadyOngoingCompletedByUid(
        role: String,
        uid: String,
        onSuccess: (List<Task>) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ){
        BaseRepository.db
            .collection("tasks")
            .whereEqualTo(role, uid)
            .whereIn("status", listOf("ready", "ongoing", "completed"))
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


                                    // TODAY BAR  -> MODIFICA LOGICA DAILY

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