package it.uniupo.ktt.ui.pages.employee.currentTask

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Expand
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
import it.uniupo.ktt.ui.model.SubTask
import it.uniupo.ktt.ui.model.Task
import it.uniupo.ktt.ui.taskstatus.TaskStatus
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.lightGray
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.subtitleColor
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.SubTaskViewModel
import it.uniupo.ktt.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun SubTaskViewScreen(navController: NavController, taskId: String, subtaskId: String) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("subtask_view") { inclusive = true }
            launchSingleTop = true
        }
    }

    val viewModel: SubTaskViewModel = viewModel()
    val taskViewModel: TaskViewModel = viewModel()
    var subTask by remember { mutableStateOf<SubTask?>(null) }

    var visibleImage by remember { mutableStateOf(false) }
    var visibleEmployeeImage by remember { mutableStateOf(false) }
    var visibleCaregiverImage by remember { mutableStateOf(false) }

    LaunchedEffect(subtaskId) {
        subTask = viewModel.getSubtaskById(taskId, subtaskId)
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
                title = "SubTask View"
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                            text = subTask?.listNumber.toString(),
                            fontSize = 14.sp,
                            color = buttonTextColor,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    subTask?.let {
                        Text(
                            text = "SubTask description:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = titleColor,
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    4.dp,
                                    shape = MaterialTheme.shapes.extraLarge,
                                    clip = false
                                )
                                .background(Color.White, shape = MaterialTheme.shapes.extraLarge)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = it.description,
                                fontWeight = FontWeight.Light,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            )
                        }

                        var subTaskImgUrl by remember { mutableStateOf("") }
                        var isLoading by remember { mutableStateOf(true) }

                        LaunchedEffect(subTask) {
                            isLoading = true

                            taskViewModel.getSubtaskImageUrl(
                                subTask!!,
                                onSuccess = { imageUrl ->
                                    subTaskImgUrl = imageUrl
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

                        when {
                            isLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            subTaskImgUrl.isNotEmpty() -> {
                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
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
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
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
                                                contentDescription = "Toggle Image Visibility",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }

                                if (visibleImage) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(subTaskImgUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Task Completed",
                                        modifier = Modifier
                                            .size(200.dp)
                                            .padding(top = 8.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }


                    if(subTask?.employeeComment != "")
                    {
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Your comment:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = titleColor,
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    4.dp,
                                    shape = MaterialTheme.shapes.extraLarge,
                                    clip = false
                                )
                                .background(Color.White, shape = MaterialTheme.shapes.extraLarge)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            subTask?.let {
                                Text(
                                    text = it.employeeComment,
                                    fontWeight = FontWeight.Light,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth(0.8f)
                                )
                            }
                        }
                    }

                    var subTaskEmployeeImgUrl by remember { mutableStateOf("") }
                    var isLoadingEmployee by remember { mutableStateOf(true) }

                    LaunchedEffect(subTask) {
                        isLoadingEmployee = true

                        taskViewModel.getEmployeeImageUrl(
                            subTask!!,
                            onSuccess = { imageUrl ->
                                subTaskEmployeeImgUrl = imageUrl
                                isLoadingEmployee = false
                                Log.d("Image URL", imageUrl)
                            },
                            onError = { exception ->
                                Log.e(
                                    "Image Error",
                                    "Errore nel caricamento immagine",
                                    exception
                                )
                                isLoadingEmployee = false
                            }
                        )
                    }

                    when {
                        isLoadingEmployee -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        subTaskEmployeeImgUrl.isNotEmpty() -> {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Your Comment Image:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = titleColor,
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
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
                                        visibleEmployeeImage = !visibleEmployeeImage
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
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
                                            contentDescription = "Toggle Image Visibility",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            if (visibleEmployeeImage) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(subTaskEmployeeImgUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Task Completed",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .padding(top = 8.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                        if(subTask?.caregiverComment != "")
                        {
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Your comment:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = titleColor,
                            )

                            Spacer(modifier = Modifier.height(15.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        4.dp,
                                        shape = MaterialTheme.shapes.extraLarge,
                                        clip = false
                                    )
                                    .background(Color.White, shape = MaterialTheme.shapes.extraLarge)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                subTask?.let {
                                    Text(
                                        text = it.caregiverComment,
                                        fontWeight = FontWeight.Light,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.fillMaxWidth(0.8f)
                                    )
                                }
                            }
                        }

                        var subTaskCareGiverImgUrl by remember { mutableStateOf("") }
                        var isLoadingCareGiver by remember { mutableStateOf(true) }

                        LaunchedEffect(subTask) {
                            isLoadingCareGiver = true

                            taskViewModel.getCaregiverImageUrl(
                                subTask!!,
                                onSuccess = { imageUrl ->
                                    subTaskCareGiverImgUrl = imageUrl
                                    isLoadingCareGiver = false
                                    Log.d("Image URL", imageUrl)
                                },
                                onError = { exception ->
                                    Log.e(
                                        "Image Error",
                                        "Errore nel caricamento immagine",
                                        exception
                                    )
                                    isLoadingCareGiver = false
                                }
                            )
                        }

                        when {
                            isLoadingCareGiver -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            subTaskCareGiverImgUrl.isNotEmpty() -> {
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Caregiver Comment Image:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = titleColor,
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
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
                                            visibleCaregiverImage = !visibleCaregiverImage
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
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
                                                contentDescription = "Toggle Image Visibility",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }

                                if (visibleCaregiverImage) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(subTaskCareGiverImgUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Task Completed",
                                        modifier = Modifier
                                            .size(200.dp)
                                            .padding(top = 8.dp),
                                        contentScale = ContentScale.Crop
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
                                    navController.popBackStack()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Return",
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


@Preview
@Composable
fun SubTaskViewScreenPreview() {
    SubTaskViewScreen(navController = NavController(context = LocalContext.current), "cdsshhf", "dscjfdsljl")
}
