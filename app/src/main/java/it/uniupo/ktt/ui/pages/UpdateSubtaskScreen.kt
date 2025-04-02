package it.uniupo.ktt.ui.pages

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import it.uniupo.ktt.ui.theme.lightGray
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.tertiary
import it.uniupo.ktt.ui.theme.titleColor

@Composable
fun UpdateSubtaskScreen(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("task manager") { inclusive = true }
            launchSingleTop = true
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
                title = "Update Subtask"
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
                            text = "1",
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
                            Text(
                                text = "Prova con un testo verosimile molto lungo per vedere se effettivamente funziona todos",
                                fontSize = 15.sp,
                                maxLines = 6,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(10.dp))

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
                        Box(
                            modifier = Modifier
                                .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false)
                                .background(color = Color.White, shape = MaterialTheme.shapes.extraLarge)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "See",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = lightGray
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .shadow(4.dp, shape = CircleShape, clip = false)
                                        .background(color = tertiary, shape = CircleShape)
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

                        Box(
                            modifier = Modifier
                                .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge, clip = false)
                                .background(color = Color.White, shape = MaterialTheme.shapes.extraLarge)
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

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .shadow(4.dp, shape = CircleShape, clip = false)
                                        .background(color = tertiary, shape = CircleShape)
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

                        Box(
                            modifier = Modifier
                                .shadow(
                                    4.dp,
                                    shape = MaterialTheme.shapes.extraLarge,
                                    clip = false
                                )
                                .background(color = Color.White, shape = MaterialTheme.shapes.extraLarge)
                                .padding(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .shadow(
                                        4.dp,
                                        shape = MaterialTheme.shapes.extraLarge,
                                        clip = false
                                    )
                                    .background(color = tertiary, shape = MaterialTheme.shapes.extraLarge)
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
                                .clickable { /* Cancella */ },
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
                                .clickable { /* Crea task */ },
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
fun UpdateSubtaskScreen() {
    UpdateSubtaskScreen(navController = NavController(context = LocalContext.current))
}
