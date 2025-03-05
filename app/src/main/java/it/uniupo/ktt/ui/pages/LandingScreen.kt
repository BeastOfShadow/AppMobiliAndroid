package it.uniupo.ktt.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.w3c.dom.Text

@Composable
fun LandingScreen(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Usa il colore di sfondo del tema
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            // Titolo
            Text(
                text = "Welcome to Keep The Time!",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.primary // Usa il colore primario del tema
            )

            // Sottotitolo
            Text(
                text = "Login to start your journey.",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f) // Colore secondario
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { newText -> email = newText },
                label = { Text("Enter email") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { newText -> password = newText },
                label = { Text("Enter password") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bottone
            Button(
                onClick = { /* Aggiungi azione login */ },
                shape = RoundedCornerShape(5.dp),
                elevation = ButtonDefaults.buttonElevation( // Aggiunge ombra
                    defaultElevation = 8.dp, // Ombra normale
                    pressedElevation = 2.dp, // Ombra più bassa quando premuto
                    hoveredElevation = 10.dp, // Ombra più alta quando il mouse passa sopra
                    focusedElevation = 8.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Usa il colore primario
                    contentColor = MaterialTheme.colorScheme.onPrimary // Testo in contrasto
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
fun LandingScreenPreview(){
    LandingScreen(modifier = Modifier.background(color = MaterialTheme.colorScheme.background).fillMaxSize())
}

@Preview
@Composable
fun LandingScreenPreviewDark(){
    LandingScreen(modifier = Modifier.background(color = Color.Blue).fillMaxSize())
}