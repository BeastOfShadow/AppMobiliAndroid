package it.uniupo.ktt.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.CustomTextField
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.lightGray
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.secondary
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewTaskScreen(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("new task") { inclusive = true }
            launchSingleTop = true
        }
    }

    var taskName by remember { mutableStateOf("") }
    var employee by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf("") }
    val subtasks = listOf(
        "Evento prova del testo davvero molto lungo, ma davvero tanto" to "Mario Rossi",
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState())
        ) {
            PageTitle(
                navController = navController,
                title = "New Task"
            )

            Spacer(modifier = Modifier.size(30.dp))

            CustomTextField(
                label = "Task name:",
                textfieldValue = taskName,
                onValueChange = { taskName = it }
            )

            Spacer(modifier = Modifier.size(20.dp))

            CustomTextField(
                label = "Employee:",
                textfieldValue = employee,
                onValueChange = { employee = it }
            )

            Row(
                modifier = Modifier.padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // "Share position" e il Switch
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Share position: ",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF403E3E),
                            ),
                        )
                        Switch(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it },
                            modifier = Modifier.scale(0.7f),
                            colors = SwitchDefaults.colors(
                                // checkedThumbColor = Color.Green,   // Colore del pallino quando il switch è acceso
                                // uncheckedThumbColor = Color.Gray,  // Colore del pallino quando il switch è spento
                                checkedTrackColor = secondary,    // Colore del tracciato quando il switch è acceso
                                // uncheckedTrackColor = Color.LightGray  // Colore del tracciato quando il switch è spento
                            )
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Duration: ",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF403E3E),
                            ),
                        )
                        TextField(
                            value = duration,
                            onValueChange = { newText ->
                                if (newText.length <= 5) {
                                    duration = newText
                                }
                            },
                            label = { Text("HH:MM") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5DFFA),
                                unfocusedContainerColor = Color(0xFFF5DFFA),
                                cursorColor = Color.Black,
                                disabledLabelColor = Color.Red,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.width(90.dp)
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = "Subtask List:",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF403E3E),
                ),
                modifier = Modifier.padding(bottom = 15.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (subtasks.isNotEmpty()) {
                        subtasks.forEachIndexed { index, (description) ->
                            Box(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .width(180.dp)
                                    .height(220.dp)
                                    .shadow(
                                        4.dp,
                                        shape = MaterialTheme.shapes.extraLarge,
                                        clip = false
                                    )
                                    .background(primary, shape = MaterialTheme.shapes.extraLarge)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(27.dp)
                                            .background(
                                                color = lightGray,
                                                shape = CircleShape
                                            )
                                            .align(Alignment.End)
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 14.sp,
                                            color = buttonTextColor,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                    Text(
                                        text = "Description:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = titleColor,
                                        textAlign = TextAlign.Start
                                    )

                                    Text(
                                        text = description,
                                        fontWeight = FontWeight.Light,
                                        fontSize = 16.sp,
                                        color = titleColor,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 4,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = "Photo:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = titleColor,
                                        textAlign = TextAlign.Start
                                    )

                                    Image(
                                        painter = painterResource(id = R.drawable.edit_rewrite),
                                        contentDescription = "Extend",
                                        modifier = Modifier.size(24.dp)
                                            .align(Alignment.End)
                                            .clickable {
                                                navController.navigate("update subtask")
                                            },
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .width(100.dp)
                            .height(100.dp)
                            .clickable {

                            }
                            .shadow(
                                4.dp,
                                shape = MaterialTheme.shapes.extraLarge,
                                clip = false)
                            .background(primary, shape = MaterialTheme.shapes.extraLarge)
                            .padding(16.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(
                                    color = tertiary,
                                    shape = CircleShape
                                )
                                .align(Alignment.Center)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                "Large floating action button",
                                tint = buttonTextColor,
                                modifier = Modifier.size(55.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(45.dp)
                    .shadow(
                        4.dp,
                        shape = MaterialTheme.shapes.large,
                        clip = false
                    )
                    .background(
                        color = tertiary,
                        shape = MaterialTheme.shapes.large
                    )
                    .clickable { /* Cancella */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Cancel",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(45.dp)
                    .shadow(
                        4.dp,
                        shape = MaterialTheme.shapes.large,
                        clip = false
                    )
                    .background(
                        color = tertiary,
                        shape = MaterialTheme.shapes.large
                    )
                    .clickable { /* Crea task */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Create",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun NewTaskScreenPreview() {
    NewTaskScreen(navController = NavController(context = LocalContext.current))
}