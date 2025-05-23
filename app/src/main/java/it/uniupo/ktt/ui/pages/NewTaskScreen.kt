package it.uniupo.ktt.ui.pages

//import coil.compose.AsyncImage
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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
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
import it.uniupo.ktt.time.parseDurationToSeconds
import it.uniupo.ktt.ui.components.CustomTextField
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.task.newtask.ActionTaskButton
import it.uniupo.ktt.ui.components.task.newtask.DurationInputField
import it.uniupo.ktt.ui.components.task.newtask.SharePositionSwitch
import it.uniupo.ktt.ui.components.task.newtask.SubtaskImage
import it.uniupo.ktt.ui.firebase.BaseRepository.currentUid
import it.uniupo.ktt.ui.model.SubTask
import it.uniupo.ktt.ui.model.Task
import it.uniupo.ktt.ui.subtaskstatus.SubtaskStatus
import it.uniupo.ktt.ui.taskstatus.TaskStatus
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.lightGray
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.TaskViewModel
import it.uniupo.ktt.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.util.UUID

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
    var description by remember { mutableStateOf("") }

    var duration by remember { mutableStateOf("") }
    var subtaskDescription by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }

    // Aggiungiamo variabili di stato per gli errori dei campi obbligatori
    var taskNameError by remember { mutableStateOf(false) }
    var employeeError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }
    var durationError by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteIndex by remember { mutableStateOf(-1) }

    // Edit subtask related state variables
    var showEditDialog by remember { mutableStateOf(false) }
    var editSubtaskIndex by remember { mutableStateOf(-1) }
    var editSubtaskDescription by remember { mutableStateOf("") }
    val editSelectedImageUri = remember { mutableStateOf<Uri?>(null) }

    var subtasks by remember { mutableStateOf<List<SubTask>>(emptyList()) }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri.value = it }
    }

    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { editSelectedImageUri.value = it }
    }

    // Funzione per validare i campi
    fun validateFields(): Boolean {
        var isValid = true

        if (taskName.trim().isEmpty()) {
            taskNameError = true
            isValid = false
        } else taskNameError = false


        if (employee.trim().isEmpty()) {
            employeeError = true
            isValid = false
        } else employeeError = false

        if (description.trim().isEmpty()) {
            descriptionError = true
            isValid = false
        } else descriptionError = false

        val regex = """^([0-9]{1,2}):([0-5][0-9])$""".toRegex()

        if (duration.trim().isEmpty() || !regex.matches(duration)) {
            durationError = true
            isValid = false
        } else durationError = false

        return isValid
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
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState())
        ) {
            PageTitle(
                navController = navController,
                title = "New Task"
            )

            Spacer(modifier = Modifier.size(30.dp))

            // TextField per taskName con messaggio di errore
            Column {
                CustomTextField(
                    label = "Task name:",
                    textfieldValue = taskName,
                    onValueChange = {
                        taskName = it
                        if (it.isNotEmpty()) taskNameError = false
                    },
                    isError = taskNameError
                )
                if (taskNameError) {
                    Text(
                        text = "Task name is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(20.dp))

            // TextField per employee con messaggio di errore
            Column {
                CustomTextField(
                    label = "Employee:",
                    textfieldValue = employee,
                    onValueChange = {
                        employee = it
                        if (it.isNotEmpty()) employeeError = false
                    },
                    isError = employeeError
                )
                if (employeeError) {
                    Text(
                        text = "Employee is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(20.dp))

            Column {
                CustomTextField(
                    label = "Description:",
                    textfieldValue = description,
                    onValueChange = {
                        description = it
                        if (it.isNotEmpty()) descriptionError = false
                    },
                    isError = descriptionError
                )
                if (descriptionError) {
                    Text(
                        text = "Description is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.padding(top = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SharePositionSwitch(
                    isChecked = isChecked,
                    onCheckedChange = { isChecked = it }
                )

                // Campo duration con messaggio di errore
                Column {
                    DurationInputField(
                        duration = duration,
                        onDurationChange = {
                            duration = it
                            if (it.isNotEmpty()) durationError = false
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
                        subtasks.forEachIndexed { index, subtask ->
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

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.chat_delete),
                                            contentDescription = "Edit",
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable {
                                                    deleteIndex = index
                                                    showDeleteDialog = true
                                                },
                                        )
                                        Image(
                                            painter = painterResource(id = R.drawable.edit_rewrite),
                                            contentDescription = "Edit",
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable {
                                                    // Set up the edit dialog with the current subtask data
                                                    editSubtaskIndex = index
                                                    editSubtaskDescription = subtasks[index].description
                                                    if (subtasks[index].descriptionImgStorageLocation != "null") {
                                                        editSelectedImageUri.value = Uri.parse(subtasks[index].descriptionImgStorageLocation)
                                                    } else {
                                                        editSelectedImageUri.value = null
                                                    }
                                                    showEditDialog = true
                                                },
                                        )
                                    }

                                    if (showDeleteDialog) {
                                        AlertDialog(
                                            onDismissRequest = { showDeleteDialog = false },
                                            title = { Text("Delete " + subtasks[deleteIndex].description) },
                                            text = { Text("Are you sure you want to delete this subtask?") },
                                            confirmButton = {
                                                TextButton(onClick = {
                                                    subtasks = subtasks
                                                        .toMutableList()
                                                        .also { it.removeAt(deleteIndex) }
                                                        .mapIndexed { idx, subtask ->
                                                            subtask.copy(listNumber = idx + 1)
                                                        }
                                                    showDeleteDialog = false
                                                }) {
                                                    Text("Delete")
                                                }
                                            },
                                            dismissButton = {
                                                TextButton(onClick = { showDeleteDialog = false }) {
                                                    Text("Cancel")
                                                }
                                            }
                                        )
                                    }
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

                        // Add Subtask Dialog
                        if (showDialog) {
                            var showAddDialog by remember { mutableStateOf(true) }
                            SubtaskImage(
                                title = "Add Subtask",
                                initialDescription = subtaskDescription,
                                initialImageUri = selectedImageUri.value,
                                showDialog = showAddDialog,
                                onDismiss = {
                                    showAddDialog = false
                                    showDialog = false
                                    subtaskDescription = ""
                                    selectedImageUri.value = null
                                },
                                onSave = { description, imageUri ->
                                    val nextNumber = if (subtasks.isEmpty()) 1 else subtasks.last().listNumber + 1
                                    subtasks += SubTask(
                                        id = UUID.randomUUID().toString(),
                                        listNumber = nextNumber,
                                        description = description,
                                        descriptionImgStorageLocation = imageUri.toString(),
                                        status = SubtaskStatus.AVAILABLE.toString()
                                    )

                                    showAddDialog = false
                                    showDialog = false
                                    subtaskDescription = ""
                                    selectedImageUri.value = null
                                }
                            )
                        }

                        if (showEditDialog && editSubtaskIndex >= 0) {
                            var showEditDialogState by remember { mutableStateOf(true) }

                            SubtaskImage(
                                title = "Edit Subtask",
                                initialDescription = editSubtaskDescription,
                                initialImageUri = editSelectedImageUri.value,
                                showDialog = showEditDialogState,
                                onDismiss = {
                                    showEditDialogState = false
                                    showEditDialog = false
                                    editSubtaskDescription = ""
                                    editSelectedImageUri.value = null
                                },
                                onSave = { description, imageUri ->
                                    // Create updated subtask with edited values
                                    val updatedSubtask = subtasks[editSubtaskIndex].copy(
                                        description = description,
                                        descriptionImgStorageLocation = imageUri.toString()
                                    )
                                    // Replace the old subtask with the updated one
                                    subtasks = subtasks.toMutableList().also {
                                        it[editSubtaskIndex] = updatedSubtask
                                    }

                                    showEditDialogState = false
                                    showEditDialog = false
                                    editSubtaskDescription = ""
                                    editSelectedImageUri.value = null
                                }
                            )
                        }
                    }
                }
            }
        }

        ActionTaskButton(
            onCancel = { navController.popBackStack() },
            onConfirm = {
                if (validateFields()) {
                    coroutineScope.launch {
                        val caregiverUid = currentUid()

                        if (caregiverUid == null) {
                            Log.d("Db", "Caregiver non loggato.")
                            return@launch
                        }

                        val uid = userViewModel.getUidByEmail(employee)

                        if (uid != null) {
                            val time = parseDurationToSeconds(duration)

                            taskViewModel.addTaskAndSubtasks(
                                task = Task(
                                    id = UUID.randomUUID().toString(),
                                    caregiver = caregiverUid,
                                    title = taskName,
                                    employee = uid,
                                    description = description,
                                    completionTimeEstimate = time,
                                    status = TaskStatus.READY.toString(),
                                    locationNeeded = isChecked
                                ),
                                subtasks = subtasks
                            )
                            navController.popBackStack()
                        } else {
                            Log.d("Db", "Nessun utente trovato con quell'email.")
                            employeeError = true
                        }
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
fun NewTaskScreenPreview() {
    NewTaskScreen(navController = NavController(context = LocalContext.current))
}