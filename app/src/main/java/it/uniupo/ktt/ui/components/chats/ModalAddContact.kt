package it.uniupo.ktt.ui.components.chats

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

//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email:") },
//            shape = RoundedCornerShape(20.dp),
//            modifier = Modifier.fillMaxWidth(),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedContainerColor = Color.White,
//                unfocusedContainerColor = Color.White,
//                disabledContainerColor = Color.White
//            )
//        )

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

            //ADD BUTTON
            Button(
                onClick = {
                    // TODO: gestisci i dati qui se vuoi
                    onDismiss()
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