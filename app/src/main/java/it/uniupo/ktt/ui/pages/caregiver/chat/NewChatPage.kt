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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.chats.AddContactButton
import it.uniupo.ktt.ui.components.chats.ChatContactLable
import it.uniupo.ktt.ui.components.chats.ModalAddContact
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.viewmodel.ChatViewModel
import it.uniupo.ktt.viewmodel.NewChatViewModel

@Composable
fun NewChatPage(navController: NavController) {
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("landing") {
            popUpTo("new chat") { inclusive = true }
            launchSingleTop = true
        }
    }

    //Stato Dialog (visibilità del del ModalAddContact (ON/OFF))
    var showDialog by remember { mutableStateOf(false) }

    val currentUid = BaseRepository.currentUid()


    // collegamento al ChatViewModel "old" della pagina padre (ChatPage)
    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("chat")
    }
    val chatViewModelRefHilt = hiltViewModel<ChatViewModel>(parentEntry)


    // collegamento ad un "new" NewChatViewModel (con Hilt)
    val newChatViewModelRefHilt = hiltViewModel<NewChatViewModel>()
    // properties observable
    val contactsRef by newChatViewModelRefHilt.contactList.collectAsState()
    val isLoadingRef by newChatViewModelRefHilt.isLoading.collectAsState()
    val errorRef by newChatViewModelRefHilt.errorMessage.collectAsState()
    // lancio metodo per Init OR Update
    LaunchedEffect (currentUid){
        if(currentUid != null){
            newChatViewModelRefHilt.loadContacts(currentUid)
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
                title = "New chat"
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
                                    onDismiss = { showDialog = false },
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
                    contactsRef.isEmpty() ->{
                        Text(
                            text = buildAnnotatedString {
                                append("Press ")

                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("add contact")
                                }

                                append(" to extend your contacts book!")
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
                                .offset(x= 0.dp, y= 260.dp)
                                .padding(horizontal = 55.dp)
                        )
                    }
                    // lista Chat recieved non vuota (1+ Chat al momento)
                    else -> {

                        // ordina la lista in ordine alfabetico in base al "name"
                        val sortedContactsList = contactsRef.sortedBy { it.name }

                        Text(
                            text = "Contacts on Keep The Time",

                            fontSize = 17.sp,
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
                                        nome = "${contact.name} ${contact.surname}",
                                        lastMessage = "send a message",
                                        modifier = Modifier
                                            .scale(1.3f),
                                        imgId = R.drawable.profile_female_default,
                                        onClick = {
                                            /*
                                                L' "uidContact" della persona con cui voglio creare una chat è gia nelle chat di cui faccio parte?
                                                    - IF(true): Get Locale del "chatId" della chat esistente e naviga to "ChatOpen" passando "chatId/ uidContact"
                                                    - ELSE: naviga to "ChatOpen" passando "null/ uidContact"
                                             */
                                            val contactName = "${contact.name} ${contact.surname}"
                                            val chatFound = chatViewModelRefHilt.searchChatByUidEmployee(contact.uidContact)

                                            // CHAT già esistente
                                            if(chatFound != null){
                                                navController.navigate("chat open/${chatFound.chatId}/${contact.uidContact}/${contactName}")
                                            }
                                            // CHAT non esistente
                                            else{
                                                navController.navigate("chat open/notFound/${contact.uidContact}/${contactName}")
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


