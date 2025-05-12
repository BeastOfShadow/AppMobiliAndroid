package it.uniupo.ktt.ui.pages.employee.statistics

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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.statistics.AvgComplationBar
import it.uniupo.ktt.ui.components.statistics.CircleDiagram
import it.uniupo.ktt.ui.components.statistics.StatStatusBadge
import it.uniupo.ktt.ui.components.statistics.TodayBar
import it.uniupo.ktt.ui.components.statistics.WrapBox
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.StatisticsViewModel

@Composable
fun EP_StatisticPage(navController: NavController) {
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("landing") {
            popUpTo("landing") { inclusive = false } // rimuovi tutte le Page nello Stack fino a Landing senza eliminare quest'ultima
            launchSingleTop = true
        }
    }

    // istanza + collegamento
    val statisticsViewModelRef = hiltViewModel<StatisticsViewModel>()

                                    // AVG BAR INFO
    // Durata Media dei lavori "completed" svolti oggi (dall'employee)
    val avgDailyCompletionTime = remember { mutableStateOf(0.0) }
    // Durata Media dei lavori "completed" svolti in generale (dall'employee)
    val avgGeneralCompletionTime = remember { mutableStateOf(0.0) }

                                    // TODAY BAR INFO
    // Task daily
    val dailyCount = remember { mutableStateOf(0) }
    // Task daily Completati
    val dailyCompletedCount = remember { mutableStateOf(0) }

                                    // CIRCLE DIAGRAM INFO
    // Tasks Completed this Year
    val yearlyCompletedCount = remember { mutableStateOf(0) }
    // Tasks Completed this Month
    val monthlyCompletedCount = remember { mutableStateOf(0) }
    // Tasks Completed this Week
    val weeklyCompletedCount = remember { mutableStateOf(0) }


                            // BOOLEAN INFO FLAGs (Loading)
    val isLoadingAvgBarInfo = remember { mutableStateOf(false) }
    val isLoadingTodayBarInfo = remember { mutableStateOf(false) }
    val isLoadingCircleDiagramInfo = remember { mutableStateOf(false) }

    val currentUid = BaseRepository.currentUid()




    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF3FF))
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



                                        //CIRCLE DIAGRAM BOX
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ){

                // INFO CIRCLE DIAGRAM
                LaunchedEffect(currentUid) {
                    isLoadingCircleDiagramInfo.value= true

                    if (currentUid != null) {
                        statisticsViewModelRef.circleDiagramInfo(
                            uid = currentUid,
                            onSuccess = { yearly, monthly, weekly ->
                                yearlyCompletedCount.value = yearly
                                monthlyCompletedCount.value = monthly
                                weeklyCompletedCount.value = weekly

                                isLoadingCircleDiagramInfo.value = false
                            },
                            onError = { e ->
                                Log.e("DEBUG", "Errore circleDiagramInfo", e)

                                isLoadingCircleDiagramInfo.value = false
                            }
                        )
                    }
                }

                if(isLoadingCircleDiagramInfo.value == true){
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                else{
                    Log.d("DEBUG", "Yearly: ${yearlyCompletedCount.value}, Montly: ${monthlyCompletedCount.value}, Weekly: ${weeklyCompletedCount.value}")

                    WrapBox (
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ){
                        // Circle Diagram
                        CircleDiagram(
                            yearly = 85,
                            monthly = 25,
                            weekly = 6

//                            yearly =  yearlyCompletedCount.value,
//                            monthly =  monthlyCompletedCount.value,
//                            weekly = weeklyCompletedCount.value
                        )
                    }

                }

            }

            Spacer(
                modifier = Modifier.height(35.dp)
            )

                                        //TODAY BAR BOX
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ){

                // INFO TODAY BAR
                LaunchedEffect(currentUid) {
                    isLoadingTodayBarInfo.value= true

                    if (currentUid != null) {
                        statisticsViewModelRef.todayBarInfo(
                            uid = currentUid,
                            onSuccess = { all, completed ->
                                dailyCount.value = all
                                dailyCompletedCount.value = completed

                                isLoadingTodayBarInfo.value = false
                            },
                            onError = { e ->
                                Log.e("DEBUG", "Errore todayBarInfo", e)

                                isLoadingTodayBarInfo.value = false
                            }
                        )
                    }
                }

                if(isLoadingTodayBarInfo.value == true){
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                else{
                    Log.d("DEBUG", "All Daily Tasks: ${dailyCount.value}, All Completed Daily Tasks: ${dailyCompletedCount.value}")


                    WrapBox (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ){
                        // Today Bar
                        TodayBar(
                            0.6f,
                            5,10

//                            dailyCount.value,
//                            dailyCompletedCount.value
                        )
                    }

                }

            }

            Spacer(
                modifier = Modifier.height(35.dp)
            )

                                        //AVG BAR BOX
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ){



                // INFO AVG BAR
                LaunchedEffect(currentUid) {
                    isLoadingAvgBarInfo.value= true

                    if (currentUid != null) {
                        statisticsViewModelRef.avgCompletionBarInfo(
                            role = "caregiver",
                            onSuccess = { avgDaily, avgGeneral ->
                                avgDailyCompletionTime.value = avgDaily
                                avgGeneralCompletionTime.value = avgGeneral

                                isLoadingAvgBarInfo.value = false
                            },
                            onError = { e ->
                                Log.e("DEBUG", "Errore avgCompletionBarInfo", e)

                                isLoadingAvgBarInfo.value = false
                            }
                        )
                    }
                }

                if(isLoadingAvgBarInfo.value == true){
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