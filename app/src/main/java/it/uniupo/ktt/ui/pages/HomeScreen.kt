package it.uniupo.ktt.ui.pages

import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("home") { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilledIconButton(
                    onClick = {
                        // navController.popBackStack()
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("landing") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
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
                       text = "Home",
                       fontWeight = FontWeight.SemiBold,
                       fontSize = 20.sp,
                       color = MaterialTheme.colorScheme.onPrimaryContainer,
                       modifier = Modifier.align(Alignment.Center)
                   )
               }
           }

           Spacer(modifier = Modifier.height(50.dp))

           Box(
               modifier = Modifier
                   .size(110.dp)
                   .background(
                       MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                       shape = CircleShape
                   )
                   .padding(16.dp)
                   .align(Alignment.CenterHorizontally),
               contentAlignment = Alignment.Center
           ) {
               Icon(
                   imageVector = Icons.Outlined.AccountCircle,
                   contentDescription = "Profile Icon",
                   modifier = Modifier.size(80.dp),
                   tint = MaterialTheme.colorScheme.primary
               )
           }

           Spacer(modifier = Modifier.height(20.dp))

           // Title
           Text(
               text = "Franca Bruni",
               fontWeight = FontWeight.SemiBold,
               fontSize = 20.sp,
               color = MaterialTheme.colorScheme.primary,
               modifier = Modifier.align(Alignment.CenterHorizontally)
           )

           // Solicitous
           Text(
               text = "Status",
               fontWeight = FontWeight.ExtraLight,
               fontSize = 16.sp,
               color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), // Color secondary
               modifier = Modifier.align(Alignment.CenterHorizontally)
           )

           Spacer(modifier = Modifier.height(65.dp))

           Text(
               text = "Main menu",
               fontWeight = FontWeight.Normal,
               fontSize = 16.sp,
               color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
           )

           Box(
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(top = 10.dp)
                   .clickable {navController.navigate("task manager")}
           ) {
               Box(
                   modifier = Modifier
                       .background(
                           color = MaterialTheme.colorScheme.primaryContainer,
                           shape = MaterialTheme.shapes.medium
                       )
                       .fillMaxWidth()
                       .padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
               ) {
                   Row(
                       modifier = Modifier.fillMaxWidth()
                   ) {
                       Box(
                           modifier = Modifier
                               .size(52.dp)
                               .background(
                                   MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                                   shape = CircleShape
                               )
                               .padding(8.dp),
                           contentAlignment = Alignment.Center
                       ) {
                           Icon(
                               imageVector = Icons.Outlined.BorderColor,
                               contentDescription = "Profile Icon",
                               modifier = Modifier.size(80.dp),
                               tint = MaterialTheme.colorScheme.primary
                           )
                       }
                       Column (
                           modifier = Modifier.padding(start = 10.dp)
                               .weight(1f)
                               .align(Alignment.CenterVertically)
                       ) {
                           Text(
                               text = "Task Manager",
                               fontWeight = FontWeight.SemiBold,
                               fontSize = 20.sp,
                               color = MaterialTheme.colorScheme.primary
                           )

                           Text(
                               text = "Create, update, delete tasks and subtasks",
                               fontWeight = FontWeight.Light,
                               fontSize = 12.sp,
                               color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                           )
                       }
                       Icon(
                           imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                           contentDescription = "Back",
                           modifier = Modifier.size(30.dp)
                               .align(Alignment.CenterVertically)
                               .padding(end = 10.dp)
                       )
                   }
               }
           }

           Box(
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(top = 10.dp)
                   .clickable {navController.navigate("chat")}
           ) {
               Box(
                   modifier = Modifier
                       .background(
                           color = MaterialTheme.colorScheme.primaryContainer,
                           shape = MaterialTheme.shapes.medium
                       )
                       .fillMaxWidth()
                       .padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
               ) {
                   Row(
                       modifier = Modifier.fillMaxWidth()
                   ) {
                       Box(
                           modifier = Modifier
                               .size(52.dp)
                               .background(
                                   MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                                   shape = CircleShape
                               )
                               .padding(8.dp),
                           contentAlignment = Alignment.Center
                       ) {
                           Icon(
                               imageVector = Icons.AutoMirrored.Outlined.Chat,
                               contentDescription = "Profile Icon",
                               modifier = Modifier.size(80.dp),
                               tint = MaterialTheme.colorScheme.primary
                           )
                       }
                       Column (
                           modifier = Modifier.padding(start = 10.dp)
                               .weight(1f)
                               .align(Alignment.CenterVertically)
                       ) {
                           Text(
                               text = "Chat",
                               fontWeight = FontWeight.SemiBold,
                               fontSize = 20.sp,
                               color = MaterialTheme.colorScheme.primary
                           )

                           Text(
                               text = "Direct messages to your employees",
                               fontWeight = FontWeight.Light,
                               fontSize = 12.sp,
                               color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                           )
                       }
                       Icon(
                           imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                           contentDescription = "Back",
                           modifier = Modifier.size(30.dp)
                               .align(Alignment.CenterVertically)
                               .padding(end = 10.dp)
                       )
                   }
               }
           }

           Box(
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(top = 10.dp)
                   .clickable {navController.navigate("statistics")}
           ) {
               Box(
                   modifier = Modifier
                       .background(
                           color = MaterialTheme.colorScheme.primaryContainer,
                           shape = MaterialTheme.shapes.medium
                       )
                       .fillMaxWidth()
                       .padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
               ) {
                   Row(
                       modifier = Modifier.fillMaxWidth()
                   ) {
                       Box(
                           modifier = Modifier
                               .size(52.dp)
                               .background(
                                   MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                                   shape = CircleShape
                               )
                               .padding(8.dp),
                           contentAlignment = Alignment.Center
                       ) {
                           Icon(
                               imageVector = Icons.AutoMirrored.Outlined.ShowChart,
                               contentDescription = "Profile Icon",
                               modifier = Modifier.size(80.dp),
                               tint = MaterialTheme.colorScheme.primary
                           )
                       }
                       Column (
                           modifier = Modifier.padding(start = 10.dp)
                               .weight(1f)
                               .align(Alignment.CenterVertically)
                       ) {
                           Text(
                               text = "Statistics",
                               fontWeight = FontWeight.SemiBold,
                               fontSize = 20.sp,
                               color = MaterialTheme.colorScheme.primary
                           )

                           Text(
                               text = "Check the work done by each employee",
                               fontWeight = FontWeight.Light,
                               fontSize = 12.sp,
                               color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                           )
                       }
                       Icon(
                           imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                           contentDescription = "Back",
                           modifier = Modifier.size(30.dp)
                               .align(Alignment.CenterVertically)
                               .padding(end = 10.dp)
                       )
                   }
               }
           }

           /*Button(
           onClick = {
               FirebaseAuth.getInstance().signOut()
               navController.navigate("landing") {
                   popUpTo("home") { inclusive = true }
                   launchSingleTop = true
               }
           },
           modifier = Modifier
               .fillMaxWidth()
               .padding(top = 10.dp)
       ) {
           Text(
               text = "Logout"
           )
       }*/
       }
   }
}

@Preview
@Composable
fun HomeScreenPreview() {
   HomeScreen(navController = NavController(context = LocalContext.current))
}
