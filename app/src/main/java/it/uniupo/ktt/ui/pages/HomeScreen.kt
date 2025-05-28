package it.uniupo.ktt.ui.pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.ui.components.MenuLabel
import it.uniupo.ktt.ui.theme.subtitleColor
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.UserViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uniupo.ktt.R
import it.uniupo.ktt.time.isToday
import it.uniupo.ktt.ui.components.homePage.AvatarSticker
import it.uniupo.ktt.ui.components.homePage.EP_ProgressBar
import it.uniupo.ktt.ui.components.homePage.ModalShowAvatars
import it.uniupo.ktt.ui.components.homePage.TargetButton
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.viewmodel.ChatViewModel
import it.uniupo.ktt.viewmodel.HomeScreenViewModel
import it.uniupo.ktt.viewmodel.TaskViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(navController: NavController) {

    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("login")
        {
            popUpTo("login") { inclusive = false }
            launchSingleTop = true //precaricamento
        }
    }

    val userUid = BaseRepository.currentUid()

    // Stato Dialog (visibilità del dello SHOW AVATAR MODAL -> (ON/OFF)) reattiva ai cambiamenti
    var showDialog by remember { mutableStateOf(false) }

    // TaskID Ongoing Daily Task (SOLO EMPLOYEE)
    var taskId by remember { mutableStateOf("") }

    // -------------------------------- VIEW MODEL REF -------------------------------------------
    val userViewModelRef = hiltViewModel<UserViewModel>() // USER
    val homeScreenViewModelRefHilt = hiltViewModel<HomeScreenViewModel>() // ENRICHED CHATS
    val viewModel: TaskViewModel = viewModel() // ONGING TASK

    // Observable
    val userRef by userViewModelRef.user.collectAsState()
    val isLoadingUserRef by userViewModelRef.isLoadingUser.collectAsState()
    val errorRef by userViewModelRef.errorMessage.collectAsState()
    val avatarRef by userViewModelRef.avatarUrl.collectAsState()
    val isLoadindEnrichedChats by homeScreenViewModelRefHilt.isLoadingEnrichedChats.collectAsState()
    val enrichedUserChatsList by homeScreenViewModelRefHilt.enrichedUserChatsList.collectAsState()
    // -------------------------------- VIEW MODEL REF -------------------------------------------


    // -------------------------------- LAUNCHED EFFECTS -------------------------------------------
    // GET USER
    LaunchedEffect(userUid) {
        if (userUid != null && userRef == null) {
            userViewModelRef.loadUserByUid(userUid)
        }

        // caso extra -> DELETE USER in DB,  mentre l'utente è Loggato
        if (!isLoadingUserRef && errorRef != null) {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // GET ALL USERCHATS (Enriched)
    LaunchedEffect(userUid) {
        if(userUid!= null) {
            homeScreenViewModelRefHilt.observeUserChats(userUid)
        }
    }
    // -------------------------------- LAUNCHED EFFECTS -------------------------------------------


    Box(
        modifier = Modifier.fillMaxSize(),
    ){
        when {
            // Wait User & Enrichedchats Loading before composing UI
            isLoadingUserRef || isLoadindEnrichedChats ->{
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            // query error
            errorRef != null -> {
                Log.e("DEBUG-HOME", "Errore delivery User")
            }
            // User not Found
            userRef == null ->{
                Log.e("DEBUG-HOME", "Errore User Loggato ma non Trovato")
            }
            // USER FOUND
            else -> {

                Log.d("DEBUG-HOME-CHATS", "EnrichedChatList:\n" + enrichedUserChatsList.joinToString("\n") {
                    "chatId=${it.chat.chatId}, name=${it.name}, surname=${it.surname}, lastMsg= ${it.chat.lastMsg}"
                })

                /*
                 *  copia in locale per smart cast sicuro (Kotlin non può garantire che non muti il suo
                 *  valore in NULL) , dato che in qualunque momento può cmabiare devo bloccare il suo val
                 *  in un istante preciso per poter fare il suo check:
                 *
                 *                              DA QUI IN POI USO "user"
                 */
                val userVal = userRef

                // parte comune
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilledIconButton(
                                onClick = {
                                    // navController.popBackStack()
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                modifier = Modifier.size(34.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowBackIosNew,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp))


                        // AVATAR
                        Log.e("DEBUG-HOME-AVATAR", "avatarUrl: $avatarRef")
                        AvatarSticker(
                            avatarRef.toString(),
                            onClick = { showDialog = true}
                        )

                        if (showDialog) {
                            Dialog(onDismissRequest = { showDialog = false }) {
                                ModalShowAvatars(
                                    onDismiss = { showDialog = false }
                                )
                            }
                        }


                        Spacer(modifier = Modifier.height(20.dp))

                        // CAREGIVER HOME-SCREEN
                        if(userVal?.role == "CAREGIVER"){
                            // Title
                            Text(
                                text = " ${userVal.name} ${userVal.surname}",

                                fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                                fontSize = 25.sp,
                                fontWeight = FontWeight(600),

                                color = titleColor,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.caregiver),
                                    contentDescription = "Endline",
                                    modifier = Modifier.size(25.dp)
                                )
                                Text(
                                    text = " ${userVal.role.lowercase()}",

                                    fontFamily = FontFamily(Font(R.font.poppins_extralight)),
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight(275),


                                    color = subtitleColor
                                )
                            }

                            Spacer(modifier = Modifier.height(65.dp))

                            Text(
                                text = "Main menu",

                                style = MaterialTheme.typography.bodyLarge, //Poppins
                                fontSize = 22.sp,
                                fontWeight = FontWeight(500),

                                color = subtitleColor,
                                modifier = Modifier.offset(x = 20.dp, y = 0.dp)
                            )

                            // CG_TASKMANAGER
                            MenuLabel(
                                navController = navController,
                                navPage = "task manager",
                                title = "Task Manager",
                                description = "Create, update, delete tasks and subtasks",
                                image = R.drawable.menu_task_new,
                                imageDescription = "Profile Icon"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // CG_CHATS
                            MenuLabel(
                                navController = navController,
                                navPage = "chat",
                                title = "Chat",
                                description = "Direct messages to your employees",
                                image = R.drawable.menu_chat,
                                imageDescription = "Chat Icon"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // CG_STATISTICS
                            MenuLabel(
                                navController = navController,
                                navPage = "CareGiver Statistic",
                                title = "Statistics",
                                description = "Check the work done by each employee",
                                image = R.drawable.menu_stats_a,
                                imageDescription = "Statistics Icon"
                            )
                        }
                        // EMPLOYEE HOME-SCREEN
                        else{
                            LaunchedEffect(userUid) {
                                if (userRef?.role == "EMPLOYEE" && userUid != null) {
                                    val todayTasks = viewModel.getTasksByEmployeeId(userUid).filter { isToday(it.timeStampStart) }
                                    Log.e("TODAY-TASK", "todayTasks: $todayTasks")
                                    val ongoingTask = todayTasks.filter { it.active }

                                    taskId = ongoingTask.firstOrNull()?.id.toString()
                                }
                            }
                            // 1) call DB per in cerca di SUBTASK ongoing by UserId
                            // 2) IF(CurrentSubTask Ongoing esiste) -> SHOW TARGET-BUTTON
                            // rendi visibile il TargetButton

                            // Title
                            Text(
                                text = " ${userVal?.name} ${userVal?.surname}",

                                fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                                fontSize = 25.sp,
                                fontWeight = FontWeight(600),

                                color = titleColor,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Text(
                                text = " ${userVal?.role?.lowercase()}",

                                fontFamily = FontFamily(Font(R.font.poppins_extralight)),
                                fontSize = 23.sp,
                                fontWeight = FontWeight(275),

                                color = subtitleColor,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // PROGRESS BAR
                            EP_ProgressBar()


                            Text(
                                text = "Main menu",

                                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                fontSize = 22.sp,
                                fontWeight = FontWeight(400),

                                color = Color(0xFF403E3E),
                                modifier = Modifier.offset(x = 20.dp, y = 0.dp)
                            )


                            // EP_DAILYTASKS
                            MenuLabel(
                                navController = navController,
                                navPage = "daily task",
                                title = "Daily Tasks",
                                description = "Today's activities in a smart list",
                                image = R.drawable.menu_task_list,
                                imageDescription = "Profile Icon"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // EP_CHAT
                            MenuLabel(
                                navController = navController,
                                navPage = "chat",
                                title = "Chat",
                                description = "Direct messages to your Supervisors",
                                image = R.drawable.menu_chat,
                                imageDescription = "Chat Icon"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // EP_STATISTICS
                            MenuLabel(
                                navController = navController,
                                navPage = "Employee Statistic",
                                title = "Statistics",
                                description = "Check all your work done and the next goals!",
                                image = R.drawable.menu_stats_b,
                                imageDescription = "Statistics Icon"
                            )
                        }
                    }
                }
            }
        }

        if (taskId != "" && taskId != "null") // più esplicito
        {
            Box(
                modifier = Modifier
                    .size(width = 70.dp, height = 60.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = -(40).dp, y = 30.dp)
            ) {
                TargetButton(
                    modifier = Modifier
                        .scale(1.15f)
                        .alpha(1f),
                    onClick = {
                        navController.navigate("current_subtask/${taskId}",)
                    }
                )
            }
        }

    }
}

@Preview
@Composable
fun HomeScreenPreview() {
   HomeScreen(navController = NavController(context = LocalContext.current))
}
