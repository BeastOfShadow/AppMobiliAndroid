package it.uniupo.ktt.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.ui.components.DailyTasksBubbleChart
import it.uniupo.ktt.ui.components.PageTitle
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
            .background(MaterialTheme.colorScheme.background)
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
                fontWeight = FontWeight.Bold,


                color = titleColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // esempi valori:
                val completedTasks = 10
                val ongoingTasks = 20
                val readyTasks = 26

                //riferimenti traformazioni Dimensioni Cerchi
                val minTasks = 11
                val maxTasks = 25

                val minDimBubble = 35f
                val maxDimBubble = 80f



                DailyTasksBubbleChart( //COMPONENT: implementa logica
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
            }





        }
    }
}


@Preview(showBackground = true)
@Composable
fun CG_StatisticPagePreview() {
    CG_StatisticPage(navController = NavController(context = LocalContext.current))
}
