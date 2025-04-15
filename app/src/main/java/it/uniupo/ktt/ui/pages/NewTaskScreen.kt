package it.uniupo.ktt.ui.pages

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
//import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.CustomTextField
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.firebase.BaseRepository.currentUid
import it.uniupo.ktt.ui.model.SubTask
import it.uniupo.ktt.ui.model.Task
import it.uniupo.ktt.ui.subtaskstatus.SubtaskStatus
import it.uniupo.ktt.ui.taskstatus.TaskStatus
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.lightGray
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.secondary
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.TaskViewModel
import it.uniupo.ktt.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewTaskScreen(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("new task") { inclusive = true }
            launchSingleTop = true
        }
    }

    val taskViewModel: TaskViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val coroutineScope = rememberCoroutineScope()

    var taskName by remember { mutableStateOf("") }
    var employee by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var subtaskDescription by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var visibleImage by remember { mutableStateOf(false) }
    var showDescriptionError by remember { mutableStateOf(false) }

    var subtasks by remember { mutableStateOf<List<SubTask>>(emptyList()) }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri.value = it }
    }

    /*val subtasks = listOf(
        "Evento prova del testo davvero molto lungo, ma davvero tanto" to "Mario Rossi",
    )*/

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
                                        text = subtasks[index].description,
                                        fontWeight = FontWeight.Light,
                                        fontSize = 16.sp,
                                        color = titleColor,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 4,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Row {
                                        Text(
                                            text = "Photo: ",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = titleColor,
                                            textAlign = TextAlign.Start
                                        )

                                        Icon(
                                            if(subtasks[index].descriptionImgStorageLocation != "null") {Icons.Outlined.Check} else {
                                                Icons.Outlined.Clear
                                            },
                                            "Large floating action button",
                                            tint = Color.Black,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

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
                                showDialog = true
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
                        if (showDialog) {
                            Dialog(onDismissRequest = { showDialog = false }) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFC5B5D8), shape = RoundedCornerShape(16.dp)) // lilla scuro
                                        .padding(24.dp)
                                        .fillMaxWidth(),
                                )
                                {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        CustomTextField(
                                            label = "Subtask Description:",
                                            textfieldValue = subtaskDescription,
                                            onValueChange = { subtaskDescription = it }
                                        )

                                        Spacer(modifier = Modifier.size(20.dp))

                                        Text(
                                            text = "Photo:",
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight(500),
                                                color = Color(0xFF403E3E),
                                            ),
                                            modifier = Modifier.padding(start = 20.dp)
                                        )

                                        Spacer(modifier = Modifier.size(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            // SEE IMAGE
                                            if(selectedImageUri.value != null) {
                                                Box(
                                                    modifier = Modifier
                                                        .shadow(
                                                            4.dp,
                                                            shape = MaterialTheme.shapes.extraLarge,
                                                            clip = false
                                                        )
                                                        .background(
                                                            color = Color.White,
                                                            shape = MaterialTheme.shapes.extraLarge
                                                        )
                                                        .clickable {
                                                            visibleImage = !visibleImage
                                                        }
                                                        .padding(
                                                            horizontal = 12.dp,
                                                            vertical = 8.dp
                                                        )
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            if(visibleImage) "Hide" else "See",
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = lightGray
                                                        )

                                                        Spacer(modifier = Modifier.width(8.dp))

                                                        Box(
                                                            modifier = Modifier
                                                                .shadow(
                                                                    4.dp,
                                                                    shape = CircleShape,
                                                                    clip = false
                                                                )
                                                                .background(
                                                                    color = tertiary,
                                                                    shape = CircleShape
                                                                )
                                                                .size(32.dp),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Image(
                                                                painter = painterResource(id = R.drawable.image_see),
                                                                contentDescription = "Extend",
                                                                modifier = Modifier.size(24.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            // ADD IMAGE
                                            if(selectedImageUri.value == null) {
                                                Box(
                                                    modifier = Modifier
                                                        .shadow(
                                                            4.dp,
                                                            shape = MaterialTheme.shapes.extraLarge,
                                                            clip = false
                                                        )
                                                        .background(
                                                            color = Color.White,
                                                            shape = MaterialTheme.shapes.extraLarge
                                                        )
                                                        .clickable {
                                                            launcher.launch("image/*")
                                                        }
                                                        .padding(
                                                            horizontal = 12.dp,
                                                            vertical = 8.dp
                                                        )
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = "Add",
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = lightGray
                                                        )

                                                        Spacer(modifier = Modifier.width(8.dp))

                                                        Box(
                                                            modifier = Modifier
                                                                .shadow(
                                                                    4.dp,
                                                                    shape = CircleShape,
                                                                    clip = false
                                                                )
                                                                .background(
                                                                    color = tertiary,
                                                                    shape = CircleShape
                                                                )
                                                                .size(32.dp),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Image(
                                                                painter = painterResource(id = R.drawable.image_upload),
                                                                contentDescription = "Extend",
                                                                modifier = Modifier.size(24.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            // REMOVE IMAGE
                                            if(selectedImageUri.value != null) {
                                                Box(
                                                    modifier = Modifier
                                                        .shadow(
                                                            4.dp,
                                                            shape = MaterialTheme.shapes.extraLarge,
                                                            clip = false
                                                        )
                                                        .background(
                                                            color = Color.White,
                                                            shape = MaterialTheme.shapes.extraLarge
                                                        )
                                                        .clickable {
                                                            selectedImageUri.value = null
                                                        }
                                                        .padding(6.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .shadow(
                                                                4.dp,
                                                                shape = MaterialTheme.shapes.extraLarge,
                                                                clip = false
                                                            )
                                                            .background(
                                                                color = tertiary,
                                                                shape = MaterialTheme.shapes.extraLarge
                                                            )
                                                            .padding(6.dp)
                                                    ) {
                                                        Image(
                                                            painter = painterResource(id = R.drawable.trashcan),
                                                            contentDescription = "Extend",
                                                            modifier = Modifier.size(24.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // IMAGE PREVIEW
                                        if(visibleImage) {
                                            Spacer(modifier = Modifier.size(20.dp))
                                            Row(
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            ) {
                                                selectedImageUri.value?.let { uri ->
//                                                    AsyncImage(
//                                                        model = uri,
//                                                        contentDescription = "Anteprima immagine",
//                                                        modifier = Modifier
//                                                            .size(200.dp)
//                                                            .clip(RoundedCornerShape(16.dp))
//                                                    )
                                                }
                                            }
                                        }

                                        if (showDescriptionError) {
                                            Spacer(modifier = Modifier.size(20.dp))

                                            Text(
                                                text = "La descrizione è obbligatoria",
                                                color = Color.Red,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(start = 20.dp, top = 4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.size(50.dp))

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .width(120.dp)
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
                                                    .clickable {
                                                        subtaskDescription = ""
                                                        selectedImageUri.value = null
                                                        showDialog = false
                                                    },
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
                                                    .width(120.dp)
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
                                                    .clickable {
                                                        if (subtaskDescription.isBlank()) {
                                                            showDescriptionError = true
                                                            return@clickable
                                                        }

                                                        val nextNumber = if (subtasks.isEmpty()) 1 else subtasks.last().listNumber + 1
                                                        subtasks += SubTask(
                                                            listNumber = nextNumber,
                                                            description = subtaskDescription,
                                                            descriptionImgStorageLocation = selectedImageUri.value.toString(),
                                                            status = SubtaskStatus.AVAILABLE.toString()
                                                        )

                                                        subtaskDescription = ""
                                                        selectedImageUri.value = null
                                                        showDialog = false
                                                    },
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
                            }
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
                    .clickable {
                        navController.popBackStack()
                    },
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
                    .clickable {
                        coroutineScope.launch {
                            val caregiverUid = currentUid()

                            if (caregiverUid == null) {
                                Log.d("Db", "Caregiver non loggato.")
                                return@launch
                            }

                            val uid = userViewModel.getUidByEmail(employee)

                            if (uid != null) {
                                val parts = duration.split(":")
                                val hours = parts[0].toIntOrNull() ?: 0
                                val minutes = parts[1].toIntOrNull() ?: 0
                                val time = (hours * 3600) + (minutes * 60)

                                taskViewModel.addTaskAndSubtasks(
                                    task = Task(
                                        id = UUID.randomUUID().toString(),
                                        caregiver = caregiverUid,
                                        title = taskName,
                                        employee = uid,
                                        completionTimeEstimate = time,
                                        status = TaskStatus.READY.toString()
                                    ),
                                    // subtasks = TODO()
                                )
                            } else {
                                Log.d("Db", "Nessun utente trovato con quell'email.")
                            }
                        }
                    },
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