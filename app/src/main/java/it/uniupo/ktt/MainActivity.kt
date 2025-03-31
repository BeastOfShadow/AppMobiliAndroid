package it.uniupo.ktt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.ui.pages.Caregiver.Chat.ChatPage
import it.uniupo.ktt.ui.pages.Caregiver.Statistics.CG_StatisticPage
import it.uniupo.ktt.ui.pages.HomeScreen
import it.uniupo.ktt.ui.pages.LandingScreen
import it.uniupo.ktt.ui.pages.LoginScreen
import it.uniupo.ktt.ui.pages.Caregiver.Chat.NewChatPage
import it.uniupo.ktt.ui.pages.NewTaskScreen
import it.uniupo.ktt.ui.pages.RegisterScreen
import it.uniupo.ktt.ui.pages.StatisticsScreen
import it.uniupo.ktt.ui.pages.TaskManagerScreen
import it.uniupo.ktt.ui.theme.KTTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FirebaseApp.initializeApp(this)
            val navController = rememberNavController()
            val startDestination by remember { mutableStateOf(if (FirebaseAuth.getInstance().currentUser == null) "landing" else "home") }

            KTTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("landing") { LandingScreen(navController) }
                        composable("login") { LoginScreen(navController) }
                        composable("register") { RegisterScreen(navController) }
                        composable("home") { HomeScreen(navController) }
                        composable("task manager") { TaskManagerScreen(navController) }
                        composable("statistics") { StatisticsScreen(navController) }
                        composable("new task") { NewTaskScreen(navController) }

                                                        //new ROUTE
                        //Statistics
                        composable("CareGiver Statistic") { CG_StatisticPage(navController) }
                        //Chats
                        composable("chat") { ChatPage(navController) }
                        composable("new chat") { NewChatPage(navController) }
                    }
                }
            }
        }
    }
}