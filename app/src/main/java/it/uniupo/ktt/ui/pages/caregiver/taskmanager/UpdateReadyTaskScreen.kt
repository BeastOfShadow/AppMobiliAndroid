package it.uniupo.ktt.ui.pages.caregiver.taskmanager

import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import it.uniupo.ktt.time.parseDurationToSeconds
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.task.ReadOnlyTextField
import it.uniupo.ktt.ui.components.task.newtask.ActionTaskButton
import it.uniupo.ktt.ui.components.task.newtask.DurationInputField
import it.uniupo.ktt.ui.components.task.newtask.SharePositionSwitch
import it.uniupo.ktt.ui.components.task.newtask.SubtaskImage
import it.uniupo.ktt.ui.components.task.taskmanager.ChipsFilter
import it.uniupo.ktt.ui.components.task.taskmanager.ElapsedTimeDisplay
import it.uniupo.ktt.ui.components.task.taskmanager.NullMessage
import it.uniupo.ktt.ui.components.task.taskmanager.TextSection
import it.uniupo.ktt.ui.firebase.BaseRepository.currentUid
import it.uniupo.ktt.ui.firebase.UserRepository.getEmployeeName
import it.uniupo.ktt.ui.model.SubTask
import it.uniupo.ktt.ui.model.Task
import it.uniupo.ktt.ui.subtaskstatus.SubtaskStatus
import it.uniupo.ktt.ui.taskstatus.TaskStatus
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.lightGray
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.secondary
import it.uniupo.ktt.ui.theme.subtitleColor
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.SubTaskViewModel
import it.uniupo.ktt.viewmodel.TaskViewModel
import it.uniupo.ktt.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun UpdateReadyTaskScreen(navController: NavController, taskId: String) {
  if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
    navController.navigate("landing") {
      popUpTo("task manager") { inclusive = true }
      launchSingleTop = true
    }
  }

  val coroutineScope = rememberCoroutineScope()

  val taskViewModel : TaskViewModel = viewModel()
  val userViewModel : UserViewModel = viewModel()
  val subTaskViewModel : SubTaskViewModel = viewModel()
  val task = taskViewModel.getTaskById(taskId)

  var subTasks by remember { mutableStateOf<List<SubTask>>(emptyList()) }

  var showDeleteDialog by remember { mutableStateOf(false) }
  var subtaskToDelete by remember { mutableStateOf<SubTask?>(null) }

  var titleError by remember { mutableStateOf(false) }
  var employeeError by remember { mutableStateOf(false) }
  var descriptionError by remember { mutableStateOf(false) }
  var timeError by remember { mutableStateOf(false) }

  LaunchedEffect(taskId) {
    subTasks = subTaskViewModel.fetchSubtask(taskId)
  }

  var visible by remember { mutableStateOf(false) }

  if(task != null) {
    var editableTitle by remember { mutableStateOf(task.title) }
    var editableDescription by remember { mutableStateOf(task.description) }
    var mail by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(task.locationNeeded) }
    var duration by remember {
      val seconds = task.completionTimeEstimate
      val hours = seconds / 3600
      val minutes = (seconds % 3600) / 60
      mutableStateOf("%02d:%02d".format(hours, minutes))
    }

    var durationError by remember { mutableStateOf(false) }

    var ausMail by remember { mutableStateOf("") }

    LaunchedEffect(task.employee) {
      val loadedMail = userViewModel.getUserEmailByUuid(task.employee).toString()
      mail = loadedMail
      ausMail = loadedMail
    }



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

        Text(
          text = "Task name:",
          style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF403E3E),
          )
        )

        Spacer(modifier = Modifier.size(10.dp))

        TextField(
          value = editableTitle,
          onValueChange = {
            editableTitle = it
            visible = task.title != it
            titleError = it.isEmpty()
          },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = primary,
            unfocusedContainerColor = primary,
            cursorColor = Color.Black,
            disabledLabelColor = Color.Red,
            focusedIndicatorColor = primary,
            unfocusedIndicatorColor = primary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .shadow(
              4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false
            )
            .clip(CircleShape)
            .background(primary)
            .padding(start = 10.dp, end = 5.dp),
          trailingIcon = {
            if (task.title.isNotEmpty()) {
              IconButton(
                onClick = {
                  editableTitle = ""
                  titleError = editableTitle.isEmpty()
                }) {
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
        if (titleError) {
          Text(
            text = "Task name is required",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
          )
        }

        Spacer(modifier = Modifier.size(20.dp))

        Text(
          text = "Employee:",
          style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF403E3E),
          )
        )

        Spacer(modifier = Modifier.size(10.dp))

        TextField(
          value = mail,
          onValueChange = {
            mail = it
            visible = ausMail != it
            employeeError = it.isEmpty()
          },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = primary,
            unfocusedContainerColor = primary,
            cursorColor = Color.Black,
            disabledLabelColor = Color.Red,
            focusedIndicatorColor = primary,
            unfocusedIndicatorColor = primary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .shadow(
              4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false
            )
            .clip(CircleShape)
            .background(primary)
            .padding(start = 10.dp, end = 5.dp),
          trailingIcon = {
            if (task.title.isNotEmpty()) {
              IconButton(
                onClick = {
                  mail = ""
                  employeeError = mail.isEmpty()
                }) {
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

        if (employeeError) {
          Text(
            text = "Employee mail is required",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
          )
        }

        Spacer(modifier = Modifier.size(20.dp))

        Text(
          text = "Description:",
          style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF403E3E),
          )
        )

        Spacer(modifier = Modifier.size(10.dp))

        TextField(
          value = editableDescription,
          onValueChange = {
            editableDescription = it
            visible = task.description != it
            descriptionError = it.isEmpty()
          },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = primary,
            unfocusedContainerColor = primary,
            cursorColor = Color.Black,
            disabledLabelColor = Color.Red,
            focusedIndicatorColor = primary,
            unfocusedIndicatorColor = primary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .shadow(
              4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false
            )
            .clip(CircleShape)
            .background(primary)
            .padding(start = 10.dp, end = 5.dp),
          trailingIcon = {
            if (task.title.isNotEmpty()) {
              IconButton(
                onClick = {
                  editableDescription = ""
                  descriptionError = editableDescription.isEmpty()
                }) {
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

        if (descriptionError) {
          Text(
            text = "Task description is required",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
          )
        }

        Spacer(modifier = Modifier.size(20.dp))

        Row(
          modifier = Modifier.padding(top = 15.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          SharePositionSwitch(
            isChecked = isChecked,
            onCheckedChange = {
              isChecked = it
              visible = task.locationNeeded != it
            }
          )

          Column {
            DurationInputField(
              duration = duration,
              onDurationChange = {
                duration = it
                durationError = it.isEmpty() || !it.matches(Regex("""^\d{2}:\d{2}$"""))

                visible = if (!durationError) {
                  task.completionTimeEstimate != parseDurationToSeconds(it)
                } else {
                  false
                }
              },
              isError = durationError
            )

            if (durationError) {
              Text(
                text = "Duration is required or invalid format",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
              )
            }
          }
        }

        if (subTasks.isNotEmpty()) {
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
          val editSelectedImageUri = remember { mutableStateOf<Uri?>(null) }

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
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(34.dp),  // altezza uguale all'immagine per allineamento
                    contentAlignment = Alignment.Center
                  ) {
                    Box(
                      modifier = Modifier
                        .size(27.dp)
                        .background(
                          color = lightGray,
                          shape = CircleShape
                        )
                        .align(Alignment.CenterEnd)
                    ) {
                      Text(
                        text = subtask.listNumber.toString(),
                        fontSize = 14.sp,
                        color = buttonTextColor,
                        modifier = Modifier.align(Alignment.Center)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.size(10.dp))

                  Text(
                    text = "Description:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = titleColor,
                    textAlign = TextAlign.Start
                  )

                  Text(
                    text = subtask.description,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp
                  )

                  Spacer(modifier = Modifier.size(10.dp))

                  Row {
                    Text(
                      text = "Photo: ",
                      fontWeight = FontWeight.Bold,
                      fontSize = 16.sp,
                      color = titleColor,
                      textAlign = TextAlign.Start
                    )

                    Icon(
                      if (subtask.descriptionImgStorageLocation != "") {
                        Icons.Outlined.Check
                      } else {
                        Icons.Outlined.Clear
                      },
                      "Large floating action button",
                      tint = Color.Black,
                      modifier = Modifier.size(22.dp)
                    )
                  }

                  Spacer(modifier = Modifier.size(10.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Image(
                      painter = painterResource(id = R.drawable.chat_delete),
                      contentDescription = "Delete",
                      modifier = Modifier
                        .size(24.dp)
                        .clickable {
                          Log.d(
                            "UI",
                            "Clicked delete on subtask id=${subtask.id}, description=${subtask.description}"
                          )
                          subtaskToDelete = subtask
                          showDeleteDialog = true
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
                          editSelectedImageUri.value =
                            if (subtask.descriptionImgStorageLocation != "")
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
                .clickable {
                  showDialog = true
                }
                .shadow(
                  4.dp,
                  shape = MaterialTheme.shapes.extraLarge,
                  clip = false
                )
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

          if (showDeleteDialog && subtaskToDelete != null) {
            AlertDialog(
              onDismissRequest = { showDeleteDialog = false },
              title = { Text("Confirm Delete") },
              text = { Text("Are you sure you want to delete this subtask?") },
              confirmButton = {
                Button(
                  onClick = {
                    val subToDel = subtaskToDelete!!
                    coroutineScope.launch {
                      Log.d("UI", "Delete requested for subtask ${subtaskToDelete?.id}")
                      subTaskViewModel.deleteTaskById(taskId, subToDel.id)
                      Log.d("UI", "Deleted subtask, fetching updated list")
                      subTasks = subTaskViewModel.fetchSubtask(taskId)
                    }
                    showDeleteDialog = false
                    subtaskToDelete = null
                  }
                ) {
                  Text("Delete")
                }
              },
              dismissButton = {
                Button(
                  onClick = {
                    showDeleteDialog = false
                    subtaskToDelete = null
                  }
                ) {
                  Text("Cancel")
                }
              }
            )
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
                coroutineScope.launch {
                  val nextIndex = subTasks.maxOfOrNull { it.listNumber }?.plus(1) ?: 1
                  subTaskViewModel.addSubtask(
                    taskId = taskId,
                    description = description,
                    imageUri = imageUri?.toString() ?: "",
                    listNumber = nextIndex,
                    storageLocation = "descriptionSubtaskImages",
                    onSuccess = {
                      coroutineScope.launch {
                        subTasks = subTaskViewModel.fetchSubtask(taskId)
                      }
                    },
                    onError = {
                      Log.e("UpdateSubtask", "Errore durante l'aggiunta del subtask")
                    }
                  )
                }
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
                  subTaskViewModel.updateSubtask(
                    taskId,
                    subtask.copy(
                      id = subtask.id,
                      description = description,
                      descriptionImgStorageLocation = imageUri?.toString() ?: ""
                    ),
                    storageLocation = "descriptionSubtaskImages",
                    onSuccess = {
                      coroutineScope.launch {
                        subTasks = subTaskViewModel.fetchSubtask(taskId)
                      }
                    },
                    onError = {
                      Log.e("UpdateSubtask", "Errore durante l'aggiornamento del subtask")
                    }
                  )
                }
                showEditDialog = false
                editSubtaskIndex = -1
              }
            )
          }
        }
      }
      if (visible) {
        ActionTaskButton(
          onCancel = { navController.popBackStack() },
          onConfirm = {
            if (!(titleError && employeeError && descriptionError && timeError)) {
              coroutineScope.launch {
                val caregiverUid = currentUid()

                if (caregiverUid == null) {
                  Log.d("INBUTTON", "Caregiver non loggato.")
                  return@launch
                }

                val uid = userViewModel.getUidByEmail(mail)

                if (uid != null) {
                  val time = parseDurationToSeconds(duration)

                  taskViewModel.updateTask(
                    taskId,
                    task.copy(
                      title = editableTitle,
                      description = editableDescription,
                      employee = uid,
                      locationNeeded = isChecked,
                      completionTimeEstimate = time
                    )
                  )
                  navController.popBackStack()
                } else {
                  Log.d("INBUTTON", "Nessun utente trovato con quell'email.")
                  employeeError = true
                }
              }
            }
          },
          modifier = Modifier.align(Alignment.BottomCenter)
        )
      }
    }
  }
}


@Preview
@Composable
fun UpdateReadyTaskScreenPreview() {
  UpdateReadyTaskScreen(navController = NavController(context = LocalContext.current), "dhfdsjh")
}
