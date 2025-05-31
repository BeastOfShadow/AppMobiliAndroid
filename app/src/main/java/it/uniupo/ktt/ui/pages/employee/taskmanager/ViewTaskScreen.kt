package it.uniupo.ktt.ui.pages.employee.taskmanager

import android.text.format.DateUtils.formatElapsedTime
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Expand
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
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
import com.google.maps.android.compose.Marker // Ensure this import is present
import it.uniupo.ktt.ui.subtaskstatus.SubtaskStatus

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
    var isLocationSaved by remember { mutableStateOf(false) }
    var savedLocation by remember { mutableStateOf<LatLng?>(null) }
    var showMapDialog by remember { mutableStateOf(false) }
    var selectedLatLng by remember { mutableStateOf(LatLng(45.0703, 7.6869)) } // Torino come default

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
                            if(task.active)
                            {
                                if(task.status == TaskStatus.ONGOING.toString())
                                    CircularTimer(task)
                                else
                                {
                                    val startMillis = task.timeStampStart.toDate().time
                                    val estimatedDurationMillis = (task.completionTimeEstimate * 1000L).coerceAtLeast(1000L)
                                    val actualDurationMillis = (task.completionTimeActual * 1000L).coerceAtLeast(0L)

                                    val progress = (actualDurationMillis.toFloat() / estimatedDurationMillis).coerceIn(0f, 1f)

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier.fillMaxSize(),
                                            color = tertiary,
                                            strokeWidth = 6.dp,
                                            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                                        )
                                        // Qui mostri il tempo effettivo in secondi o come preferisci
                                        Text(
                                            text = formatElapsedTime(task.completionTimeActual.toLong()),
                                            fontSize = 14.sp,
                                            color = subtitleColor,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                }
                            }
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
                val defaultLocation = com.google.firebase.firestore.GeoPoint(0.0, 0.0)
                // Secondo box
                if (task != null) {

                    if (task.location != defaultLocation || savedLocation != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false)
                                .background(primary, shape = MaterialTheme.shapes.extraLarge),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val currentTaskLocationLatLng = savedLocation ?: LatLng(
                                    task.location.latitude,
                                    task.location.longitude
                                )

                                val cameraPositionState = rememberCameraPositionState {
                                    position = CameraPosition.fromLatLngZoom(
                                        currentTaskLocationLatLng,
                                        10f
                                    )
                                }

                                GoogleMap(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .clip(MaterialTheme.shapes.extraLarge), // Occupa lo spazio rimanente nella Column
                                    cameraPositionState = cameraPositionState,
                                    uiSettings = MapUiSettings(
                                        zoomControlsEnabled = false,     // IMPOSTA A FALSE: Nasconde i controlli +/- dello zoom.
                                        scrollGesturesEnabled = true,    // IMPOSTA A TRUE: Permette di spostare la mappa (pan) con il dito.
                                        zoomGesturesEnabled = true,      // IMPOSTA A TRUE: Permette di ingrandire/rimpicciolire con i gesti (es. pinch-to-zoom).
                                        rotationGesturesEnabled = false, // Opzionale: solitamente per mappe piccole/integrate si disabilita la rotazione per semplicità.
                                        tiltGesturesEnabled = false,     // Opzionale: solitamente per mappe piccole/integrate si disabilita l'inclinazione.
                                        mapToolbarEnabled = false        // MANTIENI A FALSE: Nasconde la toolbar che appare cliccando un marker (con i link a Google Maps, ecc.).
                                    )
                                ) {
                                    Marker(
                                        state = MarkerState(position = currentTaskLocationLatLng),
                                        title = task.title.takeIf { it.isNotBlank() }
                                            ?: "Posizione del Task", // Titolo del marker
                                        // snippet = "Dettagli aggiuntivi qui" // Eventuale snippet
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false)
                                .background(primary, shape = MaterialTheme.shapes.extraLarge)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
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
                                            .shadow(
                                                4.dp,
                                                shape = MaterialTheme.shapes.large,
                                                clip = false
                                            )
                                            .background(
                                                tertiary,
                                                shape = MaterialTheme.shapes.large
                                            )
                                            .clickable {
                                                showMapDialog = true
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
                                            .fillMaxWidth()
                                            .height(34.dp),  // altezza uguale all'immagine per allineamento
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if(subtask.status == SubtaskStatus.RUNNING.toString()) {
                                            Image(
                                                painter = painterResource(id = R.drawable.task_running),
                                                contentDescription = "SubTask Running",
                                                modifier = Modifier.size(34.dp)
                                            )
                                        } else if(subtask.status == SubtaskStatus.COMPLETED.toString()) {
                                            Image(
                                                painter = painterResource(id = R.drawable.task_done),
                                                contentDescription = "SubTask Done",
                                                modifier = Modifier.size(34.dp)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(27.dp)
                                                .background(
                                                    color = lightGray,
                                                    shape = CircleShape
                                                )
                                                .align(Alignment.CenterEnd)  // numero a destra, centrato verticalmente
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 14.sp,
                                                color = buttonTextColor,
                                                modifier = Modifier.align(Alignment.Center)
                                            )
                                        }
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

                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {

                                            val employeeControl = subtask.employeeComment != "" || subtask.caregiverComment != ""
                                            val caregiverControl = subtask.employeeImgStorageLocation != "" || subtask.caregiverImgStorageLocation != ""
                                            
                                            if(employeeControl || caregiverControl)
                                            {
                                                Box(
                                                    modifier = Modifier
                                                        .shadow(
                                                            4.dp,
                                                            shape = MaterialTheme.shapes.extraLarge,
                                                            clip = false
                                                        )
                                                        .background(
                                                            color = tertiary,
                                                            shape = CircleShape
                                                        )
                                                        .padding(8.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Outlined.Chat,
                                                            contentDescription = "Review Icon",
                                                            tint = Color.White,
                                                            modifier = Modifier.padding(horizontal = 4.dp)
                                                        )
                                                        Icon(
                                                            imageVector = Icons.Outlined.Check,
                                                            contentDescription = "Review Icon",
                                                            tint = Color.White,
                                                            modifier = Modifier.padding(horizontal = 4.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            Icon(
                                                modifier = Modifier
                                                    .align(Alignment.CenterEnd) // ← Icona spostata a destra
                                                    .clickable {
                                                        navController.navigate("subtask_view/${taskId}/${subtask.id}")
                                                    },
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
    }

    if (showMapDialog) {
        AlertDialog(
            onDismissRequest = { showMapDialog = false },
            title = {
                Text(
                        text="Select your position",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF403E3E)
                        )
                )
                    },
            text = {
                Box(
                    modifier = Modifier.height(300.dp)
                    .background(Color(0xFFC5B5D8), shape = RoundedCornerShape(16.dp))
                    .padding(24.dp)
                    .fillMaxWidth(),
                    ) {
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(selectedLatLng, 10f)
                        }

                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            onMapClick = { latLng ->
                            selectedLatLng = latLng
                        }
                        ) {
                            Marker(
                            state = MarkerState(position = selectedLatLng)
                            )
                        }
                    }
                   },

            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Bottone Annulla (Dismiss)
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
                                showMapDialog = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Cancel",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Bottone Conferma
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
                                if (task != null) {
                                    viewModel.updateLocation(task.id, selectedLatLng)
                                }
                                savedLocation = LatLng(selectedLatLng.latitude, selectedLatLng.longitude)
                                showMapDialog = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Confirm",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            dismissButton = null
        )
    }
}


@Preview
@Composable
fun ViewTaskScreenPreview() {
    ViewTaskScreen(navController = NavController(context = LocalContext.current), "dscjfdsljl")
}
