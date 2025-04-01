package it.uniupo.ktt.ui.pages.Caregiver.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.chats.AddContactButton
import it.uniupo.ktt.ui.components.chats.ChatContactLable
import it.uniupo.ktt.ui.components.chats.ChatSearchBar
import it.uniupo.ktt.ui.components.chats.ModalAddContact
import it.uniupo.ktt.ui.components.chats.NewChatButton

@Composable
fun NewChatPage(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("new chat") { inclusive = true }
            launchSingleTop = true
        }
    }

    //Stato Dialog
    var showDialog by remember { mutableStateOf(false) }

    //creazione
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

                    Text(
                        text = "Contact on Keep The Time",

                        fontSize = 17.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF757070),

                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,

                        modifier = Modifier
                            .offset(x = -17.dp, y = 24.dp)
                    )
                }



                //IF (//non ho contatti già esistenti)
                Text(
                    text = buildAnnotatedString {
                        append("Press ")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("add contact")
                        }

                        append(" to extend your contacts book!")
                    },
                    style = MaterialTheme.typography.bodyLarge, //Poppins


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


                //ELSE {} // addTEXT + call DB prendendo tutte le chats esistenti per l'utente + le carico nel box

                var searchQuery by remember { mutableStateOf("") }


                //in base alla "searchQuery" aggiornerò la lista di chat già esistenti


                Column(
                    modifier = Modifier
                        .offset(x = 50.dp, y = 140.dp)
                ){
                    ChatContactLable(
                        "Maria Teresa",
                        lastMessage = "send a message",
                        modifier = Modifier
                            .scale(1.3f),
                        imgId = R.drawable.profile_female_default
                    )
                    Spacer(
                        modifier = Modifier
                            .height(5.dp)
                    )

                    ChatContactLable(
                        "Andrea Diprè",
                        lastMessage = "send a message",
                        modifier = Modifier
                            .scale(1.3f),
                        imgId = R.drawable.profile_male_default
                    )



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
