package it.uniupo.ktt.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.StatisticsRepository
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor() : ViewModel() {


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
}