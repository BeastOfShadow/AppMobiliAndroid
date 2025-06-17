package it.uniupo.ktt.ui.pages

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
import androidx.compose.runtime.collectAsState
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
import it.uniupo.ktt.time.formatSecondsToHHMM
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
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.secondary
import it.uniupo.ktt.ui.theme.subtitleColor
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.HomeScreenViewModel
import it.uniupo.ktt.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun TaskManagerScreen(navController: NavController, homeVm: HomeScreenViewModel) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("task manager") { inclusive = true }
            launchSingleTop = true
        }
    }

    val taskViewModel : TaskViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val uid = currentUid()
        if (uid != null) {
            homeVm.observeUserTasks(uid)  // deve aggiornare userTasksList correttamente!
        }
    }

    val tasks by homeVm.userTasksList.collectAsState()

    val readyTasks = tasks.filter { it.status == TaskStatus.READY.toString() && isToday(it.createdAt) }
    val ongoingTasks = tasks.filter { it.status == TaskStatus.ONGOING.toString() && isToday(it.createdAt) }
    val completedTasks = tasks.filter { it.status == TaskStatus.COMPLETED.toString() && isToday(it.createdAt) }
    val ratedTasks = tasks.filter { it.status == TaskStatus.RATED.toString() && isToday(it.createdAt) }

    // Lista filtri basata su liste non vuote
    val nonEmptyFilters = listOf(
        "Ready" to readyTasks.isNotEmpty(),
        "Ongoing" to ongoingTasks.isNotEmpty(),
        "Completed" to completedTasks.isNotEmpty(),
        "Rated" to ratedTasks.isNotEmpty()
    ).filter { it.second }.map { it.first }

    val filters = if (nonEmptyFilters.size >= 2) {
        listOf("All") + nonEmptyFilters
    } else {
        nonEmptyFilters
    }

// Stato di selectedFilter inizializzato e aggiornato al cambio di filters
    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(filters) {
        selectedFilter = if (filters.size == 1) filters.first() else "All"
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
                title = "Task Manager"
            )

            Spacer(modifier = Modifier.size(20.dp))

            if (filters.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No tasks available. Add a task to get started!",
                        color = Color.DarkGray,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            navController.navigate("new task")
                                  },
                        colors = ButtonDefaults.buttonColors(containerColor = primary),
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.shadow(4.dp, MaterialTheme.shapes.extraLarge)
                    ) {
                        Text(
                            text = "Add Task",
                            fontFamily = FontFamily(Font(R.font.poppins_regular)),
                            fontSize = 18.sp,
                            fontWeight = FontWeight(200),
                            color = Color.Black
                        )
                    }
                }
            }

            if(filters.isNotEmpty()) {
                ChipsFilter(
                    filters = filters,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )

                Spacer(modifier = Modifier.size(30.dp))

                if (selectedFilter == "All" || selectedFilter == "Ready") {
                    if (readyTasks.isNotEmpty()) {
                        TextSection("Ready")

                        Spacer(modifier = Modifier.size(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            readyTasks.forEach { task ->
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
                                        .background(
                                            primary,
                                            shape = MaterialTheme.shapes.extraLarge
                                        )
                                        .clickable{
                                            navController.navigate("update_ready_task/${task.id}")
                                        }
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = task.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = titleColor,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = getEmployeeName(task.employee),
                                            fontWeight = FontWeight.Light,
                                            fontSize = 14.sp,
                                            color = subtitleColor,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.size(10.dp))

                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .shadow(4.dp, shape = CircleShape, clip = false)
                                                .clickable {
                                                    coroutineScope.launch {
                                                        taskViewModel.startTask(task.id)
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
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(30.dp))
                }

                if (selectedFilter == "All" || selectedFilter == "Ongoing") {
                    if (ongoingTasks.isNotEmpty()) {
                        TextSection("Ongoing")

                        Spacer(modifier = Modifier.size(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ongoingTasks.forEach { task ->
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
                                        .background(
                                            primary,
                                            shape = MaterialTheme.shapes.extraLarge
                                        )
                                        .clickable{
                                            navController.navigate("update_ongoing_task/${task.id}")
                                        }
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = task.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = titleColor,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = getEmployeeName(task.employee),
                                            fontWeight = FontWeight.Light,
                                            fontSize = 14.sp,
                                            color = subtitleColor,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.size(15.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.task_sun),
                                                    contentDescription = "Clock",
                                                    modifier = Modifier.size(55.dp)
                                                )
                                            }

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .border(
                                                            width = 3.dp,
                                                            color = if (task.active) Color(
                                                                0xFF38D236
                                                            ) else Color(0xFFEED547),
                                                            shape = CircleShape
                                                        )
                                                        .padding(6.dp)
                                                        .height(38.dp)
                                                        .width(38.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    ElapsedTimeDisplay(
                                                        task = task
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(30.dp))
                }

                if (selectedFilter == "All" || selectedFilter == "Completed") {
                    if (completedTasks.isNotEmpty()) {
                        TextSection("Completed")

                        Spacer(modifier = Modifier.size(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            completedTasks.forEach { task ->
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
                                        .background(
                                            primary,
                                            shape = MaterialTheme.shapes.extraLarge
                                        )
                                        .padding(16.dp)
                                        .clickable(
                                            onClick = {
                                                navController.navigate("task_rating/${task.id}")

                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = task.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = titleColor,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = getEmployeeName(task.employee),
                                            fontWeight = FontWeight.Light,
                                            fontSize = 14.sp,
                                            color = subtitleColor,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.size(15.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.task_finished),
                                                    contentDescription = "Endline",
                                                    modifier = Modifier.size(50.dp)
                                                )
                                            }

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .border(
                                                            width = 3.dp,
                                                            color = Color(0xFFEA4242),
                                                            shape = CircleShape
                                                        )
                                                        .padding(6.dp)
                                                        .height(38.dp)
                                                        .width(38.dp)
                                                ) {
                                                    Text(
                                                        text = formatSecondsToHHMM(task.completionTimeActual),
                                                        fontSize = 12.sp,
                                                        color = subtitleColor,
                                                        modifier = Modifier.align(Alignment.Center)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }

                if (selectedFilter == "All" || selectedFilter == "Rated") {
                    if (ratedTasks.isNotEmpty()) {
                        TextSection("Rated")

                        Spacer(modifier = Modifier.size(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ratedTasks.forEach { task ->
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
                                        .background(
                                            primary,
                                            shape = MaterialTheme.shapes.extraLarge
                                        )
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = task.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = titleColor,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = getEmployeeName(task.employee),
                                            fontWeight = FontWeight.Light,
                                            fontSize = 14.sp,
                                            color = subtitleColor,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.size(15.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .shadow(4.dp, shape = CircleShape, clip = false)
                                                    .clickable {
                                                        navController.navigate("rated_task/${task.id}")
                                                    }
                                                    .background(
                                                        color = tertiary,
                                                        shape = CircleShape
                                                    )
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.RateReview,
                                                    contentDescription = "Rate Review",
                                                    tint = buttonTextColor,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }

        SmallFloatingActionButton(
            onClick = { navController.navigate("new task") },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = tertiary
        ) {
            Icon(
                Icons.Filled.Add,
                "Large floating action button",
                tint = buttonTextColor
            )

        }
    }
}


/*@Preview
@Composable
fun TaskManagerScreenPreview() {
    TaskManagerScreen(navController = NavController(context = LocalContext.current))
}*/
