package it.uniupo.ktt.ui.pages

import android.util.Log
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.model.SubTask
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.lightGray
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor
import it.uniupo.ktt.viewmodel.SubTaskViewModel
import it.uniupo.ktt.viewmodel.TaskViewModel
import kotlinx.coroutines.tasks.await

@Composable
fun CommentSubtaskScreen(navController: NavController, taskId: String, subtaskId: String) {
  if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
    navController.navigate("landing") {
      popUpTo("task manager") { inclusive = true }
      launchSingleTop = true
    }
  }

  val subTaskViewModel : SubTaskViewModel = viewModel()
  val taskViewModel : TaskViewModel = viewModel()
  var subTask by remember { mutableStateOf<SubTask?>(null) }

  val visibleImage = remember { mutableStateOf(false) }
  val removeImage = remember { mutableStateOf(false) }

  LaunchedEffect(subtaskId) {
    subTask = subTaskViewModel.getSubtaskById(taskId, subtaskId)
  }

  val careGiverComment = remember { mutableStateOf("") }

  LaunchedEffect(subTask) {
    subTask?.caregiverComment?.let {
      careGiverComment.value = it
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
        title = "Comment Subtask"
      )

      Spacer(modifier = Modifier.size(20.dp))

      Box(
        modifier = Modifier
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
          modifier = Modifier
            .fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(27.dp)
              .background(
                color = lightGray,
                shape = CircleShape
              )
          ) {
            Text(
              text = subTask?.listNumber.toString(),
              fontSize = 14.sp,
              color = buttonTextColor,
              modifier = Modifier.align(Alignment.Center)
            )
          }

          Spacer(modifier = Modifier.size(10.dp))

          Text(
            text = "Subtask Description:",
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            color = titleColor,
            textAlign = TextAlign.Start
          )

          Spacer(modifier = Modifier.size(10.dp))

          Box(
            modifier = Modifier
              .shadow(
                4.dp,
                shape = MaterialTheme.shapes.extraLarge,
                clip = false
              )
              .fillMaxWidth()
              .background(color = Color.White, shape = MaterialTheme.shapes.extraLarge)
              .padding(16.dp)
          ) {
            Column {
              subTask?.let {
                Text(
                  text = it.description,
                  fontSize = 15.sp,
                  maxLines = 6,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }

          Spacer(modifier = Modifier.size(10.dp))

          val firebaseImageUrl = remember { mutableStateOf<String?>(null) }

          if (!subTask?.descriptionImgStorageLocation.isNullOrBlank()) {
            val storageRef = FirebaseStorage.getInstance().reference
              .child(subTask?.descriptionImgStorageLocation ?: "")

            LaunchedEffect(subTask?.descriptionImgStorageLocation) {
              try {
                firebaseImageUrl.value = storageRef.downloadUrl.await().toString()
              } catch (e: Exception) {
                firebaseImageUrl.value = null
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            firebaseImageUrl.value?.let { imageUrl ->
              AsyncImage(
                model = imageUrl,
                contentDescription = "Anteprima immagine",
                modifier = Modifier
                  .fillMaxWidth()
                  .height(200.dp)
                  .clip(RoundedCornerShape(16.dp))
              )
            } ?: Text(
              "Caricamento immagine...",
              fontSize = 14.sp,
              color = lightGray
            )

            Spacer(modifier = Modifier.size(10.dp))
          }

          if(subTask?.employeeComment != "") {
            Text(
              text = "Employee Comment:",
              fontWeight = FontWeight.Medium,
              fontSize = 20.sp,
              color = titleColor,
              textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.size(10.dp))

            Box(
              modifier = Modifier
                .shadow(
                  4.dp,
                  shape = MaterialTheme.shapes.extraLarge,
                  clip = false
                )
                .fillMaxWidth()
                .background(
                  color = Color.White,
                  shape = MaterialTheme.shapes.extraLarge
                )
                .padding(16.dp)
            ) {
              Column {
                subTask?.let {
                  Text(
                    text = it.employeeComment,
                    fontSize = 15.sp,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }

            Spacer(modifier = Modifier.size(10.dp))
          }


          val firebaseImageUrlEmployee = remember { mutableStateOf<String?>(null) }

          if (!subTask?.descriptionImgStorageLocation.isNullOrBlank()) {
            val storageRef = FirebaseStorage.getInstance().reference
              .child(subTask?.descriptionImgStorageLocation ?: "")

            LaunchedEffect(subTask?.descriptionImgStorageLocation) {
              try {
                firebaseImageUrlEmployee.value = storageRef.downloadUrl.await().toString()
              } catch (e: Exception) {
                firebaseImageUrl.value = null
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            firebaseImageUrlEmployee.value?.let { imageUrl ->
              Text(
                text = "Employee Image Comment:",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = titleColor,
                textAlign = TextAlign.Start
              )

              Spacer(modifier = Modifier.height(10.dp))

              AsyncImage(
                model = imageUrl,
                contentDescription = "Anteprima immagine",
                modifier = Modifier
                  .fillMaxWidth()
                  .height(200.dp)
                  .clip(RoundedCornerShape(16.dp))
              )
            } ?: Text(
              "Caricamento immagine...",
              fontSize = 14.sp,
              color = lightGray
            )

            Spacer(modifier = Modifier.size(10.dp))
          }

          Text(
            text = "Your comment:",
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            color = titleColor,
            textAlign = TextAlign.Start
          )

          Spacer(modifier = Modifier.size(10.dp))

          TextField(
            value = careGiverComment.value,
            onValueChange = { careGiverComment.value = it },
            colors = TextFieldDefaults.colors(
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color.White,
              cursorColor = Color.Black,
              disabledLabelColor = Color.Red,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
              .fillMaxWidth()
              .shadow(
                4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false
              )
              .clip(CircleShape)
              .background(Color.White)
              .padding(start = 10.dp, end = 5.dp),
            trailingIcon = {
              if (careGiverComment.value.isNotEmpty()) {
                IconButton(onClick = { careGiverComment.value = "" }) {
                  Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Clear text"
                  )
                }
              }
            },
            shape = MaterialTheme.shapes.extraLarge,
            singleLine = true
          )

          Spacer(modifier = Modifier.size(10.dp))

          val remove = remember { mutableStateOf(false) }
          val visibleImage = remember { mutableStateOf(false) }
          val img = remember { mutableStateOf("") }
          val currentImg = remember { mutableStateOf("") }

          LaunchedEffect(subTask?.caregiverImgStorageLocation) {
            currentImg.value = subTask?.caregiverImgStorageLocation ?: ""
          }

          val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
              img.value = it.toString()
              remove.value = false
              visibleImage.value = false
            }
          }

          Text(
            text = "Photo:",
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            color = titleColor,
            textAlign = TextAlign.Start
          )

          Spacer(modifier = Modifier.size(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            // SEE
            if(currentImg.value != "" || img.value != "") {
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

            // ADD
            if (img.value == "" && currentImg.value == "" /*&& remove.value*/) {
              Box(
                modifier = Modifier
                  .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge)
                  .background(Color.White, shape = MaterialTheme.shapes.extraLarge)
                  .clickable { launcher.launch("image/*") }
                  .padding(horizontal = 12.dp, vertical = 8.dp)
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
                  Spacer(Modifier.width(8.dp))
                  Box(
                    modifier = Modifier
                      .shadow(4.dp, shape = CircleShape)
                      .background(tertiary, shape = CircleShape)
                      .size(32.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Image(
                      painter = painterResource(id = R.drawable.image_upload),
                      contentDescription = "Upload Image",
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }
              }
            }

            // REMOVE
            if ((img.value.isNotEmpty() || currentImg.value.isNotEmpty()) && !remove.value) {
              Box(
                modifier = Modifier
                  .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge)
                  .background(Color.White, shape = MaterialTheme.shapes.extraLarge)
                  .clickable {
                    img.value = ""
                    currentImg.value = ""
                    remove.value = true
                  }
                  .padding(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge)
                    .background(tertiary, shape = MaterialTheme.shapes.extraLarge)
                    .padding(6.dp)
                ) {
                  Image(
                    painter = painterResource(id = R.drawable.trashcan),
                    contentDescription = "Remove Image",
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
              if (img.value.isEmpty()) {
                var subTaskCareGiverImgUrl by remember { mutableStateOf<String?>(null) }
                var isLoading by remember { mutableStateOf(false) }

                LaunchedEffect(subTask) {
                  if (subTask != null) {
                    isLoading = true
                    taskViewModel.getCaregiverCommentImageUrl(
                      subTask!!,
                      onSuccess = { imageUrl ->
                        subTaskCareGiverImgUrl = imageUrl
                        isLoading = false
                        Log.d("Image URL", imageUrl)
                      },
                      onError = { exception ->
                        Log.e("Image Error", "Errore nel caricamento immagine", exception)
                        isLoading = false
                      }
                    )
                  }
                }

                if (isLoading) {
                  CircularProgressIndicator()
                } else {
                  subTaskCareGiverImgUrl?.let { imageUrl ->

                    AsyncImage(
                      model = imageUrl,
                      contentDescription = "Subtask Image",
                      modifier = Modifier
                        .size(280.dp)
                        .scale(0.8f)
                    )
                  } ?: Text("Nessuna immagine disponibile")
                }
              }
              else {
                AsyncImage(
                  model = img.value,
                  contentDescription = "Anteprima immagine",
                  modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                )
              }
            }
          }

          Spacer(modifier = Modifier.size(40.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            Box(
              modifier = Modifier
                .width(140.dp)
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
                "Cancel",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Box(
              modifier = Modifier
                .width(140.dp)
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
                  subTask?.let {
                    subTaskViewModel.commentSubtask(
                      taskId,
                      it.id,
                      careGiverComment.value,
                      img.value,
                      remove.value,
                      "caregiverComment",
                      onSuccess = {
                        navController.popBackStack()
                      },
                      onError = {
                        Log.e("ERROR", "Error commenting subtask")
                      },
                    )
                  }
                },
              contentAlignment = Alignment.Center
            ) {
              Text(
                "Update",
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
fun CommentSubtaskScreen() {
  CommentSubtaskScreen(navController = NavController(context = LocalContext.current), "disihh", "cv")
}
