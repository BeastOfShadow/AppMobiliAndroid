package it.uniupo.ktt.ui.pages.caregiver.chat

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import it.uniupo.ktt.ui.components.chats.ChatInputBar
import it.uniupo.ktt.ui.components.chats.ChatPageTitle
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.viewmodel.ChatOpenViewModel

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import it.uniupo.ktt.ui.components.chats.ChatMsgBubble
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import it.uniupo.ktt.viewmodel.UserViewModel


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun ChatOpen(
    navController: NavController,
    chatId: String,
    uidContact: String
) {
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("login") {
            popUpTo("login") { inclusive = false } // rimuovi tutte le Page nello Stack fino a Landing senza eliminare quest'ultima
            launchSingleTop = true
        }
    }


    val currentUid = BaseRepository.currentUid()

    // Stato MessageText + scorrimento lista
    var messageText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    // -------------------------------- VIEW MODEL REF -------------------------------------------
    // ref UserViewModel istanziato in HomeScreen
    val routeKeyHome = "home"
    val parentEntry = remember(routeKeyHome) {
        navController.getBackStackEntry(routeKeyHome)
    }
    val userViewModel: UserViewModel = hiltViewModel(parentEntry) // USER
    val userRef by userViewModel.user.collectAsState()

    // Istanza chatOpenViewModel + OBSERVABLEs
    val viewModel: ChatOpenViewModel = hiltViewModel()
    val messages by viewModel.messageList.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    // Contact + Contact AvatarUrl
    val avatarUrl by viewModel.avatarUrl
    val contactUser by viewModel.contactUser
    // Unified Waiter (attende Messages & User)
    val isLoading by viewModel.isLoading
    // -------------------------------- VIEW MODEL REF -------------------------------------------


    // ---------------------------------- LYFE CYCLE ---------------------------------------------
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (currentUid != null) {
                if (event == Lifecycle.Event.ON_STOP) {

                    // CASO 1) update SESSION (pre-existent Chat)
                    if(chatId != "notFound"){
                        viewModel.updateChatSession(chatId)
                    }
                    // CASO 2) update SESSION (chat created after entering ChatOpen)
                    else if(viewModel.savedChatId.value != "notFound"){
                        viewModel.updateChatSession(viewModel.savedChatId.value)
                    }

                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    // ---------------------------------- LYFE CYCLE ---------------------------------------------


    // -------------------------------- LAUNCHED EFFECTS -------------------------------------------
    // Scroll automatico all'ultimo messaggio
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(0)
    }

    // Get Role
    LaunchedEffect(currentUid) {
        currentUid?.let { viewModel.getRoleByUid(it) }
    }

    // passaggio UidContact al viewModel e Call to get User + AvatarUrl
    LaunchedEffect(uidContact) {
        viewModel.setUidContact(uidContact)
    }

    // Creazione Listener (IF "chatId != notFound") + Update Sessione
    LaunchedEffect(chatId) {
        if (chatId != "notFound") {
            viewModel.loadMessages(chatId) // -> LOAD MESSAGE + LISTENER REAL-TIME
            viewModel.updateChatSession(chatId) // -> UPDATE SESSIONE
        }
    }
    // -------------------------------- LAUNCHED EFFECTS -------------------------------------------


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
    ) {

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        else{


            // ----*****  HEADER-FISSO  *****----
            ChatPageTitle(
                navController = navController,
                chatId = chatId,
                nome = "${contactUser?.name} ${contactUser?.surname}",
                avatarUrl = avatarUrl ?: "",
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(Color.Transparent)
                    .scale(1.3f)
            )


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp, bottom = 10.dp)
            ) {

                // ----*****  MESSAGES  *****----
                when {
                    error != null -> {
                        Log.e("DEBUG-ModelView", "Errore caricamento messaggi")
                    }
                    else -> {
                        LazyColumn(
                            state = listState,
                            reverseLayout = true,
                            modifier = Modifier
                                .weight(1f)
                                //.padding(horizontal = 16.dp),
                            ,contentPadding = PaddingValues(12.dp)
                        ) {
                            val sortedMessages = messages.sortedByDescending { it.timeStamp }
                            items(
                                items = sortedMessages,
                                key = { message -> message.timeStamp }
                            ) { message ->
                                ChatMsgBubble(
                                    isMine = message.sender == currentUid,
                                    text = message.text,
                                    timeStamp = message.timeStamp,
                                    seen = message.seen
                                )

                            }
                        }


                    }
                }

                ChatInputBar(
                    text = messageText,
                    onTextChange = { messageText = it },
                    onSendClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(
                                chatId = chatId,
                                text = messageText,
                                deviceToken = contactUser?.deviceToken ?: "error",
                                senderName = " ${userRef?.name} ${userRef?.surname}",
                            )
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.CenterHorizontally)
                )

            }







        }

    }
}


@Preview
@Composable
fun ChatOpenPreview() {
    //ChatOpen(navController = NavController(context = LocalContext.current), "prova", "prova", "prova nome")
}
