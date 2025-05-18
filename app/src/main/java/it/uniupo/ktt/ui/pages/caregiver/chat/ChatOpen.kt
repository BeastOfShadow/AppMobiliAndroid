package it.uniupo.ktt.ui.pages.caregiver.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.chats.ChatInputBar
import it.uniupo.ktt.ui.components.chats.ChatPageTitle
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.viewmodel.ChatOpenViewModel

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import it.uniupo.ktt.ui.components.chats.ChatMsgBubble
import androidx.compose.foundation.lazy.rememberLazyListState


@Composable
fun ChatOpen(
    navController: NavController,
    chatId: String,
    uidContact: String,
    contactName: String
) {
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("login") {
            popUpTo("login") { inclusive = false } // rimuovi tutte le Page nello Stack fino a Landing senza eliminare quest'ultima
            launchSingleTop = true
        }
    }


    val currentUid = BaseRepository.currentUid()

    // Istanza viewModel + OBSERVABLE
    val viewModel: ChatOpenViewModel = hiltViewModel()
    val messages by viewModel.messageList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    // Stato MessageText + scorrimento lista
    var messageText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()


    // Scroll automatico all'ultimo messaggio
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(0)
    }

    // passaggio UidContact al viewModel
    LaunchedEffect(uidContact) {
        viewModel.setUidContact(uidContact)
    }

    // Creazione Listener (IF "chatId != notFound")
    LaunchedEffect(chatId) {
        if (chatId != "notFound") {
            viewModel.loadMessages(chatId)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ChatPageTitle(
            navController = navController,
            nome = contactName,
            avatarUrl = R.drawable.profile_female_default,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
                .scale(1.3f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp) // Spazio per il titolo
                .imePadding()
        ) {
            // --- LISTA MESSAGGI ---
            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    error != null -> {
                        Log.e("DEBUG-ModelView", "Errore caricamento messaggi")
                    }
                    else -> {
                        val sortedMessages = messages.sortedByDescending { it.timeStamp }

                        LazyColumn(
                            state = listState,
                            reverseLayout = true,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(
                                items = sortedMessages,
                                key = { message -> message.timeStamp } // oppure puoi usare anche un campo ID univoco
                            ) { message ->
                                ChatMsgBubble(
                                    isMine = message.sender == currentUid,
                                    text = message.text,
                                    timeStamp = message.timeStamp,
                                    seen = message.seen
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                    }
                }
            }

            // --- INPUT BAR ---
            ChatInputBar(
                text = messageText,
                onTextChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(chatId, messageText)
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            )
        }
    }
}


@Preview
@Composable
fun ChatOpenPreview() {
    ChatOpen(navController = NavController(context = LocalContext.current), "prova", "prova", "prova nome")
}
