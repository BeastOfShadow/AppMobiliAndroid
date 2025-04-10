package it.uniupo.ktt.ui.pages.caregiver.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.firebase.BaseRepository

@Composable
fun ChatOpen(navController: NavController, chatId: String, uidContact: String) {
    //Controllo Login
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("landing")
        {
            popUpTo("chat") { inclusive = true } //animazione
            launchSingleTop = true //precaricamento
        }
    }

    Log.d("DEBUG-CHATOPEN-INPUT-PARAMS", "chatId: $chatId, uidCOntact: $uidContact")


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(20.dp)
    ) {
        Column() {
            PageTitle(
                navController = navController,
                title = "Chat"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // PAGE (NO PAGE-TITLE)
            Box(
                modifier = Modifier
                    //.shadow(8.dp, RoundedCornerShape(20.dp), clip = false)
                    .padding(4.dp)
                    .background(Color(0xFFF5DFFA), shape = RoundedCornerShape(10))
                    .fillMaxSize()
            ){

                // UI-> ModelView Behaviour
//                when {
//                    // wait to build PageUI (DB data not delivered yet)
//                    isLoadingRef -> {
//                        CircularProgressIndicator(Modifier.align(Alignment.Center))
//                    }
//                    // query error
//                    errorRef != null -> {
//                        Log.e("DEBUG-ModelView", "Errore delivery Lista Messaggi")
//                    }
//                    // lista Messaggi recieved vuota o non vuota (0+ Msg al momento)
//                    else -> {
//
//                        // ordina la lista in base alla data dell'ultimo messaggio
//                        val sortedMsgList = messagesRef.sortedBy { it.timeStamp }
//
//                        Box(
//                            modifier = Modifier
//                                .offset(x = 50.dp, y = 110.dp)
//                                .heightIn(max = 555.dp)
//                                .verticalScroll(rememberScrollState())
//                        ){
//                            Column{
//                                // itera lista e crea Lable contatti
//                                sortedMsgList.forEach { msg ->
//                                    MsgLabel(
//                                        sender = msg.sender,
//                                        text = msg.text,
//                                        timeStamp = msg.timeStamp,
//                                        seen = msg.seen
//                                    )
//                                    Spacer(
//                                        modifier = Modifier
//                                            .height(5.dp)
//                                    )
//                                }
//
//                            }
//                        }
//
//                    }
//                }


            }
        }


    }
}

@Preview
@Composable
fun ChatOpenPreview() {
    //ChatOpen(navController = NavController(context = LocalContext.current))
}
