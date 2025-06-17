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
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.pages.caregiver.taskmanager.UpdateOngoingTaskScreen
import it.uniupo.ktt.ui.pages.caregiver.taskmanager.UpdateReadyTaskScreen
import it.uniupo.ktt.ui.pages.caregiver.taskmanager.VisualizeRatedTaskScreen
import it.uniupo.ktt.ui.pages.employee.currentTask.SubTaskViewScreen
import it.uniupo.ktt.ui.pages.employee.taskmanager.DailyTaskScreen
import it.uniupo.ktt.ui.pages.employee.taskmanager.ViewTaskScreen
import it.uniupo.ktt.viewmodel.HomeScreenViewModel
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.currentBackStackEntryAsState
import it.uniupo.ktt.ui.components.global.foregroundBadge


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
            *       Nel momento in cui l'app va in BACKGROUND Or LOGOUT il Listener Cade per dare spazio al meccanismo
            *       PUSH notify + FCM.
            *
            */
            val homeVM: HomeScreenViewModel = hiltViewModel() // -> ENRICHED CHATS (globalmente dispo nell'APP)
            val highlightedChat by homeVM.highlightedChat.collectAsState()

            val lifecycleOwner = LocalLifecycleOwner.current

            // Verifica del "CurrentBackStack" -> DON'T SHOW BADGE quando sono in "ChatOpen"
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val isInChatOpen = currentRoute?.startsWith("chat open/") == true
            // Comunica variazione (sono/non sono in ChatOpen)
            LaunchedEffect(isInChatOpen) {
                homeVM.setChatOpen(isInChatOpen)
            }

            // ------ *** LIFE-CYCLE -> LISTENER CHAT *** ------
            /*
            *                       DISPOSABLE-EFFECT & CALL-OCCORRENZA (MIX):
            *
            *       Combiniamo 2 Logiche per permettere...
            *
            *               1) GENERAZIONE-LISTENER con (APP-FOREGROUNG(ON_START) && uid!=NULL) or
            *                  (APP-FOREGROUNG(ON_START) && uid==NULL) se ne occupa dopo il Login
            *                  la HomeScreen.
            *
            *               2) DELETE-LISTENER dopo LOGOUT(se ne occupa direttamente la HomeScreen all'onClick)
            *                  or APP-BACKGROUND(ON_STOP)
            *
            *
            *       EXTRA: "DisposableEffect" viene usato per registrare un OSSERVATORE "LifecycleEventObserver"
            *              che permette di monitorare gli eventi "ON_START(entrata in APP)" e "ON_STOP(uscita dall'APP)"
            *
            *              In questo modo "ON_START" -> genero Listener, "ON_STOP" -> elimino Listener
            *
            */
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    val uid = BaseRepository.currentUid()
                    if (uid != null) {
                        when (event) {
                            Lifecycle.Event.ON_START -> {
                                Log.d("Lifecycle", "ON_START: Listener acceso")
                                homeVM.observeUserChats(uid)
                                homeVM.observeUserTasks(uid)
                            }
                            Lifecycle.Event.ON_STOP -> {
                                Log.d("Lifecycle", "ON_STOP: Listener spento")
                                homeVM.stopObservingChats()
                                homeVM.stopObservingTasks()
                            }
                            else -> {}
                        }
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            // ------ *** LYFE-CYCLE -> LISTENER CHAT *** ------

            // -------------------- VIEWMODEL GLOBALE (lifetime = MainActivity) ---------------------


            KTTTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    // ------------- BADGE NEW MESSAGE ------------- (elemento UI Globale)
                    if (highlightedChat != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(10f) // Primo Piano
                        ) {
                            foregroundBadge(
                                highlightedChat = highlightedChat,
                                onDismiss = { homeVM.clearHighlightedChat() },
                                onClick = { chatId, otherUid ->

                                    /*
                                    *     LOGICA:
                                    *       dato che tornando indietro dalla "ChatOpen" viene ricercato nello
                                    *       NavStackBackEntry "ChatPage", se non esiste lo creo al volo senza
                                    *       mostrarlo all'utente.
                                    *
                                    *       In questo modo lo Stack viene mantenuto coerente e anche la navigazione
                                    *       in ritorno
                                    *
                                    *     PULIZIA BADGE:
                                    *       così dopo il Press del Badge viene subito tolto
                                    */
                                    // 1) inserimento "ChatPage" nello Stack (se non esistente)
                                    navController.navigate("chat") {
                                        launchSingleTop = true // evita duplicati
                                    }

                                    // 2) goto "ChatOpen"
                                    navController.navigate("chat open/$chatId/$otherUid")

                                    // 3) pulizia Badge
                                    homeVM.clearHighlightedChat()
                                },
                                modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp)
                            )
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
                        composable("home") { HomeScreen(navController, homeVM) }

                        composable("task manager") { TaskManagerScreen(navController, homeVM) }
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
                        composable("daily task") { DailyTaskScreen(navController, homeVM) }
                        composable(
                            route = "view_task/{taskId}",
                            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""

                            ViewTaskScreen(navController = navController, taskId = taskId, homeVM)
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
                                subtaskId = subtaskId,
                                homeVm = homeVM
                            )
                        }

                        composable(
                            route = "comment_subtask/{taskId}/{subtaskId}",
                            arguments = listOf(
                                navArgument("taskId") { type = NavType.StringType },
                                navArgument("subtaskId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                            val subtaskId = backStackEntry.arguments?.getString("subtaskId") ?: ""

                            CommentSubtaskScreen(
                                navController = navController,
                                taskId = taskId,
                                subtaskId = subtaskId,
                                homeVm = homeVM
                            )
                        }

                        composable(
                            route = "update_ready_task/{taskId}",
                            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""

                            UpdateReadyTaskScreen(navController = navController, taskId = taskId)
                        }

                        composable(
                            route = "update_ongoing_task/{taskId}",
                            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""

                            UpdateOngoingTaskScreen(navController = navController, taskId = taskId, homeVm = homeVM)
                        }

                        //new ROUTE
                        composable("update subtask") { UpdateSubtaskScreen(navController) }


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
                        composable("chat") { ChatPage(navController, homeVM) }
                        composable("new chat") { NewChatPage(navController, homeVM) }
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