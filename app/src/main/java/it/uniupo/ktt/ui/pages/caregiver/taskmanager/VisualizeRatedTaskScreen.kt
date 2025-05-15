package it.uniupo.ktt.ui.pages.caregiver.taskmanager

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Expand
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.RatingComponent
import it.uniupo.ktt.ui.components.task.ReadOnlyTextField
import it.uniupo.ktt.ui.components.task.ReadOnlyTextFieldPreview
import it.uniupo.ktt.ui.components.task.taskmanager.ChipsFilter
import it.uniupo.ktt.ui.components.task.taskmanager.NullMessage
import it.uniupo.ktt.ui.components.task.taskmanager.TextSection
import it.uniupo.ktt.ui.firebase.BaseRepository.currentUid
import it.uniupo.ktt.ui.firebase.UserRepository.getEmployeeName
import it.uniupo.ktt.ui.model.Task
import it.uniupo.ktt.ui.taskstatus.TaskStatus
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.lightGray
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.subtitleColor
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun VisualizeRatedTaskScreen(navController: NavController, taskId: String) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("task manager") { inclusive = true }
            launchSingleTop = true
        }
    }

    val maxStars = 5
    var selectedSubtaskIndex by remember { mutableStateOf(-1) }
    var showSubtaskDetailDialog by remember { mutableStateOf(false) }

    val viewModel: TaskViewModel = viewModel()
    val task = viewModel.getTaskById(taskId)
    val subtasks = viewModel.getSubtasksByTaskId(taskId)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(bottom = 50.dp)
                .verticalScroll(rememberScrollState())
        ) {
            PageTitle(
                navController = navController,
                title = "Task Rating"
            )

            Spacer(modifier = Modifier.size(30.dp))

            if (task != null) {
                ReadOnlyTextField(
                    label = "Task name:",
                    value = task.title
                )
            }

            Spacer(modifier = Modifier.size(20.dp))

            if (task != null) {
                ReadOnlyTextField(
                    label = "Employee:",
                    value = getEmployeeName(task.employee)
                )
            }

            Spacer(modifier = Modifier.size(20.dp))

            if (task != null) {
                ReadOnlyTextField(
                    label = "Description:",
                    value = task.description
                )
            }

            Spacer(modifier = Modifier.size(30.dp))

            // Subtask Display Section
            Text(
                text = "Subtask List:",
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF403E3E),
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
                                    .height(180.dp)
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
                                        text = subtask.description,
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
                                            if(subtask.descriptionImgStorageLocation != "") {
                                                Icons.Outlined.Check
                                            } else {
                                                Icons.Outlined.Clear
                                            },
                                            "Photo indicator",
                                            tint = Color.Black,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Icon(
                                        modifier = Modifier.clickable {
                                            showSubtaskDetailDialog = true
                                            selectedSubtaskIndex = index
                                        }.align(Alignment.End),
                                        imageVector = Icons.Outlined.Expand,
                                        contentDescription = "Edit",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                        if (showSubtaskDetailDialog && selectedSubtaskIndex >= 0) {
                            val subtask = subtasks[selectedSubtaskIndex]
                            Dialog(onDismissRequest = { showSubtaskDetailDialog = false }) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFC5B5D8), shape = RoundedCornerShape(16.dp))
                                        .padding(24.dp)
                                        .fillMaxWidth(),
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = subtask.description,
                                            style = TextStyle(
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF403E3E),
                                            ),
                                            modifier = Modifier.padding(bottom = 20.dp)
                                        )

                                        if(subtask.descriptionImgStorageLocation != "") {
                                            AsyncImage(
                                                model = subtask.descriptionImgStorageLocation,
                                                contentDescription = "Anteprima immagine",
                                                modifier = Modifier
                                                    .size(200.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "No image available",
                                                fontSize = 16.sp,
                                                color = subtitleColor
                                            )
                                        }

                                        Spacer(modifier = Modifier.size(30.dp))

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
                                                .align(Alignment.CenterHorizontally)
                                                .clickable {
                                                    showSubtaskDetailDialog = false
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "Close",
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No subtasks available for this task",
                                fontSize = 16.sp,
                                color = subtitleColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(30.dp))

            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxWidth()
                    .shadow(
                        4.dp,
                        shape = MaterialTheme.shapes.extraLarge,
                        clip = false
                    )
                    .background(Color(0xFFF5DFFA), shape = MaterialTheme.shapes.extraLarge)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Overall Rating Comment:",
                        fontSize = 16.sp,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (task != null) {
                        Text(
                            text = task.overallComment,
                            fontSize = 16.sp,
                            color = subtitleColor
                        )
                    }


                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Overall Rating:",
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (index in 0 until maxStars) {
                            val filled = index < (task?.overallRating ?: 0)
                            Icon(
                                imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "Rating ${index + 1} of $maxStars",
                                tint = if (filled) Color(0xFF000000) else Color.Gray,
                                modifier = Modifier
                                    .size(35.dp)
                                    .padding(horizontal = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(20.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(145.dp)
                    .height(45.dp)
                    .shadow(
                        4.dp,
                        shape = MaterialTheme.shapes.large,
                        clip = false
                    )
                    .background(
                        color = tertiary,
                        shape = MaterialTheme.shapes.large
                    ).clickable{
                        navController.navigate("task manager")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Go back",
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
fun VisualizeRatedTaskScreenPreview() {
    VisualizeRatedTaskScreen(navController = NavController(context = LocalContext.current), taskId = "043e4e8e-b4ef-4da0-87fc-973f786d9fd1")
}
