package it.uniupo.ktt.ui.pages.caregiver.statistics

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
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.ui.components.statistics.AvgComplationBar
import it.uniupo.ktt.ui.components.statistics.DailyTasksBubbleChart
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.statistics.StatStatusBadge
import it.uniupo.ktt.ui.theme.titleColor


@Composable
fun CG_StatisticPage(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
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
                // esempi valori(in futuro ottienili da DB e calcolali):
                val completedTasks = 26
                val ongoingTasks = 26 //16
                val readyTasks = 30

                //riferimenti Chiave traformazioni Dimensioni Cerchi (NON MODIFICARE)
                val minTasks = 11
                val maxTasks = 25

                val minDimBubble = 35f
                val maxDimBubble = 80f


                //BubbleChart COMPONENT
                DailyTasksBubbleChart(
                    completed = when {
                        completedTasks < minTasks -> minDimBubble //caso dimensione Bubble minima 35f
                        completedTasks > maxTasks -> maxDimBubble //caso dimensione Bubble massima 80f
                        else -> 35f + ((completedTasks - 10) *3f)         //caso intermedio Bubble
                    },
                    ongoing = when {
                        ongoingTasks < minTasks -> minDimBubble //caso dimensione Bubble minima 35f
                        ongoingTasks > maxTasks -> maxDimBubble //caso dimensione Bubble massima 80f
                        else -> 35f + ((ongoingTasks - 10) *3f)         //caso intermedio Bubble
                    },
                    ready = when {
                        readyTasks < minTasks -> minDimBubble //caso dimensione Bubble minima 35f
                        readyTasks > maxTasks -> maxDimBubble //caso dimensione Bubble massima 80f
                        else -> 35f + ((readyTasks - 10) *3f)         //caso intermedio Bubble
                    },
                    completedNumb = completedTasks,
                    onGoingNumb = ongoingTasks,
                    readyNumb = readyTasks
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

                    // esempi valori(in futuro ottienili da DB e calcolali):
                    val completionTimeToday = 16.41
                    val completionTimeGeneral = 26.55
                    val ratioTime = completionTimeToday/completionTimeGeneral


                    //BAR
                    AvgComplationBar(
                        todayTime = completionTimeToday.toFloat(),
                        generalTime = completionTimeGeneral.toFloat(),
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
                        style = MaterialTheme.typography.bodyLarge, //Poppins

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
                            .offset(x = (252).dp, y = (55).dp)
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

