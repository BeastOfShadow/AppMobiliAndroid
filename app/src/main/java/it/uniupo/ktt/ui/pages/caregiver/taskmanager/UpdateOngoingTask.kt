package it.uniupo.ktt.ui.pages.caregiver.taskmanager

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.R
import it.uniupo.ktt.time.isToday
import it.uniupo.ktt.ui.components.MenuLabel
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.task.ReadOnlyTextField
import it.uniupo.ktt.ui.components.task.newtask.DurationInputField
import it.uniupo.ktt.ui.components.task.newtask.SharePositionSwitch
import it.uniupo.ktt.ui.components.task.newtask.SubtaskImage
import it.uniupo.ktt.ui.components.task.taskmanager.ChipsFilter
import it.uniupo.ktt.ui.components.task.taskmanager.ElapsedTimeDisplay
import it.uniupo.ktt.ui.components.task.taskmanager.NullMessage
import it.uniupo.ktt.ui.components.task.taskmanager.TextSection
import it.uniupo.ktt.ui.firebase.BaseRepository.currentUid
import it.uniupo.ktt.ui.firebase.UserRepository.getEmployeeName
import it.uniupo.ktt.ui.model.Task
import it.uniupo.ktt.ui.taskstatus.TaskStatus
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.secondary
import it.uniupo.ktt.ui.theme.subtitleColor
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun UpdateOngoingTaskScreen(navController: NavController, taskId: String) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("task manager") { inclusive = true }
            launchSingleTop = true
        }
    }

    val taskViewModel : TaskViewModel = viewModel()
    val task = taskViewModel.getTaskById(taskId)

    val subTasks = taskViewModel.getSubtasksByTaskId(taskId)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            PageTitle(
                navController = navController,
                title = "Update Task"
            )

            Spacer(modifier = Modifier.size(20.dp))

            if (task != null) {
                ReadOnlyTextField(
                    label = "Task name:",
                    value = task.title
                )

                Spacer(modifier = Modifier.size(20.dp))

                ReadOnlyTextField(
                    label = "Employee:",
                    value = getEmployeeName(task.employee)

                )

                Spacer(modifier = Modifier.size(20.dp))

                ReadOnlyTextField(
                    label = "Description:",
                    value = task.description
                )

                Row(
                    modifier = Modifier.padding(top = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SharePositionSwitch(
                        isChecked = task.locationNeeded,
                        onCheckedChange = { }
                    )

                    Column {
                        val seconds = task.completionTimeEstimate
                        val hours = seconds / 3600
                        val minutes = (seconds % 3600) / 60
                        val time = "%02d:%02d".format(hours, minutes)
                        DurationInputField(
                            duration = time,
                            onDurationChange = {}, // Non serve azione
                            isError = false
                        )
                    }
                }
            }

            if(subTasks.isNotEmpty())
            {
                Text(
                    text = "Subtask List:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                )

                val coroutineScope = rememberCoroutineScope()
                var showDialog by remember { mutableStateOf(false) }
                var showEditDialog by remember { mutableStateOf(false) }
                var editSubtaskIndex by remember { mutableStateOf(-1) }
                var editSubtaskDescription by remember { mutableStateOf("") }
                var editSelectedImageUri = remember { mutableStateOf<Uri?>(null) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    subTasks.forEachIndexed { index, subtask ->
                        Box(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .width(180.dp)
                                .height(220.dp)
                                .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge)
                                .background(primary, shape = MaterialTheme.shapes.extraLarge)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Subtask ${subtask.listNumber}", fontWeight = FontWeight.Bold)

                                Text(
                                    text = subtask.description,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 14.sp
                                )

                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                    Image(
                                        painter = painterResource(id = R.drawable.chat_delete),
                                        contentDescription = "Delete",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                /*coroutineScope.launch {
                                                    taskViewModel.deleteSubtask(taskId, subtask.id)

                                                    // Ricrea l'indice corretto (listNumber)
                                                    val updatedList = taskViewModel.getSubtasksByTaskId(taskId)
                                                        .sortedBy { it.listNumber }
                                                        .mapIndexed { i, s ->
                                                            s.copy(listNumber = i + 1)
                                                        }

                                                    updatedList.forEach {
                                                        taskViewModel.updateSubtask(it)
                                                    }
                                                }*/
                                            }
                                    )

                                    Image(
                                        painter = painterResource(id = R.drawable.edit_rewrite),
                                        contentDescription = "Edit",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                editSubtaskIndex = index
                                                editSubtaskDescription = subtask.description
                                                editSelectedImageUri.value = if (subtask.descriptionImgStorageLocation != "null")
                                                    Uri.parse(subtask.descriptionImgStorageLocation)
                                                else null
                                                showEditDialog = true
                                            }
                                    )
                                }
                            }
                        }
                    }

                    // Pulsante per aggiungere nuovo subtask
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .width(100.dp)
                            .height(100.dp)
                            .clickable { showDialog = true }
                            .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge)
                            .background(primary, shape = MaterialTheme.shapes.extraLarge)
                            .padding(16.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add", modifier = Modifier.align(Alignment.Center))
                    }
                }

                if (showDialog) {
                    SubtaskImage(
                        title = "Add Subtask",
                        initialDescription = "",
                        initialImageUri = null,
                        showDialog = true,
                        onDismiss = {
                            showDialog = false
                        },
                        onSave = { description, imageUri ->
                            /*coroutineScope.launch {
                                val nextIndex = subTasks.maxOfOrNull { it.listNumber }?.plus(1) ?: 1
                                taskViewModel.addSubtask(
                                    taskId = taskId,
                                    description = description,
                                    descriptionImgStorageLocation = imageUri.toString(),
                                    listNumber = nextIndex
                                )
                            }*/
                            showDialog = false
                        }
                    )
                }

                if (showEditDialog && editSubtaskIndex >= 0) {
                    val subtask = subTasks[editSubtaskIndex]
                    SubtaskImage(
                        title = "Edit Subtask",
                        initialDescription = editSubtaskDescription,
                        initialImageUri = editSelectedImageUri.value,
                        showDialog = true,
                        onDismiss = {
                            showEditDialog = false
                            editSubtaskIndex = -1
                        },
                        onSave = { description, imageUri ->
                            coroutineScope.launch {
                                /*taskViewModel.updateSubtask(
                                    subtask.copy(
                                        description = description,
                                        descriptionImgStorageLocation = imageUri.toString()
                                    )
                                )*/
                            }
                            showEditDialog = false
                            editSubtaskIndex = -1
                        }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun UpdateOngoingTaskScreenPreview() {
    UpdateOngoingTaskScreen(navController = NavController(context = LocalContext.current), "dhfdsjh")
}