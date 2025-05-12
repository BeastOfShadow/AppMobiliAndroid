package it.uniupo.ktt.ui.components.task.newtask

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.CustomTextField
import it.uniupo.ktt.ui.theme.lightGray
import it.uniupo.ktt.ui.theme.tertiary

@Composable
fun SubtaskImage(
    title: String,
    initialDescription: String,
    initialImageUri: Uri?,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: (description: String, imageUri: Uri?) -> Unit
) {
    val subtaskDescription = remember { mutableStateOf(initialDescription) }
    val selectedImageUri = remember { mutableStateOf(initialImageUri) }
    val visibleImage = remember { mutableStateOf(selectedImageUri.value != null) }
    var showDescriptionError by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri.value = it
            visibleImage.value = false
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
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
                        text = title,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF403E3E),
                        ),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    CustomTextField(
                        label = "Subtask Description:",
                        textfieldValue = subtaskDescription.value,
                        onValueChange = { subtaskDescription.value = it }
                    )

                    Spacer(modifier = Modifier.size(20.dp))

                    Text(
                        text = "Photo:",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight(500),
                            color = Color(0xFF403E3E),
                        ),
                        modifier = Modifier.padding(start = 20.dp)
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // SEE IMAGE
                        if(selectedImageUri.value != null) {
                            Box(
                                modifier = Modifier
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
                                        visibleImage.value = !visibleImage.value
                                    }
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        if(visibleImage.value) "Hide" else "See",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = lightGray
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

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
                                            contentDescription = "Extend",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // ADD IMAGE
                        if(selectedImageUri.value == null) {
                            Box(
                                modifier = Modifier
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
                                        launcher.launch("image/*")
                                    }
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Add",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = lightGray
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

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
                                            painter = painterResource(id = R.drawable.image_upload),
                                            contentDescription = "Extend",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // REMOVE IMAGE
                        if(selectedImageUri.value != null) {
                            Box(
                                modifier = Modifier
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
                                        selectedImageUri.value = null
                                    }
                                    .padding(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .shadow(
                                            4.dp,
                                            shape = MaterialTheme.shapes.extraLarge,
                                            clip = false
                                        )
                                        .background(
                                            color = tertiary,
                                            shape = MaterialTheme.shapes.extraLarge
                                        )
                                        .padding(6.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.trashcan),
                                        contentDescription = "Extend",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }

                    // IMAGE PREVIEW
                    if(visibleImage.value) {
                        Spacer(modifier = Modifier.size(20.dp))
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            selectedImageUri.value?.let { uri ->
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Anteprima immagine",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        }
                    }

                    if (showDescriptionError) {
                        Spacer(modifier = Modifier.size(20.dp))

                        Text(
                            text = "La descrizione è obbligatoria",
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 20.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(50.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
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
                                    onDismiss()
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
                                    if (subtaskDescription.value.isBlank()) {
                                        showDescriptionError = true
                                        return@clickable
                                    }

                                    onSave(subtaskDescription.value, selectedImageUri.value)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Save",
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