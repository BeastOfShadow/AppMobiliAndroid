package it.uniupo.ktt.ui.components.chats

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.*
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.firebase.ChatRepository
import it.uniupo.ktt.ui.model.Contact
import kotlinx.coroutines.launch


@Composable
fun ModalAddContact(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
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
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.size(20.dp))


        // Bottoni
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

            // coroutine ref
            val scope = rememberCoroutineScope()

            //ADD BUTTON
            Button(
                onClick = {
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

                                //POST NEW CONTACT
                                ChatRepository.postNewContact(
                                    newContact = newContact,
                                    onSuccess = {
                                        Log.d("DEBUG", "Contatto creato con successo")
                                        onDismiss()
                                    },
                                    onError = { e ->
                                        Log.e("DEBUG", "Errore nel salvataggio del contatto", e)
                                        // Mostra errore in UI se vuoi (es. Snackbar, Toast)
                                    }
                                )
                            }
                            catch (e: IllegalArgumentException){
                                Log.e("DEBUG", "Dati non validi: ${e.message}")
                                // puoi anche mostrare un Toast o uno Snackbar all’utente qui
                            }

                        }
                        else{ //utente non trovato
                            Log.d("DEBUG", "Email cercata: $email | targetUser: $targetUser")

                            //fai tremare la textfield? e msg "email non trovata nel database"

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



@Preview(showBackground = true)
@Composable
fun ModalAddContactPreview() {
    //ModalAddContact()
}