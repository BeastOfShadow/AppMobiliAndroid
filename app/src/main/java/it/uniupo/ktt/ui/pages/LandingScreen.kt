package it.uniupo.ktt.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LandingScreen(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser != null) {
        navController.navigate("home"){
            popUpTo("landing"){inclusive = true}
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            // Title
            Text(
                text = "Welcome to Keep The Time!",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.primary // Usa il colore primary del theme
            )

            // Solicitous
            Text(
                text = "Login to start your journey.",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f) // Color secondary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Button
            Button(
                onClick = { /* Aggiungi azione login con Google */ },
                shape = RoundedCornerShape(5.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 5.dp,
                    pressedElevation = 2.dp,
                    hoveredElevation = 8.dp,
                    focusedElevation = 8.dp
                ),
                // Per il Google Sign-In si usa solitamente uno sfondo bianco e contenuti in nero
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                // Assicurati di avere aggiunto la risorsa del logo Google (es. R.drawable.ic_google)
                /*Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))*/
                Text(
                    text = "Continue with Google",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

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
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Button
            Button(
                onClick = { /* Aggiungi azione login */ },
                shape = RoundedCornerShape(5.dp),
                elevation = ButtonDefaults.buttonElevation( // Aggiunge ombra
                    defaultElevation = 5.dp, // Ombra normale
                    pressedElevation = 2.dp, // Ombra più bassa quando premuto
                    hoveredElevation = 8.dp, // Ombra più alta quando il mouse passa sopra
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
                    text = "Log in",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Not registered yet? Register",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.clickable { navController.navigate("register") }
            )
        }
    }
}

@Preview
@Composable
fun LandingScreenPreview(){
    LandingScreen(navController = NavController(context = LocalContext.current))
}
