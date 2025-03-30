package it.uniupo.ktt.ui.pages

import android.app.Activity
import android.app.Instrumentation
import android.credentials.CredentialManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.oAuthCredential
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser != null) {
        navController.navigate("home") {
            popUpTo("register") { inclusive = true }
            launchSingleTop = true
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

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
            FilledIconButton(
                onClick = {
                    // navController.popBackStack()
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("landing") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.size(34.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(55.dp))

            // Title
            Text(
                text = "Register",
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = MaterialTheme.colorScheme.primary, // Usa il colore primary del theme
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Solicitous
            Text(
                text = "Create your new account",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), // Color secondary
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(150.dp))

            /* Button
            Button(
                onClick = { /* Aggiungi azione sign up con Google */ },
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
                    text = "Sign up with Google",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            */
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

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Input
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { newText -> confirmPassword = newText },
                label = { Text("Confirm password") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(
                            imageVector = if (isConfirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(55.dp))

            // Button
            Button(
                onClick = {
                    if (email.isEmpty() || email.isBlank()) {
                        message = "Email is required.\n"
                    } else if (password.isEmpty() || password.isBlank()) {
                        message = "Password is required.\n"
                    } else if (confirmPassword.isEmpty() || confirmPassword.isBlank()) {
                        message = "Confirm password is required.\n"
                    } else if (password != confirmPassword) {
                        message = "Passwords do not match.\n"
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {


                                    // ++ Creazione User DataBase ++
                                    val uid= FirebaseAuth.getInstance().currentUser?.uid
                                    val db = FirebaseFirestore.getInstance()

                                    val user = hashMapOf(
                                        "email" to email,
                                        "role" to "employee"
                                        //"name" e "surname" da aggiornare in un secondo momento
                                    )

                                    //post on DB
                                    if (uid != null) {
                                        Log.d("UID", uid ?: "UID nullo")

                                        db.collection("users").document(uid)
                                            .set(user)
                                            .addOnSuccessListener {
                                                Log.d("Firestore", "Utente aggiunto con successo")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w("Firestore", "Errore nell'aggiunta utente", e)
                                            }
                                    }
                                    else{
                                        Log.d("UID", "UID nullo")
                                    }




                                    navController.navigate("home") {
                                        popUpTo("landing") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                } else {
                                    message = task.exception?.message ?: "Unknown error occurred"
                                }
                            }
                    }
                },
                shape = RoundedCornerShape(5.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 5.dp,
                    pressedElevation = 2.dp,
                    hoveredElevation = 8.dp,
                    focusedElevation = 8.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Sign up",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Text(
                    text = "Login",
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.clickable { navController.navigate("login") }
                )
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = NavController(context = LocalContext.current))
}
