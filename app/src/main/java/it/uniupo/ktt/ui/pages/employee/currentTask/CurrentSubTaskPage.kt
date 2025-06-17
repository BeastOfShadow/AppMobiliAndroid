package it.uniupo.ktt.ui.pages.employee.currentTask

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.CustomTextField
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.firebase.BaseRepository
import it.uniupo.ktt.ui.pages.employee.taskmanager.DailyTaskScreen
import it.uniupo.ktt.ui.subtaskstatus.SubtaskStatus
import it.uniupo.ktt.ui.taskstatus.TaskStatus
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.lightGray
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.HomeScreenViewModel
import it.uniupo.ktt.viewmodel.SubTaskViewModel
import it.uniupo.ktt.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun CurrentSubtaskPage(navController: NavController, taskId: String, homeVm: HomeScreenViewModel) {
    if (!LocalInspectionMode.current && !BaseRepository.isUserLoggedIn()) {
        navController.navigate("login") {
            popUpTo("login") { inclusive = false } // rimuovi tutte le Page nello Stack fino a Landing senza eliminare quest'ultima
            launchSingleTop = true
        }
    }

    val taskViewModel : TaskViewModel = viewModel()
    val subtaskViewModel : SubTaskViewModel = viewModel()

    val task = taskViewModel.getTaskById(taskId)
    val userSubTasksMap by homeVm.userSubTasksMap.collectAsState()
    val subTasks = userSubTasksMap[taskId] ?: emptyList()
    var subTask by remember(subTasks) {
        mutableStateOf(subTasks.find { it.status == SubtaskStatus.RUNNING.toString() })
    }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val visibleImage = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(30.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            PageTitle(
                navController = navController,
                title = "Current Subtask"
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
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
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (subTasks.isEmpty() && task != null) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,

                            fontSize = 20.sp,
                            fontWeight = FontWeight(500),

                            color = titleColor,
                        )
                        Text(
                            text = "Description:",
                            style = MaterialTheme.typography.bodyLarge,

                            fontSize = 16.sp,
                            fontWeight = FontWeight(500),

                            color = titleColor,
                        )
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyLarge,

                            fontSize = 14.sp,
                            fontWeight = FontWeight(500),

                            color = titleColor,
                        )

                        Spacer(modifier = Modifier.height(20.dp))

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
                                    taskViewModel.updateTaskStatus(taskId, TaskStatus.COMPLETED)
                                    navController.navigate("daily task") {
                                        popUpTo("current subtask") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Commit",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    val subTaskAvailable = subTasks.find { it.status == SubtaskStatus.AVAILABLE.toString() }

                    LaunchedEffect(subTasks) {
                        if (subTask == null && subTaskAvailable != null) {
                            subtaskViewModel.updateSubtaskStatus(
                                taskId,
                                subTaskAvailable.id,
                                SubtaskStatus.RUNNING.toString()
                            )
                            // Aggiorna localmente per mostrare subito il cambiamento nella UI
                            subTask = subTaskAvailable.copy(status = SubtaskStatus.RUNNING.toString())
                        }
                    }

                    if(subTask != null) {
                        subTask?.let { subTask ->
                            Box(
                                modifier = Modifier
                                    .height(27.dp)
                                    .background(
                                        color = lightGray,
                                        shape = CircleShape
                                    )
                                    .padding(start = 6.dp, end = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = "${subTask.listNumber}/${subTasks.size}",
                                    fontSize = 14.sp,
                                    color = buttonTextColor,
                                )
                            }

                            if (subTask.descriptionImgStorageLocation != "") {
                                var subTaskEmployeeImgUrl by remember { mutableStateOf<String?>(null) }
                                var isLoading by remember { mutableStateOf(false) }

                                LaunchedEffect(subTask) {
                                    isLoading = true
                                    taskViewModel.getSubtaskImageUrl(
                                        subTask,
                                        onSuccess = { imageUrl ->
                                            subTaskEmployeeImgUrl = imageUrl
                                            isLoading = false
                                            Log.d("Image URL", imageUrl)
                                        },
                                        onError = { exception ->
                                            Log.e(
                                                "Image Error",
                                                "Errore nel caricamento immagine",
                                                exception
                                            )
                                            isLoading = false
                                        }
                                    )
                                }

                                if (isLoading) {
                                    CircularProgressIndicator()
                                } else {
                                    subTaskEmployeeImgUrl?.let { imageUrl ->
                                        AsyncImage(
                                            model = imageUrl,
                                            contentDescription = "Subtask Image",
                                            modifier = Modifier
                                                .size(280.dp)
                                                .scale(0.8f)
                                        )
                                    }
                                }
                            }


                            val launcher =
                                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                                    uri?.let {
                                        selectedImageUri.value = it
                                        visibleImage.value = false
                                    }
                                }

                            Text(
                                text = "Description:",
                                style = MaterialTheme.typography.bodyLarge,

                                fontSize = 18.sp,
                                fontWeight = FontWeight(500),

                                color = titleColor,
                            )

                            Text(
                                text = subTask.description,
                                style = MaterialTheme.typography.bodyLarge,

                                fontSize = 14.sp,
                                fontWeight = FontWeight(500),

                                color = titleColor,
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            val comment = remember { mutableStateOf("") }

                            Text(
                                text = "Comment:",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFF403E3E),
                                ),
                                modifier = Modifier.padding(start = 20.dp)
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            TextField(
                                value = comment.value,
                                onValueChange = { comment.value = it },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    cursorColor = Color.Black,
                                    disabledLabelColor = Color.Red,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false
                                    )
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(start = 10.dp, end = 5.dp),
                                trailingIcon = {
                                    if (comment.value.isNotEmpty()) {
                                        IconButton(onClick = { comment.value = "" }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Close,
                                                contentDescription = "Clear text"
                                            )
                                        }
                                    }
                                },
                                shape = MaterialTheme.shapes.extraLarge,
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

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
                                if (selectedImageUri.value != null) {
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
                                                visibleImage.value = !visibleImage.value
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
                                                if (visibleImage.value) "Hide" else "See",
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
                                if (selectedImageUri.value == null) {
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
                                if (selectedImageUri.value != null) {
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
                            if (visibleImage.value) {
                                Spacer(modifier = Modifier.size(20.dp))
                                Row(
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    selectedImageUri.value?.let { uri ->
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = "Anteprima immagine",
                                            modifier = Modifier
                                                .size(200.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

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
                                        // TODO: fix image storage (non salva di nuovo l'immaigne...), fix routing system
                                        subtaskViewModel.commitSubtask(
                                            taskId = taskId,
                                            subtask = subTask,
                                            newStatus = SubtaskStatus.COMPLETED.toString(),
                                            comment = comment.value,
                                            localImagePath = selectedImageUri.value.toString(),
                                            onSuccess = {
                                                val nextSubTaskIndex =
                                                    subTask.listNumber // perché parte da 1

                                                if (nextSubTaskIndex < subTasks.size) {
                                                    val nextSubTask =
                                                        subTasks.find { it.listNumber == nextSubTaskIndex + 1 }

                                                    nextSubTask?.let { next ->
                                                        subtaskViewModel.viewModelScope.launch {
                                                            val success =
                                                                subtaskViewModel.updateSubtaskStatus(
                                                                    taskId = taskId,
                                                                    subtaskId = next.id,
                                                                    newStatus = SubtaskStatus.RUNNING.toString()
                                                                )

                                                            if (success) {
                                                                navController.navigate("daily task") {
                                                                    popUpTo("current subtask") {
                                                                        inclusive = true
                                                                    }
                                                                    launchSingleTop = true
                                                                }
                                                            } else {
                                                                Log.e(
                                                                    "UI",
                                                                    "❌ Fallita l'attivazione della prossima subtask"
                                                                )
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    taskViewModel.updateTaskStatus(
                                                        taskId = taskId,
                                                        newStatus = TaskStatus.COMPLETED
                                                    )
                                                    navController.navigate("daily task") {
                                                        popUpTo("current subtask") {
                                                            inclusive = true
                                                        }
                                                        launchSingleTop = true
                                                    }
                                                }
                                            },
                                            onError = { exception ->
                                                Log.e(
                                                    "UI",
                                                    "Errore commit subtask: ${exception.message}"
                                                )
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Commit",
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