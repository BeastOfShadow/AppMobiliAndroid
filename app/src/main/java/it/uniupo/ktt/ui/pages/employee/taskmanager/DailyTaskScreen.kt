package it.uniupo.ktt.ui.pages.employee.taskmanager

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.R
import it.uniupo.ktt.time.isToday
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.firebase.BaseRepository.currentUid
import it.uniupo.ktt.ui.subtaskstatus.SubtaskStatus
import it.uniupo.ktt.ui.taskstatus.TaskStatus
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.lightGray
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.subtitleColor
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.HomeScreenViewModel
import it.uniupo.ktt.viewmodel.SubTaskViewModel
import it.uniupo.ktt.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun DailyTaskScreen(navController: NavController, homeVm: HomeScreenViewModel) {
    val tag = "DailyTaskScreen"
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        Log.e(tag, "User not logged in, navigating to landing")
        navController.navigate("landing") {
            popUpTo("daily task") { inclusive = true }
            launchSingleTop = true
        }
        return
    }

    val taskViewModel: TaskViewModel = viewModel()
    val subTaskViewModel: SubTaskViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        currentUid()?.let { uid ->
            Log.e(tag, "LaunchedEffect - observing user tasks for uid: $uid")
            homeVm.observeUserTasks(uid)
            taskViewModel.loadTodayTasksEmpolyee(uid)
        }
    }

    val tasks by homeVm.userTasksList.collectAsState()
    val isLoading by homeVm.isLoadingTasks.collectAsState()
    Log.e(tag, "Collected tasks: $tasks")
    Log.e(tag, "Loading state: $isLoading")

    val todayTasks = tasks.filter {
        isToday(it.createdAt)
                && (it.status == TaskStatus.ONGOING.toString()
                    || it.status == TaskStatus.RATED.toString()
                    || it.status == TaskStatus.COMPLETED.toString()) }.sortedBy { it.createdAt }
    Log.e(tag, "Filtered today's tasks: $todayTasks")

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
            PageTitle(navController = navController, title = "Daily Tasks")
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Task List",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = titleColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            when {
                isLoading -> {
                    Log.e(tag, "Tasks are loading...")
                    CircularProgressIndicator()
                }
                todayTasks.isEmpty() -> {
                    Log.e(tag, "No tasks found for today.")
                    Text(
                        text = "No tasks today",
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                        color = subtitleColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    Column {
                        todayTasks.forEachIndexed { index, task ->
                            val canStart = task.status !in listOf(
                                TaskStatus.COMPLETED.toString(),
                                TaskStatus.RATED.toString()
                            ) && todayTasks.subList(0, index).all {
                                it.status in listOf(
                                    TaskStatus.COMPLETED.toString(),
                                    TaskStatus.RATED.toString()
                                )
                            }

                            Log.e(tag, "Task $index: id=${task.id}, canStart=$canStart, status=${task.status}, active=${task.active}")

                            Box(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .fillMaxWidth()
                                    .shadow(4.dp, MaterialTheme.shapes.extraLarge)
                                    .background(primary, MaterialTheme.shapes.extraLarge)
                                    .clickable {
                                        Log.e(tag, "Clicked on task id=${task.id}")
                                        navController.navigate("view_task/${task.id}")
                                    }
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(27.dp)
                                            .background(lightGray, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "${index + 1}", fontSize = 14.sp, color = buttonTextColor)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Task name:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = titleColor
                                    )
                                    Text(
                                        text = task.title,
                                        fontWeight = FontWeight.Light,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(0.8f)
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))

                                    when {
                                        task.status in listOf(
                                            TaskStatus.COMPLETED.toString(),
                                            TaskStatus.RATED.toString()
                                        ) -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.task_done),
                                                contentDescription = "Task Completed",
                                                modifier = Modifier.size(44.dp)
                                            )
                                        }
                                        task.active -> {
                                            Image(
                                                painter = painterResource(id = R.drawable.task_running),
                                                contentDescription = "Task Running",
                                                modifier = Modifier.size(44.dp)
                                            )
                                        }
                                        canStart -> {
                                            val subTasks = taskViewModel.getSubtasksByTaskId(task.id)
                                            Log.e(tag, "Subtasks for task ${task.id}: $subTasks")
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .shadow(4.dp, CircleShape)
                                                    .clickable {
                                                        Log.e(tag, "Starting task id=${task.id}")
                                                        coroutineScope.launch {
                                                            taskViewModel.startTaskEmployee(task.id)
                                                            subTasks.firstOrNull()?.let { st ->
                                                                Log.e(tag, "Starting first subtask id=${st.id}")
                                                                subTaskViewModel.updateSubtaskStatus(
                                                                    task.id,
                                                                    st.id,
                                                                    SubtaskStatus.RUNNING.toString()
                                                                )
                                                            }
                                                            currentUid()?.let { uid ->
                                                                taskViewModel.loadTodayTasksEmpolyee(uid)
                                                            }
                                                        }
                                                    }
                                                    .background(tertiary, CircleShape)
                                                    .padding(8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.RocketLaunch,
                                                    contentDescription = "Start",
                                                    tint = buttonTextColor
                                                )
                                            }
                                        }
                                        else -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .shadow(4.dp, CircleShape)
                                                    .background(tertiary.copy(alpha = 0.4f), CircleShape)
                                                    .padding(8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.RocketLaunch,
                                                    contentDescription = "Start disabled",
                                                    tint = buttonTextColor
                                                )
                                            }
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
}
