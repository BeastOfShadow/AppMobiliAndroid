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
import androidx.compose.ui.window.Dialog
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.homePage.AvatarSticker
import it.uniupo.ktt.ui.components.homePage.EP_ProgressBar
import it.uniupo.ktt.ui.components.homePage.ModalShowAvatars
import it.uniupo.ktt.ui.components.homePage.TargetButton
import it.uniupo.ktt.ui.firebase.BaseRepository


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(navController: NavController) {

    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("landing")
        {
            popUpTo("landing") { inclusive = false }
            launchSingleTop = true //precaricamento
        }
    }

    //Stato Dialog (visibilità del del ModalAddContact (ON/OFF)) reattiva ai cambiamenti
    var showDialog by remember { mutableStateOf(false) }

    // istanza + collegamento
    val userViewModelRef = hiltViewModel<UserViewModel>()
    // osservabili
    val userRef by userViewModelRef.user.collectAsState()
    val isLoadingUserRef by userViewModelRef.isLoadingUser.collectAsState()
    val errorRef by userViewModelRef.errorMessage.collectAsState()
    val avatarRef by userViewModelRef.avatarUrl.collectAsState()

    val userUid = BaseRepository.currentUid()

    // targetButton visibility
    var showTargetButton by remember { mutableStateOf(false) }

    if(userRef == null){
        LaunchedEffect (userUid){
            if(userUid!= null) {
                userViewModelRef.loadUserByUid(userUid)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ){
        when {
            // wait to build PageUI ... (DB data not delivered yet)
            isLoadingUserRef -> {
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

                /*
                 *  copia in locale per smart cast sicuro (Kotlin non può garantire che non muti il suo
                 *  valore in NULL) , dato che in qualunque momento può cmabiare devo bloccare il suo val
                 *  in un istante preciso per poter fare il suo check:
                 *
                 *                              DA QUI IN POI USO "user"
                 */
                val userVal = userRef

                // partr comune
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
                                    navController.navigate("landing") {
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

                                style = MaterialTheme.typography.bodyLarge, // This will use Poppins
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

                                    style = MaterialTheme.typography.bodySmall, //Poppins
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight(300),


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
                            // 1) call DB per in cerca di SUBTASK ongoing by UserId
                            // 2) IF(CurrentSubTask Ongoing esiste) -> SHOW TARGET-BUTTON
                            // rendi visibile il TargetButton

                            // Title
                            Text(
                                text = " ${userVal?.name} ${userVal?.surname}",

                                style = MaterialTheme.typography.bodyLarge, // This will use Poppins
                                fontSize = 25.sp,
                                fontWeight = FontWeight(600),

                                color = titleColor,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Text(
                                text = " ${userVal?.role?.lowercase()}",

                                style = MaterialTheme.typography.bodySmall, //Poppins
                                fontSize = 23.sp,
                                fontWeight = FontWeight(300),

                                color = subtitleColor,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // PROGRESS BAR
                            EP_ProgressBar()


                            Text(
                                text = "Main menu",

                                style = MaterialTheme.typography.bodyLarge, //Poppins
                                fontSize = 22.sp,
                                fontWeight = FontWeight(500),

                                color = subtitleColor,
                                modifier = Modifier.offset(x = 20.dp, y = 0.dp)
                            )


                            // EP_DAILYTASKS
                            MenuLabel(
                                navController = navController,
                                navPage = "task manager",
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

        // TARGET-BUTTON
        Box(
            modifier = Modifier
                .size(width = 70.dp, height = 60.dp)
                .align(Alignment.TopEnd)
                .offset(x = -(40).dp, y = 30.dp)
        ){
            TargetButton(
                modifier = Modifier
                    .scale(1.15f)
                    .alpha(if (showTargetButton) 1f else 0f),
                onClick = {
                    navController.navigate("current subtask")
                }
            )
        }

    }


}

@Preview
@Composable
fun HomeScreenPreview() {
   HomeScreen(navController = NavController(context = LocalContext.current))
}
