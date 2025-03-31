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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Blender
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.secondary
import it.uniupo.ktt.ui.theme.subtitleColor
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskManagerScreen(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("task manager") { inclusive = true }
            launchSingleTop = true
        }
    }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Ready", "Ongoing", "Completed")
    val events = listOf(
        "Evento prova del testo davvero molto lungo, ma davvero tanto" to "Anche il nome e cognome non sono da meno",
        "Evento 2" to "Luca Bianchi",
        "Evento 3" to "Giulia Verdi",
        "Evento 4" to "Anna Neri",
        "Evento 5" to "Marco Gialli",
        "Evento 6" to "Sara Blu",
        "Evento 7" to "Giorgio Arancio"
    )

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                filters.forEach { filter ->
                    Box(
                        modifier = Modifier
                            .border(width = 1.dp, color = Color(0xFF403E3E), shape = MaterialTheme.shapes.extraLarge)
                            .clickable { selectedFilter = filter },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(if (selectedFilter == filter) Color(0xFF6200EE) else Color.White, shape = MaterialTheme.shapes.extraLarge)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filter,
                                fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp,
                                color = if (selectedFilter == filter) Color.White else subtitleColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Filter: $selectedFilter",
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "There are no ...",
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.size(30.dp))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Ready",
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.size(10.dp))

            if(events.isEmpty())
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "There are no ready events.",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                )
            else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    events.forEach { (eventTitle, personName) ->
                        Box(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .width(180.dp)
                                .height(180.dp)
                                .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false)
                                .background(primary, shape = MaterialTheme.shapes.extraLarge)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = eventTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = titleColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = personName,
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
                                        .background(
                                            color = tertiary,
                                            shape = CircleShape
                                        )
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch, // Sostituisci con l'icona desiderata
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

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Ongoing",
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.size(10.dp))

            if(events.isEmpty())
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "There are no ongoing events.",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                )
            else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    events.forEach { (eventTitle, personName) ->
                        Box(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .width(180.dp)
                                .height(180.dp)
                                .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false)
                                .background(primary, shape = MaterialTheme.shapes.extraLarge)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = eventTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = titleColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = personName,
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
                                                    color = Color(0xFFEED547),
                                                    shape = CircleShape
                                                )
                                                .padding(6.dp)
                                                .height(38.dp)
                                                .width(38.dp)
                                        ) {
                                            Text(
                                                text = "12:47",
                                                fontSize = 14.sp,
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

            Spacer(modifier = Modifier.size(30.dp))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Completed",
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.size(10.dp))

            if(events.isEmpty())
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "There are no ready events.",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                )
            else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    events.forEach { (eventTitle, personName) ->
                        Box(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .width(180.dp)
                                .height(180.dp)
                                .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false)
                                .background(primary, shape = MaterialTheme.shapes.extraLarge)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = eventTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = titleColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = personName,
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
                                                    color = Color(0xFFEED547),
                                                    shape = CircleShape
                                                )
                                                .padding(6.dp)
                                                .height(38.dp)
                                                .width(38.dp)
                                        ) {
                                            Text(
                                                text = "12:47",
                                                fontSize = 14.sp,
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


@Preview
@Composable
fun TaskManagerScreenPreview() {
    TaskManagerScreen(navController = NavController(context = LocalContext.current))
}
