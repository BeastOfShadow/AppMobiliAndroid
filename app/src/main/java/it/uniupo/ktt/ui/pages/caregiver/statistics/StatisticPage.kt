package it.uniupo.ktt.ui.pages.caregiver.statistics

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.uniupo.ktt.ui.components.statistics.AvgComplationBar
import it.uniupo.ktt.ui.components.statistics.DailyTasksBubbleChart
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.statistics.StatStatusBadge
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.StatisticsRepository
import it.uniupo.ktt.ui.model.Task
import it.uniupo.ktt.ui.theme.titleColor

@Composable
fun CG_StatisticPage(navController: NavController) {
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("landing") {
            popUpTo("new chat") { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(30.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
            //.verticalScroll(rememberScrollState())
        ) {
            PageTitle(
                navController = navController,
                title = "Statistic"
            )
            Spacer(
                modifier = Modifier.height(42.dp)
            )

            Text(
                text = "Daily Tasks",
                style = MaterialTheme.typography.bodyLarge, //Poppins

                fontSize = 22.sp,
                fontWeight = FontWeight(500),


                color = titleColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // 3 Variabili BASE da cui derivo "completed"
                val completedTasks = remember { mutableStateOf(0) }
                val ongoingTasks = remember { mutableStateOf(0) }
                val readyTasks = remember { mutableStateOf(0) }

                // Multi-Query -> per ottenere il numero tot. dei tasks nei loro 3 Stati
                val personalUid = BaseRepository.currentUid()

                LaunchedEffect(personalUid) {
                    if(personalUid!= null){
                        StatisticsRepository.getTaskCountsByStatus(
                            uid = personalUid,
                            // classica Logica Val Mutable in Lambda expression -> che verrà modificato
                            onResult = { ready, ongoing, completed ->
                                readyTasks.value = ready
                                ongoingTasks.value = ongoing
                                completedTasks.value = completed
                            },
                            onError = { e -> Log.e("DEBUG", "Errore nella query", e)}
                        )
                    }

                }

                //riferimenti Chiave traformazioni Dimensioni Cerchi (NON MODIFICARE)
                val minTasks = 11       // Default: 11
                val maxTasks = 25       // Default: 25

                val minDimBubble = 35f
                val maxDimBubble = 80f


                //BubbleChart COMPONENT
                DailyTasksBubbleChart(
                    // "completed, ongoing, ready" sono il Raggio finale del cerchio rappresentato
                    completed = when {
                        completedTasks.value < minTasks -> minDimBubble //caso dimensione Bubble minima 35f
                        completedTasks.value > maxTasks -> maxDimBubble //caso dimensione Bubble massima 80f
                        else -> 35f + ((completedTasks.value - 10) *3f)         //caso intermedio Bubble
                    },
                    ongoing = when {
                        ongoingTasks.value < minTasks -> minDimBubble //caso dimensione Bubble minima 35f
                        ongoingTasks.value > maxTasks -> maxDimBubble //caso dimensione Bubble massima 80f
                        else -> 35f + ((ongoingTasks.value - 10) *3f)         //caso intermedio Bubble
                    },
                    ready = when {
                        readyTasks.value < minTasks -> minDimBubble //caso dimensione Bubble minima 35f
                        readyTasks.value > maxTasks -> maxDimBubble //caso dimensione Bubble massima 80f
                        else -> 35f + ((readyTasks.value - 10) *3f)         //caso intermedio Bubble
                    },
                    completedNumb = completedTasks.value,
                    onGoingNumb = ongoingTasks.value,
                    readyNumb = readyTasks.value
                )

                Text(
                    text = "Average Completion Time",
                    style = MaterialTheme.typography.bodyLarge, //Poppins

                    fontSize = 22.sp,
                    fontWeight = FontWeight(500),


                    color = titleColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(
                    modifier = Modifier.height(32.dp)
                )



                //AVG BAR BOX
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(100.dp)
                ){

                    // OTTIENI 2 MEDIE
                    /*
                    *  1) Media "completionTimeToday": ovvero la media della Duratio dei lavori "completed"
                    *                                  svolti da tutti i tuoi dipendenti oggi
                    *
                    *  2) Media "completionTimeGeneral": ovvero la media della durata di tutti i lavori "completed"
                    *                                    svolti in generale
                    *
                    * */

                    // 1.1) Lista completionTimeToday: -------------------------------------------
                    val dailyTasksCompletedList = remember { mutableStateOf<List<Task>>(emptyList()) }

                    LaunchedEffect(personalUid) {
                        if (personalUid != null) {
                            StatisticsRepository.getAllDailyCompletedTaskByCaregiverUid(

                                uid = personalUid,
                                onSuccess = { dailyCompletedTasks ->
                                    Log.d("DEBUG", "Lista tasks: ${dailyCompletedTasks.joinToString(separator = "\n")}")
                                    dailyTasksCompletedList.value = dailyCompletedTasks.filter { it.isValid() }
                                },
                                onError = { e ->
                                    Log.e("DEBUG", "Errore nella getAllDailyCompletedTaskByCaregiverUid query", e)
                                }
                            )
                        }
                    }

                    // 1.2) calcola la media TodayCompletionTime:
                    val avgDailyCompletionTime = dailyTasksCompletedList.value
                        .map { it.completionTimeActual }   // seleziona campo
                        .filter { it > 0 }  // considera solo i valori > 0 (escludi quelli invalidi)
                        .average()  // calcola media

                    // 2.1) Lista completionTimeGeneral: -------------------------------------------
                    val generalTasksCompletedList = remember { mutableStateOf<List<Task>>(emptyList()) }

                    LaunchedEffect(personalUid) {
                        if (personalUid != null) {
                            StatisticsRepository.getGeneralCompletedTaskByCaregiverUid(

                                uid = personalUid,
                                onSuccess = { generalCompletedTasks ->
                                    Log.d("DEBUG", "Lista tasks: ${generalCompletedTasks.joinToString(separator = "\n")}")
                                    generalTasksCompletedList.value = generalCompletedTasks.filter { it.isValid() }
                                },
                                onError = { e ->
                                    Log.e("DEBUG", "Errore nella getGeneralCompletedTaskByCaregiverUid query", e)
                                }
                            )
                        }
                    }

                    // 2.2) calcola la media GeneralcompletionTime:
                    val avgGeneralCompletionTime = dailyTasksCompletedList.value
                        .map { it.completionTimeActual }   // seleziona campo
                        .filter { it > 0 }  // considera solo i valori > 0 (escludi quelli invalidi)
                        .average()  // calcola media


                    // ho le 2 medie, stampale per controllare che tutto sia OK
                    // se tutto ok ->

                    // val ratioTime = avgDailyCompletionTime/avgGeneralCompletionTime





                    // esempi valori(in futuro ottienili da DB e calcolali):
                    val completionTimeToday = 16.41
                    val completionTimeGeneral = 26.55
                    val ratioTime = completionTimeToday/completionTimeGeneral

                    //BAR
                    AvgComplationBar(
                        ratio = ratioTime.toFloat()
                    )

                    //TODAY'S BADGE
                    StatStatusBadge(
                        statusColor = Color(0xFF6326A9),
                        statusText = "35.40 min",

                        //Modifier aggiuntivi utili passabili
                        modifier = Modifier
                            .scale(0.6f)
                            .offset(x = (-30).dp, y = (-30).dp)
                    )
                    Text(
                        text = "(today)",
                        style = MaterialTheme.typography.labelSmall, //Poppins

                        fontSize = 13.sp,
                        fontWeight = FontWeight(400),

                        color = Color(0xFF827676),
                        modifier = Modifier
                            .offset(x = (32).dp, y = (-30).dp)
                            .height(18.dp)
                    )

                    //GENERAL BADGE
                    StatStatusBadge(
                        statusColor = Color(0xFFF5DFFA),
                        statusText = "40.12 min",
                        //Modifier aggiuntivi utili passabili
                        modifier = Modifier
                            .scale(0.6f)
                            .offset(x = (+342).dp, y = (+37).dp)
                        //.align(Alignment.TopStart)
                    )
                    Text(
                        text = "(general)",
                        style = MaterialTheme.typography.labelSmall, //Poppins

                        fontSize = 13.sp,
                        fontWeight = FontWeight(400),

                        color = Color(0xFF827676),
                        modifier = Modifier
                            .offset(x = (255).dp, y = (59).dp)
                    )

                }

            }

        }

    }

}


@Preview(showBackground = true)
@Composable
fun CG_StatisticPagePreview() {
    CG_StatisticPage(navController = NavController(context = LocalContext.current))
}


