package it.uniupo.ktt.ui.pages.caregiver.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.chats.ChatContactLable
import it.uniupo.ktt.ui.components.chats.ChatSearchBar
import it.uniupo.ktt.ui.components.chats.NewChatButton
import androidx.compose.runtime.*
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.ChatRepository
import it.uniupo.ktt.ui.model.Chat


@Composable
fun ChatPage(navController: NavController) {
    //Controllo Login
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing")
        {
            popUpTo("chat") { inclusive = true } //animazione
            launchSingleTop = true //precaricamento
        }
    }

    // CALL DB (prendi lista Chat dell'utente)
    val chatsList = remember { mutableStateOf<List<Chat>>(emptyList())}
    val currentUid = BaseRepository.currentUid()

    LaunchedEffect(currentUid){
        if(currentUid != null){
            ChatRepository.getAllChatsByUid(
                uid = currentUid,
                onSuccess = { chats ->
                    Log.d("DEBUG", "Lista Chats: ${chats.joinToString(separator = "\n")}")
                    Log.d("DEBUG", "numero Chats: ${chats.size}")
                    chatsList.value = chats.filter { it.isValid() }
                },
                onError = { e ->
                    Log.e("DEBUG", "Errore ChatPage -> getAllConntactsByUid query", e)
                }
            )
        }
    }

    //creazione
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(20.dp)
            //.verticalScroll(rememberScrollState())
    ) {
        Column() {
            PageTitle(
                navController = navController,
                title = "Chat"
            )

            Spacer(modifier = Modifier.height(20.dp))

                                        //Page
            Box(
                modifier = Modifier
                    //.shadow(8.dp, RoundedCornerShape(20.dp), clip = false)
                    .padding(4.dp)
                    .background(Color(0xFFF5DFFA), shape = RoundedCornerShape(10))
                    .fillMaxSize()
            ){

                if(chatsList.value.isEmpty()){
                    Text(
                        text = buildAnnotatedString {
                            append("Press ")

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("new chat icon")
                            }

                            append(" to start a conversation!")
                        },
                        style = MaterialTheme.typography.bodyMedium, //Poppins


                        //letterSpacing = 1.sp,
                        fontSize = 22.sp,
                        fontWeight = FontWeight(400),
                        lineHeight = 34.sp,



                        color = Color(0xFF423C3C),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            //.align(Alignment.Center)
                            .offset(x = 0.dp, y = 280.dp)
                            .padding(horizontal = 55.dp)
                    )
                }
                else{ // Add SearchBar + Popolazione Lista 
                    var searchQuery by remember { mutableStateOf("") }

                    ChatSearchBar(
                        //CICLO REATTIVO
                        //Compose per ogni aggiornamento fa il ReCompose passando come "query" il nuovo valore immesso nella searchBar dall'utente

                        query = searchQuery, //testo attuale ("value" in TextField)
                        onQueryChanged = { searchQuery = it }, //ogni volta che l’utente digita qualcosa, questa funzione viene chiamata con il nuovo testo (it), che viene usato per aggiornare searchQuery.
                        modifier = Modifier
                        //.scale(1.3f)
                    )

                    // ordina la lista in base alla data dell'ultimo messaggio
                    val sortedChatList = chatsList.value.sortedBy{ it.lastTimeStamp }

                    Column(
                        modifier = Modifier
                            .offset(x = 50.dp, y = 110.dp)
                    ){
                        // itera lista e crea Lable contatti
                        sortedChatList.forEach { chat ->
                            ChatContactLable(
                                nome = "${chat.employee} ${chat.employee}",
                                lastMessage = chat.lastMsg,
                                modifier = Modifier
                                    .scale(1.3f),
                                imgId = R.drawable.profile_female_default
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(5.dp)
                            )
                        }

                    }


                }




                // BUTTON new Chat
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(40.dp)
                        .padding(bottom = 10.dp)
                ){
                    NewChatButton(
                        navController = navController,

                        //Modifier aggiuntivi utili passabili
                        modifier = Modifier
                            .scale(1.2f)
                    )
                }



            }
        }


    }
}

@Preview
@Composable
fun ChatPagePreview() {
    ChatPage(navController = NavController(context = LocalContext.current))
}
