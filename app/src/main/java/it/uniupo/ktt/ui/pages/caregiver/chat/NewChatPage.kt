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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.chats.AddContactButton
import it.uniupo.ktt.ui.components.chats.ChatContactLable
import it.uniupo.ktt.ui.components.chats.ModalAddContact
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.viewmodel.ChatViewModel
import it.uniupo.ktt.viewmodel.HomeScreenViewModel
import it.uniupo.ktt.viewmodel.NewChatViewModel

@Composable
fun NewChatPage(navController: NavController, homeVM: HomeScreenViewModel) {
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("login") {
            popUpTo("login") { inclusive = false } // rimuovi tutte le Page nello Stack fino a Landing senza eliminare quest'ultima
            launchSingleTop = true
        }
    }

    //Stato Dialog (visibilità del del ModalAddContact (ON/OFF))
    var showDialog by remember { mutableStateOf(false) }

    val currentUid = BaseRepository.currentUid()

    // -------------------------------- VIEW MODEL REF -------------------------------------------
    val newChatViewModelRefHilt = hiltViewModel<NewChatViewModel>() // ENRICHED CONTACTS

    // Observable
    val enrichedContactRef by newChatViewModelRefHilt.enrichedContactList.collectAsState()
    val isLoadingRef by newChatViewModelRefHilt.isLoading.collectAsState()
    val errorRef by newChatViewModelRefHilt.errorMessage.collectAsState()
    // -------------------------------- VIEW MODEL REF -------------------------------------------


    // -------------------------------- LAUNCHED EFFECTS -------------------------------------------
    LaunchedEffect (currentUid){
        if(currentUid != null){
            newChatViewModelRefHilt.loadContacts(currentUid)
        }
    }
    // -------------------------------- LAUNCHED EFFECTS -------------------------------------------


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(20.dp)
    ) {
        Column() {
            PageTitle(
                navController = navController,
                title = "New chat"
            )

            Spacer(modifier = Modifier.height(20.dp))

            //Page
            Box(
                modifier = Modifier
                    //.shadow(8.dp, RoundedCornerShape(20.dp), clip = false)
                    .padding(4.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10),
                        clip = false
                    )
                    .background(Color(0xFFF5DFFA), shape = RoundedCornerShape(10))
                    .fillMaxSize()
            ){
                // UPPER PART( button + addcontact + msg)
                Column(
                        modifier = Modifier
                    .offset(x = 50.dp, y = 30.dp)) {

                    //ADD CONTACT BUTTON
                    Box(){
                        AddContactButton(
                            //navController = navController,

                            //Modifier aggiuntivi utili passabili
                            modifier = Modifier
                                .scale(1.6f),
                            onClick = { showDialog = true}
                        )

                        if (showDialog) {
                            Dialog(onDismissRequest = { showDialog = false }) {
                                ModalAddContact(
                                    onDismiss = { showDialog = false }
                                )
                            }
                        }
                    }

                }

                // UI-> ModelView Behaviour
                when {
                    // wait to build PageUI (DB data not delivered yet)
                    isLoadingRef -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    // query error
                    errorRef != null -> {
                        Log.e("DEBUG-ModelView", "Errore delivery Lista Contacts")
                    }
                    // lista Chat recieved vuota (0 Chat al momento)
                    enrichedContactRef.isEmpty() ->{
                        Text(
                            text = buildAnnotatedString {
                                append("Press ")

                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("add contact")
                                }

                                append(" to extend your contacts book!")
                            },
                            fontFamily = FontFamily(Font(R.font.poppins_regular)),
                            fontWeight = FontWeight(400),

                            fontSize = 22.sp,
                            lineHeight = 34.sp,

                            color = Color(0xFF423C3C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                //.align(Alignment.Center)
                                .offset(x= 0.dp, y= 260.dp)
                                .padding(horizontal = 55.dp)
                        )
                    }
                    // lista Chat recieved non vuota (1+ Chat al momento)
                    else -> {

                        // ordina la lista in ordine alfabetico in base al "name"
                        val sortedContactsList = enrichedContactRef.sortedBy { it.contact.name }

                        Text(
                            text = "Contacts on Keep The Time",

                            fontSize = 17.sp,
                            fontFamily = FontFamily(Font(R.font.poppins_regular)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF757070),

                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,

                            modifier = Modifier
                                .offset(x = 40.dp, y = 110.dp)
                        )

                        Box(
                            modifier = Modifier
                                .offset(x = 50.dp, y = 140.dp)
                                .heightIn(max = 555.dp)
                                .verticalScroll(rememberScrollState())
                        ){
                            Column(){
                                // scotti lista e crea ChatLable contatti
                                sortedContactsList.forEach { contact ->
                                    ChatContactLable(
                                        nome = AnnotatedString("${contact.contact.name} ${contact.contact.surname}"),
                                        lastMessage = "send a message",
                                        modifier = Modifier
                                            .scale(1.3f),
                                        imgUrl = contact.avatarUrl,
                                        showBadge = false,
                                        onClick = {

                                            // "searchChatByUidEmployee" non è una Async Call, ma controlla se nel "ChatViewModel" (generato nella ChatPage precedente) nella lista di Chat è già presente una chat con l'uid del contatto selezionato
                                            val chatFound = homeVM.searchChatByUid(contact.contact.uidContact)


                                            // CHAT già esistente
                                            if(chatFound != null){
                                                navController.navigate("chat open/${chatFound.chat.chatId}/${contact.contact.uidContact}")
                                            }
                                            // CHAT non esistente
                                            else{
                                                navController.navigate("chat open/notFound/${contact.contact.uidContact}")
                                            }
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

            }
        }


    }
}


