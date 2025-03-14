package it.uniupo.ktt.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewTaskScreen(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("new task") { inclusive = true }
            launchSingleTop = true
        }
    }

    var taskName by remember { mutableStateOf("") }
    var employee by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    var subtasks by remember { mutableStateOf(listOf("Subtask 1", "Subtask 2", "Subtask 3")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            FilledIconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.size(34.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier.size(18.dp)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .padding(end = 34.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(vertical = 8.dp),
            ) {
                Text(
                    text = "New Task",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.size(30.dp))

        OutlinedTextField(
            value = taskName,
            onValueChange = { newText -> taskName = newText },
            label = { Text("Task Name") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(15.dp))

        OutlinedTextField(
            value = employee,
            onValueChange = { newText -> employee = newText },
            label = { Text("Employee") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.padding(top = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Share position: ",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                modifier = Modifier.scale(0.7f)
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        Text(
            text = "Subtask List:"
        )

        Column {
            for (i in subtasks.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Primo box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Subtask ${i + 1}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Filled.Image,
                                    contentDescription = "Subtask Image",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = subtasks[i],
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Secondo box (se esiste)
                    if (i + 1 < subtasks.size) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Subtask ${i + 2}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Image,
                                        contentDescription = "Subtask Image",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = subtasks[i + 1],
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f)) // Placeholder for odd number of items
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f) // Rende la box quadrata
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                        .clickable {
                            subtasks = subtasks + "New Subtask"
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Subtask",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview
@Composable
fun NewTaskScreenPreview() {
    NewTaskScreen(navController = NavController(context = LocalContext.current))
}