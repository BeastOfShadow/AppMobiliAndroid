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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.chats.AddContactButton
import it.uniupo.ktt.ui.components.chats.ChatContactLable
import it.uniupo.ktt.ui.components.chats.ModalAddContact
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.ChatRepository.getAllConntactsByUid
import it.uniupo.ktt.ui.model.Contact

@Composable
fun NewChatPage(navController: NavController) {
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("landing") {
            popUpTo("new chat") { inclusive = true }
            launchSingleTop = true
        }
    }

    //Get lista Contatti
    val contactList = remember { mutableStateOf<List<Contact>>(emptyList()) }
    val currentUid = BaseRepository.currentUid()

    LaunchedEffect(currentUid) {
        if (currentUid != null) {
            getAllConntactsByUid(

                uid = currentUid,
                onSuccess = { contacts ->
                    Log.d("DEBUG", "Lista Contatti: ${contacts.joinToString(separator = "\n")}")
                    contactList.value = contacts.filter { it.isValid() }
                },
                onError = { e ->
                    Log.e("DEBUG", "Errore nella getAllConntactsByUid query", e)
                }
            )
        }
    }


    //Stato Dialog (visibilità del del ModalAddContact (ON/OFF))
    var showDialog by remember { mutableStateOf(false) }

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
                                    contactList
                                )
                            }
                        }
                    }

                    if(contactList.value.isNotEmpty()){
                        Text(
                            text = "Contact on Keep The Time",

                            fontSize = 17.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF757070),

                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,

                            modifier = Modifier
                                .offset(x = -(17).dp, y = 24.dp)
                        )
                    }
                }

                // LOWER PART (popolazione lista OR msg)
                if(contactList.value.isEmpty()){ // LISTA CONTACT EMPTY
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
                else { // LISTA CONTACT NOT EMPTY

                    // ordina la lista in base al nome in ordine alfabetico

                    val sortedContactList = contactList.value.sortedBy{ it.name }

                    Column(
                        modifier = Modifier
                            .offset(x = 50.dp, y = 140.dp)
                    ){
                        // itera lista e crea Lable contatti
                        sortedContactList.forEach { contact ->
                            ChatContactLable(
                                nome = "${contact.name} ${contact.surname}",
                                lastMessage = "send a message",
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

            }
        }


    }
}

@Preview
@Composable
fun NewChatPagePreview() {
    NewChatPage(navController = NavController(context = LocalContext.current))
}
