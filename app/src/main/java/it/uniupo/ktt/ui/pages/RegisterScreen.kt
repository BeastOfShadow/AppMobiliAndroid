package it.uniupo.ktt.ui.pages

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.AccessCustomTextField
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.model.User
import it.uniupo.ktt.ui.roles.UserRole

@Composable
fun RegisterScreen(navController: NavController) {
    if (!LocalInspectionMode.current && BaseRepository.isUserLoggedIn()) {
        navController.navigate("home") {
            popUpTo("register") { inclusive = true }
            launchSingleTop = true
        }
    }

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
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

            Spacer(modifier = Modifier.height(50.dp))

            // Title
            Text(
                text = "Register",

                fontFamily = FontFamily(Font(R.font.poppins_medium)),
                fontWeight = FontWeight(500),
                fontSize = 40.sp,

                color = Color.Black,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Gray.copy(alpha = 0.4f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Solicitous
            Text(
                text = "Create your new account",

                fontFamily = FontFamily(Font(R.font.poppins_light)),
                fontWeight = FontWeight(350),
                fontSize = 19.sp,

                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), // Color secondary
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(80.dp))


            // TEXTFIELDs + BUTTON + ENDPage
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
                    .width(360.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Email Input

                AccessCustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    labelText = "Email"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Name Input
                    AccessCustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        labelText = "Name",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Surname Input
                    AccessCustomTextField(
                        value = surname,
                        onValueChange = { surname = it },
                        labelText = "Surname",
                        modifier = Modifier.weight(1f)
                    )
                }


                Spacer(modifier = Modifier.height(20.dp))

                // Password Input
                AccessCustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    labelText = "Password",
                    isPassword = true,
                    isPasswordVisible = isPasswordVisible,
                    onPasswordToggle = { isPasswordVisible = !isPasswordVisible }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Password Input
                AccessCustomTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    labelText = "Confirm password",
                    isPassword = true,
                    isPasswordVisible = isConfirmPasswordVisible,
                    onPasswordToggle = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                )

                Spacer(modifier = Modifier.height(38.dp))


                // Button
                Button(
                    onClick = {
                        if (email.isEmpty() || email.isBlank()) {
                            message = "Email is required.\n"
                        } else if (name.isEmpty() || name.isBlank()) {
                            message = "Name is required.\n"
                        } else if (surname.isEmpty() || surname.isBlank()) {
                            message = "Surname is required.\n"
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
                                        val uid= BaseRepository.currentUid()

                                        val user = User(
                                            uid = uid.toString(),
                                            email = email.lowercase(),
                                            role = UserRole.EMPLOYEE.toString(),
                                            name = name.lowercase().replaceFirstChar { it.uppercase() },
                                            surname = surname.lowercase().replaceFirstChar { it.uppercase() },
                                            avatar = "avatar/Screenshot 2025-04-29 alle 17.42.53.png",
                                            userPoint = 0
                                        )

                                        //post on DB
                                        if (uid != null) {

                                            BaseRepository.db
                                                .collection("users")
                                                .document(uid)
                                                .set(user)
                                                .addOnSuccessListener {
                                                    Log.d("DEBUG", "Utente aggiunto con successo")

                                                    // se aggiungo l'utente con successo allora vado alla home
                                                    navController.navigate("home") {
                                                        popUpTo("landing") { inclusive = true }
                                                        launchSingleTop = true
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w("DEBUG", "Errore nell'aggiunta utente", e)
                                                }
                                        }
                                        else{
                                            Log.d("UID", "UID nullo")
                                        }





                                    } else {
                                        message = task.exception?.message ?: "Unknown error occurred"
                                    }
                                }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 5.dp,
                        pressedElevation = 2.dp,
                        hoveredElevation = 8.dp,
                        focusedElevation = 8.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C46FF),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                ) {
                    Text(
                        text = "Sign up",

                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        fontWeight = FontWeight(500),

                        fontSize = 20.sp,
                    )
                }

                if (message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = message, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(90.dp))

                // Link to LOGIN
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 16.sp,


                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontWeight = FontWeight(400),

                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    )

                    Text(
                        text = "Login",
                        fontSize = 17.sp,

                        fontFamily = FontFamily(Font(R.font.poppins_medium)),
                        fontWeight = FontWeight(400),

                        textDecoration = TextDecoration.Underline,
                        color = Color(0xFF9C46FF),
                        modifier = Modifier.clickable { navController.navigate("login") }
                    )
                }
            }


        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = NavController(context = LocalContext.current))
}
