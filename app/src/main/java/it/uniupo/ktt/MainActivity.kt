package it.uniupo.ktt

import android.content.pm.PackageManager
import android.os.Build
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
import it.uniupo.ktt.ui.pages.caregiver.chat.ChatPage
import it.uniupo.ktt.ui.pages.caregiver.statistics.CG_StatisticPage
import it.uniupo.ktt.ui.pages.HomeScreen
import it.uniupo.ktt.ui.pages.LoginScreen
import it.uniupo.ktt.ui.pages.caregiver.chat.NewChatPage
import it.uniupo.ktt.ui.pages.CommentSubtaskScreen
import it.uniupo.ktt.ui.pages.NewTaskScreen
import it.uniupo.ktt.ui.pages.RegisterScreen
import it.uniupo.ktt.ui.pages.TaskManagerScreen
import it.uniupo.ktt.ui.pages.UpdateSubtaskScreen
import it.uniupo.ktt.ui.pages.caregiver.chat.ChatOpen
import it.uniupo.ktt.ui.theme.KTTTheme

import androidx.navigation.NavType
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import it.uniupo.ktt.ui.pages.employee.currentTask.CurrentSubtaskPage
import it.uniupo.ktt.ui.pages.employee.statistics.EP_StatisticPage
import it.uniupo.ktt.ui.pages.TaskRatingScreen
import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.pages.caregiver.taskmanager.VisualizeRatedTaskScreen
import it.uniupo.ktt.ui.pages.employee.currentTask.SubTaskViewScreen
import it.uniupo.ktt.ui.pages.employee.taskmanager.DailyTaskScreen
import it.uniupo.ktt.ui.pages.employee.taskmanager.ViewTaskScreen
import it.uniupo.ktt.viewmodel.HomeScreenViewModel

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkChatUnread
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val startDestination by remember { mutableStateOf(if (BaseRepository.currentUser() == null) "login" else "home") }


            // -------------------- VIEWMODEL GLOBALE (lifetime = MainActivity) ---------------------

            /*
            *       APP in FOREGROUND:
            *
            *       Istanzio qui il ViewModel per permettere che sia disponibile e attivo navigando in tutte le
            *       Page dell'app, in questo modo la EnrichedChatList sarà sempre attiva e pronta ad aggiornamenti
            *       così che lutente abbia sempre il Badge Dispo ovunque si trovi.
            *
            *       Nel momento in cui l'app va in BackGround il Listener Cade per dare spazio al meccanismo
            *       PUSH notify + FCM.
            *
            * */
            val currentEntry by navController.currentBackStackEntryAsState()

            // (solo se è istanziato l'HomeScreenViewModel)
            val homeVM = if (currentEntry?.destination?.route == "home") {
                hiltViewModel<HomeScreenViewModel>()
            } else null

            val highlightedChat = homeVM?.highlightedChat?.collectAsState()?.value
            // -------------------- VIEWMODEL GLOBALE (lifetime = MainActivity) ---------------------


            KTTTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    // ------------- BADGE NEW MESSAGE ------------- (elemento UI Globale)
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (highlightedChat != null) {
                            val currentUid = BaseRepository.currentUid()
                            val otherUid = if (highlightedChat.chat.caregiver == currentUid)
                                highlightedChat.chat.employee
                            else
                                highlightedChat.chat.caregiver

                            FloatingActionButton(
                                onClick = {
                                    navController.navigate("chat open/${highlightedChat.chat.chatId}/$otherUid")
                                    homeVM.clearHighlightedChat()
                                },
                                containerColor = Color(0xFF9C27B0),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(end = 24.dp, top = 32.dp)
                            ) {
                                Icon(Icons.Default.MarkChatUnread, contentDescription = "New Message")
                            }
                        }
                    }
                    // ------------- BADGE NEW MESSAGE  -------------


                    // -------------------------- NAVIGATION --------------------------
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // REWORKED
                        composable("login") { LoginScreen(navController) }
                        composable("register") { RegisterScreen(navController) }
                        composable("home") { HomeScreen(navController) }

                        composable("task manager") { TaskManagerScreen(navController) }
                        composable("new task") { NewTaskScreen(navController) }
                        composable(
                            route = "task_rating/{taskId}",
                            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""

                            TaskRatingScreen(navController = navController, taskId = taskId)
                        }
                        composable(
                            route = "rated_task/{taskId}",
                            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""

                            VisualizeRatedTaskScreen(navController = navController, taskId = taskId)
                        }
                        composable("daily task") { DailyTaskScreen(navController) }
                        composable(
                            route = "view_task/{taskId}",
                            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""

                            ViewTaskScreen(navController = navController, taskId = taskId)
                        }
                        composable(
                            route = "subtask_view/{taskId}/{subtaskId}",
                            arguments = listOf(
                                navArgument("taskId") { type = NavType.StringType },
                                navArgument("subtaskId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                            val subtaskId = backStackEntry.arguments?.getString("subtaskId") ?: ""

                            SubTaskViewScreen(
                                navController = navController,
                                taskId = taskId,
                                subtaskId = subtaskId
                            )
                        }

                        //new ROUTE
                        composable("update subtask") { UpdateSubtaskScreen(navController) }
                        composable("comment subtask") { CommentSubtaskScreen(navController) }


                        //CurrentSubtask (Target)
                        composable(
                            route = "current_subtask/{taskId}",
                            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""

                            CurrentSubtaskPage(navController = navController, taskId = taskId)
                        }

                        //Statistics
                        composable("CareGiver Statistic") { CG_StatisticPage(navController) }
                        composable("Employee Statistic") { EP_StatisticPage(navController) }
                        //Chats
                        composable("chat") { ChatPage(navController) }
                        composable("new chat") { NewChatPage(navController) }
                        composable(
                            "chat open/{chatId}/{uidContact}", // Route che accetta 2 PARAM
                            arguments = listOf(
                                navArgument("chatId") { type = NavType.StringType }, // Def PARAM1 type
                                navArgument("uidContact") {type = NavType.StringType} // Def PARAM2 type
                            )

                        ) { backStackEntry ->
                            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                            val uidContact = backStackEntry.arguments?.getString("uidContact") ?: ""

                            ChatOpen(navController, chatId, uidContact) // Passaggio dei  PARAM
                        }
                    }
                    // -------------------------- NAVIGATION --------------------------

                }
            }
        }
    }
}