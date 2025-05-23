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
import androidx.compose.material.icons.outlined.RateReview
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
fun DailyTaskScreen(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("daily task") { inclusive = true }
            launchSingleTop = true
        }
    }

    val taskViewModel : TaskViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()

    var tasks by remember { mutableStateOf(emptyList<Task>()) }

    LaunchedEffect(Unit) {
        val uid = currentUid()
        if (uid != null) {
            val allTasks = taskViewModel.getTasksByEmployeeId(uid)
            tasks = allTasks
                .filter { isToday(it.timeStampStart) }
                .sortedBy { it.timeStampStart }
        }
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
                title = "Daily Tasks"
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text(
                text = "Task List",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = titleColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(20.dp))

            if(tasks.isEmpty()) {
                Text(
                    text = "No tasks today",
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    color = subtitleColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Column {
                    tasks.forEachIndexed { index, task ->
                        Box(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .fillMaxWidth()
                                .shadow(
                                    4.dp,
                                    shape = MaterialTheme.shapes.extraLarge,
                                    clip = false
                                )
                                .background(primary, shape = MaterialTheme.shapes.extraLarge)
                                .clickable {
                                    navController.navigate("view_task/${task.id}")
                                }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(27.dp)
                                        .background(
                                            color = lightGray,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 14.sp,
                                        color = buttonTextColor,
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Description:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = titleColor,
                                )

                                Text(
                                    text = task.description,
                                    fontWeight = FontWeight.Light,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(0.8f)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                val canStart = !task.active && tasks.subList(0, index).all { !it.active && it.status == TaskStatus.COMPLETED.toString() }

                                if(task.active && task.completionTimeActual != 0){
                                    Image(
                                        painter = painterResource(id = R.drawable.task_done),
                                        contentDescription = "Task Completed",
                                        modifier = Modifier
                                            .size(44.dp)
                                            .padding(top = 8.dp)
                                    )
                                } else if (task.active) {
                                    Image(
                                        painter = painterResource(id = R.drawable.task_running),
                                        contentDescription = "Task Completed",
                                        modifier = Modifier
                                            .size(44.dp)
                                            .padding(top = 8.dp)
                                    )
                                } else if (canStart) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .shadow(4.dp, shape = CircleShape, clip = false)
                                            .clickable {
                                                coroutineScope.launch {
                                                    taskViewModel.startTaskEmployee(task.id)
                                                    // Dopo aver avviato il task, ricarica la lista
                                                    val uid = currentUid()
                                                    if (uid != null) {
                                                        val allTasks = taskViewModel.getTasksByEmployeeId(uid)
                                                        tasks = allTasks.filter { isToday(it.timeStampStart) }
                                                    }
                                                }
                                            }
                                            .background(
                                                color = tertiary,
                                                shape = CircleShape
                                            )
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RocketLaunch,
                                            contentDescription = "Start",
                                            tint = buttonTextColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .shadow(4.dp, shape = CircleShape, clip = false)
                                            .background(
                                                color = tertiary,
                                                shape = CircleShape
                                            )
                                            .then(Modifier.background(Color.Gray.copy(alpha = 0.4f), shape = CircleShape))
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RocketLaunch,
                                            contentDescription = "Start",
                                            tint = buttonTextColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun DailyTaskScreenPreview() {
    DailyTaskScreen(navController = NavController(context = LocalContext.current))
}
