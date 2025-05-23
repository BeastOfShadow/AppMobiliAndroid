package it.uniupo.ktt.ui.pages.employee.currentTask

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.theme.titleColor

@Composable
fun CurrentSubtaskPage(navController: NavController, taskId: String) {
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("login") {
            popUpTo("login") { inclusive = false } // rimuovi tutte le Page nello Stack fino a Landing senza eliminare quest'ultima
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
                title = "Current Subtask"
            )
            Spacer(
                modifier = Modifier.height(42.dp)
            )

            Text(
                text = "CURRENT SUBTASK Prova",
                style = MaterialTheme.typography.bodyLarge, //Poppins

                fontSize = 22.sp,
                fontWeight = FontWeight(500),


                color = titleColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )



        }

    }

}