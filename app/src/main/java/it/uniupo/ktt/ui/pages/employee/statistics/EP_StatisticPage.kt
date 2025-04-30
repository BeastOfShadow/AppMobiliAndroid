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
import androidx.navigation.NavController
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.statistics.AvgComplationBar
import it.uniupo.ktt.ui.components.statistics.DailyTasksBubbleChart
import it.uniupo.ktt.ui.components.statistics.StatStatusBadge
import it.uniupo.ktt.ui.components.statistics.WrapBox
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.StatisticsRepository
import it.uniupo.ktt.ui.theme.titleColor

@Composable
fun EP_StatisticPage(navController: NavController) {
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("landing") {
            popUpTo("landing") { inclusive = false } // rimuovi tutte le Page nello Stack fino a Landing senza eliminare quest'ultima
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
                text = "Statistic Employee Prova",
                style = MaterialTheme.typography.bodyLarge, //Poppins

                fontSize = 22.sp,
                fontWeight = FontWeight(500),


                color = titleColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )



        }

    }

}