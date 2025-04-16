package it.uniupo.ktt.ui.components.chats

import android.util.Log
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.ChatRepository
import it.uniupo.ktt.ui.model.Contact
import it.uniupo.ktt.viewmodel.NewChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun ModalAddContact(
    onDismiss: () -> Unit
    ) {

    // Link al ChatViewModel
    /*
        Dato che il mio Progetto è configurato con HILT quando istanzio un
        "viewModel" classico, verrà automaticame convertito in un "HiltViewModel"
        dalla Factory di Hilt integrata.
        Per questo funziona correttamente anceh così
    */
    val viewModelRef : NewChatViewModel = viewModel()
    val contactList by viewModelRef.contactList.collectAsState()

    //snackBar (modale info)
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Shake Animation (errore Email non trovata)
    var shakeEmail by remember { mutableStateOf(false) }
    val offsetX by animateFloatAsState(
        targetValue = if (shakeEmail) 10f else 0f,
        animationSpec = repeatable(
            iterations = 6,
            animation = tween(durationMillis = 90, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    // Reset automatico dello shake
    LaunchedEffect(shakeEmail) {
        if (shakeEmail) {
            delay(300) // durata equivalente all'animazione
            shakeEmail = false
        }
    }


    // Wrap Column con ScaffHold (per mostrare SNACKBAR)
    Scaffold (
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
        containerColor = Color.Transparent
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier
                    //.padding(paddingValues)
                    .background(Color(0xFFC5B5D8), shape = RoundedCornerShape(16.dp)) // lilla scuro
                    .padding(24.dp)
                    .width(300.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                                        // CAMPI
                CustomChatTextField(
                    label = "Name:",
                    textfieldValue = name,
                    onValueChange = { name = it }
                )

                CustomChatTextField(
                    label = "Surname:",
                    textfieldValue = surname,
                    onValueChange = { surname = it }
                )

                CustomChatTextField(
                    label = "Email:",
                    textfieldValue = email,
                    onValueChange = { email = it },

                    modifier = Modifier.offset { IntOffset(offsetX.roundToInt(), 0) }
                )

                Spacer(modifier = Modifier.size(20.dp))

                                            // BUTTONS
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    //CANCEL BUTTON
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBBA5E1)) // viola chiaro
                    ) {
                        Text("Cancel")
                    }

                    // coroutine riferimento (corutine dato che uso .await)
                    val scope = rememberCoroutineScope()

                    //ADD BUTTON
                    Button(
                        onClick = {
                            // Validazione campi
                            if (name.isBlank() || surname.isBlank() || email.isBlank()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Tutti i campi sono obbligatori")
                                }
                                return@Button
                            }

                            // Controllo contatto già presente nella tua Lista contatti
                            val contactExists = contactList.any { it.email == email.lowercase() }

                            if (contactExists) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Impossibile aggiungere il contatto:\n Contatto già Esistente")
                                }
                                return@Button
                            }

                            // Coroutine Space
                            scope.launch{

                                // POST CONTACT on DB
                                val emailLowerCase = email.lowercase() //trasforma in Lowercase
                                val myUid = BaseRepository.currentUid().toString()
                                val targetUser = ChatRepository.getUserByEmail(emailLowerCase)

                                if(targetUser!= null && targetUser.isValid()) { //utente trovato
                                    try {
                                        val newContact = Contact(
                                            email = emailLowerCase,
                                            name = name,
                                            surname = surname,
                                            uidPersonal = myUid,
                                            uidContact = targetUser.uid
                                        )
                                        //Log.d("DEBUG", "Contatto: $newContact")

                                        //POST NEW CONTACT (con ViewModel)
                                        viewModelRef.postContact(newContact)

                                        onDismiss() //callback visibilità modale OFF (chiudi modale)
                                        
                                        snackbarHostState.showSnackbar(
                                            message = "Contact successfully added!"
                                        )
                                    }
                                    catch (e: IllegalArgumentException){
                                        Log.e("DEBUG", "Dati non validi: ${e.message}")
                                        // puoi anche mostrare un Toast o uno Snackbar all’utente qui
                                    }

                                }
                                else{ //utente non trovato
                                    Log.d("DEBUG", "Email cercata: $email | targetUser: $targetUser")

                                    //SHAKE + SNACKBAR
                                    shakeEmail = true
                                    snackbarHostState.showSnackbar("Email non presente nel database")
                                }
                            }

                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C46FF)) // viola più scuro
                    ) {
                        Text("Add")
                    }
                }
            }
        }


    }
}



@Preview(showBackground = true)
@Composable
fun ModalAddContactPreview() {
    //ModalAddContact()
}