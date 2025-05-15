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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.statistics.AvgComplationBar
import it.uniupo.ktt.ui.components.statistics.DailyTasksBubbleChart
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.statistics.StatStatusBadge
import it.uniupo.ktt.ui.components.statistics.WrapBox
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.StatisticsRepository
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.StatisticsViewModel
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun CG_StatisticPage(navController: NavController) {
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("login") {
            popUpTo("login") { inclusive = false } // rimuovi tutte le Page nello Stack fino a Landing senza eliminare quest'ultima
            launchSingleTop = true
        }
    }

    val personalUid = BaseRepository.currentUid()

    // istanza + collegamento
    val statisticsViewModelRef = hiltViewModel<StatisticsViewModel>()

                            // BUBBLE CHART var & rif & call
    val completedTasks = remember { mutableStateOf(0) }
    val ongoingTasks = remember { mutableStateOf(0) }
    val readyTasks = remember { mutableStateOf(0) }

    //riferimenti traformazioni Dimensioni Cerchi (NON MODIFICARE)
    val minTasks = 11
    val maxTasks = 25
    val minDimBubble = 35f
    val maxDimBubble = 80f

    // Loading Booleand (bubble chart -> 3 counter)
    val isLoadingBubbleChart = remember { mutableStateOf(false) }

    LaunchedEffect(personalUid) {
        isLoadingBubbleChart.value = true

        if(personalUid!= null){
            statisticsViewModelRef.bubbleChartInfo(
                uid = personalUid,
                onResult = { ready, ongoing, completed ->
                    readyTasks.value = ready
                    ongoingTasks.value = ongoing
                    completedTasks.value = completed

                    isLoadingBubbleChart.value = false
                },
                onError = { e ->
                    Log.e("DEBUG", "Errore nella query", e)

                    isLoadingBubbleChart.value = false
                }
            )
        }
    }


                            // AVG BAR var & call
    val avgDailyCompletionTime = remember { mutableStateOf(0.0) }
    val avgGeneralCompletionTime = remember { mutableStateOf(0.0) }

    // Loading Booleand (avgBar -> medie)
    val isLoadingAvgBar = remember { mutableStateOf(false) }

    LaunchedEffect(personalUid) {
        isLoadingAvgBar.value= true

        if (personalUid != null) {
            statisticsViewModelRef.avgCompletionBarInfo(
                role = "caregiver",
                onSuccess = { avgDaily, avgGeneral ->
                    avgDailyCompletionTime.value = avgDaily
                    avgGeneralCompletionTime.value = avgGeneral

                    isLoadingAvgBar.value = false
                },
                onError = { e ->
                    Log.e("DEBUG", "Errore avgCompletionBarInfo", e)

                    isLoadingAvgBar.value = false
                }
            )
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
                fontFamily = FontFamily(Font(R.font.poppins_medium)),

                fontSize = 22.sp,
                fontWeight = FontWeight(500),


                color = Color(0xFF2A2525),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                                                // BUBBLE CHART WAIT
                if(isLoadingBubbleChart.value == true){
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
                else{
                    Log.d("DEBUG", " BUBBLE CHART -> completed: ${completedTasks.value}, ongoing: ${ongoingTasks.value}, ready: ${readyTasks.value}")


                    //BUBBLE CHART
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
                }




                                                //AVG BAR BOX
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(100.dp)
                ){

                    if(isLoadingAvgBar.value == true){
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    else{
                        Log.d("DEBUG", "Media Daily Tasks: ${avgDailyCompletionTime.value}")
                        Log.d("DEBUG", "Media General Tasks: ${avgGeneralCompletionTime.value}")


                        // Liste Vuote -> OK
                        if(avgDailyCompletionTime.value == 0.0 && avgGeneralCompletionTime.value == 0.0){
                            WrapBox (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ){
                                AvgComplationBar(
                                    ratio = 0f,
                                    progressBarHeight = 42.dp,
                                    progressBarWidth = 250.dp,

                                    badgeTop = {},
                                    textTop = {},
                                    badgeBottom = {},
                                    textBottom = {
                                        Text(
                                            text = "not available yet",
                                            style = MaterialTheme.typography.labelSmall, //Poppins

                                            fontSize = 18.sp,
                                            fontWeight = FontWeight(400),

                                            color = Color(0xFF827676),
                                            modifier = Modifier
                                                .offset(x = (105).dp, y = (11).dp)
                                        )
                                    }
                                )
                            }
                        }
                        // Lista Today Vuota -> OK ( NB: se ho "completionTimeToday" non Vuota esiste per forza "completionTimeGeneral", non viceversa)
                        else if(avgDailyCompletionTime.value == 0.0){

                            WrapBox (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ){
                                AvgComplationBar(
                                    ratio = 1f, // Full Bar
                                    progressBarHeight = 42.dp,
                                    progressBarWidth = 250.dp,

                                    badgeTop = {
                                        StatStatusBadge(
                                            statusColor = Color(0xFF6326A9),
                                            statusText = String.format("%.2f min", avgGeneralCompletionTime.value),

                                            //Modifier aggiuntivi utili passabili
                                            modifier = Modifier
                                                .scale(0.6f)
                                                .offset(x = (-30).dp, y = (-30).dp)
                                        )
                                    },
                                    textTop = {
                                        Text(
                                            text = "(general)",
                                            style = MaterialTheme.typography.labelSmall, //Poppins

                                            fontSize = 13.sp,
                                            fontWeight = FontWeight(400),

                                            color = Color(0xFF827676),
                                            modifier = Modifier
                                                .offset(x = (30).dp, y = (-30).dp)
                                                .height(18.dp)
                                        )
                                    },

                                    badgeBottom = {
                                        StatStatusBadge(
                                            statusColor = Color(0xFFF5DFFA),
                                            statusText = "unavailable",
                                            //Modifier aggiuntivi utili passabili
                                            modifier = Modifier
                                                .scale(0.6f)
                                                .offset(x = (+315).dp, y = (+37).dp)
                                        )
                                    },
                                    textBottom = {
                                        Text(
                                            text = "(today)",
                                            style = MaterialTheme.typography.labelSmall, //Poppins

                                            fontSize = 13.sp,
                                            fontWeight = FontWeight(400),

                                            color = Color(0xFF827676),
                                            modifier = Modifier
                                                .offset(x = (258).dp, y = (59).dp)
                                        )
                                    }
                                )
                            }



                        }
                        // Entrambe liste Non Vuote -> OK
                        else{

                            // scelgo sempre la media minore da passare per prima, così da vedere sempre una differenza nella bar (e non sovrapposizioni)
                            if(avgDailyCompletionTime.value <= avgGeneralCompletionTime.value){

                                WrapBox (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                ){
                                    AvgComplationBar(
                                        ratio = (avgDailyCompletionTime.value/avgGeneralCompletionTime.value).toFloat(),
                                        progressBarHeight = 42.dp,
                                        progressBarWidth = 250.dp,

                                        badgeTop = {
                                            StatStatusBadge(
                                                statusColor = Color(0xFF6326A9),
                                                statusText = String.format("%.2f min", avgDailyCompletionTime.value),

                                                //Modifier aggiuntivi utili passabili
                                                modifier = Modifier
                                                    .scale(0.6f)
                                                    .offset(x = (-30).dp, y = (-30).dp)
                                            )
                                        },
                                        textTop = {
                                            Text(
                                                text = "(today)",
                                                style = MaterialTheme.typography.labelSmall, //Poppins

                                                fontSize = 13.sp,
                                                fontWeight = FontWeight(400),

                                                color = Color(0xFF827676),
                                                modifier = Modifier
                                                    .offset(x = (30).dp, y = (-30).dp)
                                                    .height(18.dp)
                                            )
                                        },

                                        badgeBottom = {
                                            StatStatusBadge(
                                                statusColor = Color(0xFFF5DFFA),
                                                statusText = String.format("%.2f min", avgGeneralCompletionTime.value),
                                                //Modifier aggiuntivi utili passabili
                                                modifier = Modifier
                                                    .scale(0.6f)
                                                    .offset(x = (+342).dp, y = (+37).dp)
                                                //.align(Alignment.TopStart)
                                            )
                                        },
                                        textBottom = {
                                            Text(
                                                text = "(general)",
                                                style = MaterialTheme.typography.labelSmall, //Poppins

                                                fontSize = 13.sp,
                                                fontWeight = FontWeight(400),

                                                color = Color(0xFF827676),
                                                modifier = Modifier
                                                    .offset(x = (258).dp, y = (59).dp)
                                            )
                                        }
                                    )
                                }

                            }
                            else{

                                WrapBox (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                ){
                                    AvgComplationBar(
                                        ratio = (avgGeneralCompletionTime.value/avgDailyCompletionTime.value).toFloat(),
                                        progressBarHeight = 42.dp,
                                        progressBarWidth = 250.dp,

                                        badgeTop = {
                                            StatStatusBadge(
                                                statusColor = Color(0xFF6326A9),
                                                statusText = String.format("%.2f min", avgGeneralCompletionTime.value),

                                                //Modifier aggiuntivi utili passabili
                                                modifier = Modifier
                                                    .scale(0.6f)
                                                    .offset(x = (-30).dp, y = (-30).dp)
                                            )
                                        },
                                        textTop = {
                                            Text(
                                                text = "(general)",
                                                style = MaterialTheme.typography.labelSmall, //Poppins

                                                fontSize = 13.sp,
                                                fontWeight = FontWeight(400),

                                                color = Color(0xFF827676),
                                                modifier = Modifier
                                                    .offset(x = (22).dp, y = (-30).dp)
                                                    .height(18.dp)
                                            )
                                        },

                                        badgeBottom = {
                                            StatStatusBadge(
                                                statusColor = Color(0xFFF5DFFA),
                                                statusText = String.format("%.2f min", avgDailyCompletionTime.value),
                                                //Modifier aggiuntivi utili passabili
                                                modifier = Modifier
                                                    .scale(0.6f)
                                                    .offset(x = (+342).dp, y = (+37).dp)
                                            )
                                        },
                                        textBottom = {
                                            Text(
                                                text = "(today)",
                                                style = MaterialTheme.typography.labelSmall, //Poppins

                                                fontSize = 13.sp,
                                                fontWeight = FontWeight(400),

                                                color = Color(0xFF827676),
                                                modifier = Modifier
                                                    .offset(x = (262).dp, y = (59).dp)
                                            )
                                        }
                                    )
                                }

                            }
                        }
                    }
                    
                }

            }

        }

    }

}


