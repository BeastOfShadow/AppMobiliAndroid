package it.uniupo.ktt.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.StatisticsRepository
import it.uniupo.ktt.ui.model.Task
import java.time.LocalDate
import javax.inject.Inject

import java.time.temporal.WeekFields
import java.time.ZoneId

@HiltViewModel
class StatisticsViewModel @Inject constructor() : ViewModel() {

                                // BUBBLE CHART

    // OK -> Completed,Ongoing,Ready Counters
    fun bubbleChartInfo(
        uid: String,
        onResult: (ready: Int, ongoing: Int, completed: Int) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        StatisticsRepository.getReadyOngoingCompletedByUid(
            role = "caregiver",
            uid = uid,
            onSuccess = { tasks ->
                val ready = tasks.count { it.status == "ready" }
                val ongoing = tasks.count { it.status == "ongoing" }

                val today = LocalDate.now()
                val zoneId = ZoneId.systemDefault()

                val completed = tasks.count { task ->
                    task.status == "completed" &&
                            task.timeStampEnd?.toDate()
                                ?.toInstant()
                                ?.atZone(zoneId)
                                ?.toLocalDate() == today
                }

                onResult(ready, ongoing, completed)
            },
            onError = onError
        )
    }

                                // AVG BAR

        // OK -> media TaskDaily, media TaskGeneral
    fun avgCompletionBarInfo (
        role: String,
        onSuccess: (avgDaily: Double, avgGeneral: Double) -> Unit,
        onError: (Exception) -> Unit = {}
    ){

        var avgDaily = 0.0
        var avgGeneral = 0.0

        val uid = BaseRepository.currentUid()

        if(uid != null){

            // Calcolo Medie Daily&General dei task completati (solo quelli finiti dove "completionTimeActual" > 0)
            StatisticsRepository.getAllDailyCompletedTaskByUid(
                role = role,
                uid = uid,
                onSuccess = { dailyTasks ->

                    val filteredDaily = dailyTasks.filter { it.isValid() } // lista filtrata secondo la mia "isValid()" scritta nel Task MODEL

                    if(filteredDaily.isNotEmpty()){
                        avgDaily = filteredDaily
                            .map { it.completionTimeActual }   // seleziona campo
                            .filter { it > 0 }  // considera solo i valori > 0 (escludi task non ancora terminati)
                            .average()  // calcola media

                        //Log.d("DEBUG", "Media Daily Tasks: ${avgDailyCompletionTime.value}")
                    }

                    StatisticsRepository.getGeneralCompletedTaskByUid(
                        role = role,
                        uid = uid,
                        onSuccess = { generalTasks ->

                            val filteredGeneral = generalTasks.filter { it.isValid() } // lista filtrata secondo la mia "isValid()" scritta nel Task MODEL

                            if(filteredGeneral.isNotEmpty()){
                                avgGeneral = filteredGeneral
                                    .map { it.completionTimeActual }   // seleziona campo
                                    .filter { it > 0 }  // considera solo i valori > 0 (escludi task non ancora terminati)
                                    .average()  // calcola media

                                //Log.d("DEBUG", "Media Daily Tasks: ${avgDailyCompletionTime.value}")
                            }

                            onSuccess(avgDaily, avgGeneral)
                        },
                        onError = onError
                    )
                },
                onError = onError
            )
        }




    }


                                // TODAY BAR

    // DA TESTARE -> DailyTask_Counter & DailyTaskCompleted_Counter
    fun todayBarInfo(
        uid: String,
        onSuccess: (total: Int, completed: Int) -> Unit = { _, _ -> },
        onError: (Exception) -> Unit = {}
    ) {
        StatisticsRepository.getAllDailyTaskByUid(
            role = "employee",
            uid = uid,
            onSuccess = { tasks ->
                val dailyTaskCounter = tasks.size
                val dailyTaskCompletedCounter = tasks.count { it.status == "completed" }
                onSuccess(dailyTaskCounter, dailyTaskCompletedCounter)
            },
            onError = onError
        )
    }


                                // CIRCLE DIAGRAM

    // DA TESTARE -> countTaskYearly, countTaskMonthly, countTaskWeekly
    fun circleDiagramInfo(
        uid: String,
        onSuccess: (yearly: Int, monthly: Int, weekly: Int) -> Unit,
        onError: (Exception) -> Unit = {}
    ){
        /*
        *                  -------- LOGICA --------
        *
        *   // 1) ottengo lista annuale, la conto
        *   // 2) ottengo la lista monthly da quella annuale e conto
        *   // 3) ottengo la lista weekly da quella montly e conto
        *
        *   // return INFO
        *
        */

        StatisticsRepository.getAllYearlyTasksCompletedByUid(
            uid = uid,
            onSuccess = { yearlyTasksList ->
                val now = LocalDate.now()
                val currentMonth = now.month
                val currentWeek = now.get(WeekFields.ISO.weekOfWeekBasedYear())

                // 1) INFO: Totale Task completati in questo anno
                val yearlyCount = yearlyTasksList.size

                // 2) INFO:Task di questo mese corrente dalla lista annuale
                val monthlyTasksList = yearlyTasksList.filter { task ->
                    val endTime = task.timeStampEnd?.toDate()
                        ?.toInstant()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toLocalDate()
                    endTime?.month == currentMonth
                }
                val monthlyCount = monthlyTasksList.size

                // 3) Filtro i task di questa settimana dalla lista mensile
                val weeklyTasks = monthlyTasksList.filter { task ->
                    val endDate = task.timeStampEnd?.toDate()
                        ?.toInstant()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toLocalDate()
                    endDate?.get(WeekFields.ISO.weekOfWeekBasedYear()) == currentWeek
                }
                val weeklyCount = weeklyTasks.size


                onSuccess(yearlyCount, monthlyCount, weeklyCount)
            },
            onError = onError
        )

    }


}