package it.uniupo.ktt.ui.pages.employee.taskmanager

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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Expand
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.Button
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
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.components.task.CircularTimer
import it.uniupo.ktt.ui.components.task.ReadOnlyTextField
import it.uniupo.ktt.ui.components.task.taskmanager.ChipsFilter
import it.uniupo.ktt.ui.components.task.taskmanager.ElapsedTimeDisplay
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
fun ViewTaskScreen(navController: NavController, taskId: String) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("view_task") { inclusive = true }
            launchSingleTop = true
        }
    }

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
                .verticalScroll(rememberScrollState())
        ) {
            PageTitle(
                navController = navController,
                title = "Task View"
            )

            if (task != null) {
                Spacer(modifier = Modifier.size(20.dp))

                ReadOnlyTextField(
                    label = "Task name:",
                    value = task.title
                )
            }

            if (task != null) {
                Spacer(modifier = Modifier.size(20.dp))

                ReadOnlyTextField(
                    label = "Task description:",
                    value = task.description
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                // Primo Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
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
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (task != null) {
                            if(task.active) CircularTimer(task)
                            else
                                Text(
                                    text = "Task not active",
                                    fontSize = 14.sp,
                                    color = subtitleColor,
                                    textAlign = TextAlign.Center
                                )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp)) // Spazio tra i due box

                // Secondo Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false)
                        .background(primary, shape = MaterialTheme.shapes.extraLarge)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (task != null) {
                        if (task.locationNeeded) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(27.dp)
                                        .background(lightGray, CircleShape)
                                        .align(Alignment.End)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocationOn,
                                        contentDescription = "Vehicle Icon",
                                        tint = Color.Black,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(45.dp)
                                        .shadow(4.dp, shape = MaterialTheme.shapes.large, clip = false)
                                        .background(tertiary, shape = MaterialTheme.shapes.large)
                                        .clickable {
                                            // Azione quando c'è location
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Location",
                                        fontSize = 14.sp,
                                        color = buttonTextColor
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Location not needed",
                                    fontSize = 14.sp,
                                    color = subtitleColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

            }

            if(subtasks.isNotEmpty()) {
                Spacer(modifier = Modifier.size(30.dp))

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

                                    Icon(
                                        modifier = Modifier.clickable {
                                            navController.navigate("subtask_view/${taskId}/${subtask.id}")
                                        }.align(Alignment.End),
                                        imageVector = Icons.Outlined.Expand,
                                        contentDescription = "Expand",
                                        tint = Color.Black
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


@Preview
@Composable
fun ViewTaskScreenPreview() {
    ViewTaskScreen(navController = NavController(context = LocalContext.current), "dscjfdsljl")
}
