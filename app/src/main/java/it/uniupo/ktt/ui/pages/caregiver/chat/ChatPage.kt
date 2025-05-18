package it.uniupo.ktt.ui.pages.caregiver.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.hilt.navigation.compose.hiltViewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.ChatRepository
import it.uniupo.ktt.ui.model.Chat
import it.uniupo.ktt.viewmodel.ChatViewModel

@Composable
fun ChatPage(navController: NavController) {
    //Controllo Login
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("login")
        {
            popUpTo("login") { inclusive = false } // rimuovi tutte le Page nello Stack fino a Landing senza eliminare quest'ultima
            launchSingleTop = true
        }
    }

    // collegamento con ChatViewModel
    val chatViewModelRefHilt = hiltViewModel<ChatViewModel>()
    // proprietà osservabili del ChatViewModel (Only Read)
    val chatsRef by chatViewModelRefHilt.enrichedChatList.collectAsState()
    val isLoadingRef by chatViewModelRefHilt.isLoading.collectAsState()
    val errorRef by chatViewModelRefHilt.errorMessage.collectAsState()

    val currentUid = BaseRepository.currentUid()

    // ogni volta che entro nella page viene lanciato per update
    LaunchedEffect (currentUid){
        if(currentUid!= null){
            chatViewModelRefHilt.loadChats(currentUid)
        }
    }

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
                    .padding(4.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10),
                        clip = false
                    )
                    .background(Color(0xFFF5DFFA), shape = RoundedCornerShape(10))
                    .fillMaxSize()
            ){

                // UI-> ModelView Behaviour
                when {
                    // wait to build PageUI (DB data not delivered yet)
                    isLoadingRef -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    // query error
                    errorRef != null -> {
                        Log.e("DEBUG-ModelView", "Errore delivery Lista Chats")
                    }
                    // lista Chat recieved vuota (0 Chat al momento)
                    chatsRef.isEmpty() ->{
                        Text(
                            text = buildAnnotatedString {
                                append("Press ")

                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("new chat icon")
                                }

                                append(" to start a conversation!")
                            },
                            fontFamily = FontFamily(Font(R.font.poppins_regular)),


                            //letterSpacing = 1.sp,
                            fontSize = 22.sp,
                            fontWeight = FontWeight(400),
                            lineHeight = 34.sp,



                            color = Color(0xFF423C3C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .offset(x = 0.dp, y = 280.dp)
                                .padding(horizontal = 55.dp)
                        )
                    }
                    // lista Chat recieved non vuota (1+ Chat al momento)
                    else -> {
                        // Add SearchBar + Popolazione Lista
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
                        val sortedChatList = chatsRef.sortedBy { it.chat.lastTimeStamp }

                        Box(
                            modifier = Modifier
                                .offset(x = 50.dp, y = 110.dp)
                                .heightIn(max = 555.dp)
                                .verticalScroll(rememberScrollState())
                        ){
                            Column{
                                // itera lista e crea Lable contatti
                                sortedChatList.forEach { enrichedChat ->

                                    // scompongo la lista arricchita
                                    val chat = enrichedChat.chat

                                    ChatContactLable(
                                        nome = "${enrichedChat.name} ${enrichedChat.surname}",
                                        lastMessage = chat.lastMsg,
                                        modifier = Modifier
                                            .scale(1.3f),
                                        imgUrl = enrichedChat.avatarUrl,
                                        onClick = {
                                            // GOTO -> "ChatOpen",  passing "chatId, uidEmployee"

                                            navController.navigate("chat open/${chat.chatId}/${chat.employee}")
                                        }
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .height(5.dp)
                                    )
                                }

                            }
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
