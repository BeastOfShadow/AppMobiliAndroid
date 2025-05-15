package it.uniupo.ktt.ui.firebase

import android.util.Log
import com.google.firebase.Timestamp
import it.uniupo.ktt.ui.model.Task
import java.time.LocalDate
import java.time.ZoneId

object StatisticsRepository {

                                    // BUBBLE CHART
        // OK -> CompletedCount, OngoingCount, ReadyCount
    fun getReadyOngoingCompletedByUid(
        role: String,
        uid: String,
        onSuccess: (List<Task>) -> Unit = {},
        onError: (Exception) -> Unit = {}
    ){
        BaseRepository.db
            .collection("tasks")
            .whereEqualTo(role, uid)
            .whereIn("status", listOf("READY", "ONGOING", "COMPLETED"))
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

        // OK -> lista CompletedTask by Uid & role
    fun getGeneralCompletedTaskByUid(
            role: String,
            uid: String,
            onSuccess: (List<Task>) -> Unit = {},
            onError: (Exception) -> Unit = {}
    ) {

        BaseRepository.db
            .collection("tasks")
            .whereEqualTo(role, uid)
            .whereEqualTo("status", "COMPLETED")
            .get()
            .addOnSuccessListener { snapshot -> //ritorna una lista di Tasks
                val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                // Log.d("DEBUG", "Trovati ${tasks.size} General Completed Tasks dato Caregiver: $uid")
                onSuccess(tasks)
            }
            .addOnFailureListener { e ->
                Log.e("DEGUB", "QUERY-Error in StatisticsRepo -> getAllDailyCompletedTaskByUid", e)
                onError(e)
            }
    }


                                    // TODAY BAR

        // OK -> all Daily Tasks COMPLETED or ONGOING By: Role, Uid
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
            .whereIn("status", listOf("ONGOING", "COMPLETED"))
            .whereGreaterThanOrEqualTo("timeStampEnd", dayStart)
            .whereLessThanOrEqualTo("timeStampEnd", dayEnd)
            .get()
            .addOnSuccessListener { snapshot ->
                val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                // Log.d("DEBUG", "Totale task oggi: ${tasks.size}")
                onSuccess(tasks)
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG", "Errore getTodayTotalTaskCountByUid", e)
                onError(e)
            }
    }


                                    // CIRCLE DIAGRAM

        // OK -> ritorna lista Task COMPLETED, dell'anno corrente
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
            .whereEqualTo("status", "COMPLETED")
            .whereGreaterThanOrEqualTo("timeStampEnd", tsStart)
            .whereLessThanOrEqualTo("timeStampEnd", tsEnd)
            .get()
            .addOnSuccessListener { snapshot ->
                val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
                // Log.d("DEBUG", "Totale task COMPLETED this YEAR: ${tasks.size}")
                onSuccess(tasks)
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG", "Errore getAllYearlyTasksCompletedByUid", e)
                onError(e)
            }
    }




}