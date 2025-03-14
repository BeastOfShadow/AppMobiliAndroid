package it.uniupo.ktt.ui.pages

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.autofill.Autofill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import org.checkerframework.checker.units.qual.A

@Composable
fun LandingScreen(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser != null) {
        navController.navigate("home") {
            popUpTo("landing") { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // Title
            Text(
                text = "The best app to track your time",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(200.dp))

            // Button
            Button(
                onClick = { navController.navigate("login") },
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
                    text = "Sign in",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "Create an account.",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 18.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { navController.navigate("register") }
            )
        }
    }
}

@Preview
@Composable
fun LandingScreenPreview() {
    LandingScreen(navController = NavController(context = LocalContext.current))
}
