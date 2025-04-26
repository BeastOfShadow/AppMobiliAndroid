package it.uniupo.ktt.ui.pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.subtitleColor
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.UserViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.homePage.AvatarSticker
import it.uniupo.ktt.ui.firebase.BaseRepository


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(navController: NavController) {

    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("landing")
        {
            popUpTo("chat") { inclusive = true } // clear Stack
            launchSingleTop = true //precaricamento
        }
    }

    // istanza + collegamento
    val userViewModelRef = hiltViewModel<UserViewModel>()
    // osservabili
    val userRef by userViewModelRef.user.collectAsState()
    val isLoadingRef by userViewModelRef.isLoading.collectAsState()
    val errorRef by userViewModelRef.errorMessage.collectAsState()
    val avatarRef by userViewModelRef.avatarUrl.collectAsState()

    val userUid = BaseRepository.currentUid()

    if(userRef == null){
        LaunchedEffect (userUid){
            if(userUid!= null) {
                userViewModelRef.loadUserByUid(userUid)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        when {
            // wait to build PageUI ... (DB data not delivered yet)
            isLoadingRef -> {
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


                // controllo redirect iniziale
                if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn())  {
                    navController.navigate("landing") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }

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

                        // IMG PROFILO (AVATAR)
                        Log.e("DEBUG-HOME-AVATAR", "avatarUrl: $avatarRef")
                        AvatarSticker(avatarRef.toString())


                        Spacer(modifier = Modifier.height(20.dp))

                        // Title
                        Text(
                            text = " ${userVal?.name} ${userVal?.surname}",
                            style = MaterialTheme.typography.titleLarge, // This will use Poppins

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
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = " ${userVal?.role?.lowercase()}",
                                fontWeight = FontWeight.ExtraLight,
                                fontSize = 16.sp,
                                color = subtitleColor
                            )
                        }

                        Spacer(modifier = Modifier.height(65.dp))

                        Text(
                            text = "Main menu",
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = subtitleColor
                        )

                        MenuLabel(
                            navController = navController,
                            navPage = "task manager",
                            title = "Task Manager",
                            description = "Create, update, delete tasks and subtasks",
                            image = R.drawable.menu_task_new,
                            imageDescription = "Profile Icon"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        MenuLabel(
                            navController = navController,
                            navPage = "chat",
                            title = "Chat",
                            description = "Direct messages to your employees",
                            image = R.drawable.menu_chat,
                            imageDescription = "Chat Icon"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        MenuLabel(
                            navController = navController,
                            navPage = "CareGiver Statistic",
                            title = "Statistics",
                            description = "Check the work done by each employee",
                            image = R.drawable.menu_stats_a,
                            imageDescription = "Statistics Icon"
                        )

                    }
                }





            }
        }
    }




}

@Preview
@Composable
fun HomeScreenPreview() {
   HomeScreen(navController = NavController(context = LocalContext.current))
}
