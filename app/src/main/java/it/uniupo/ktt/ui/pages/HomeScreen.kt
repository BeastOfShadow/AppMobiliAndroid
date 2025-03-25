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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.ui.components.MenuLabel
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.subtitleColor
import it.uniupo.ktt.ui.theme.titleColor

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
           }

           Spacer(modifier = Modifier.height(80.dp))

           Box(
               modifier = Modifier
                   .size(110.dp)
                   .graphicsLayer {
                       shadowElevation = 4.dp.toPx() // Altezza dell'ombra
                       shape = CircleShape
                       clip = false
                       alpha = 1f
                   }
                   .background(primary, CircleShape)
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
               style = MaterialTheme.typography, // This will use Poppins

               color = titleColor,
               modifier = Modifier.align(Alignment.CenterHorizontally)
           )

           // Solicitous
           Text(
               text = "Status",
               fontWeight = FontWeight.ExtraLight,
               fontSize = 16.sp,
               color = subtitleColor, // Color secondary
               modifier = Modifier.align(Alignment.CenterHorizontally)
           )

           Spacer(modifier = Modifier.height(65.dp))

           Text(
               text = "Main menu",
               fontWeight = FontWeight.Normal,
               fontSize = 16.sp,
               color = subtitleColor
           )

            MenuLabel(
                navController = navController,
                navPage = "task manager",
                title = "Task Manager",
                description = "Create, update, delete tasks and subtasks",
                icon = Icons.Outlined.BorderColor,
                iconDescription = "Profile Icon"
            )

            Spacer(modifier = Modifier.height(10.dp))

            MenuLabel(
                navController = navController,
                navPage = "chat",
                title = "Chat",
                description = "Direct messages to your employees",
                icon = Icons.AutoMirrored.Outlined.Chat,
                iconDescription = "Chat Icon"
            )

            Spacer(modifier = Modifier.height(10.dp))

           MenuLabel(
               navController = navController,
               navPage = "CareGiver Statistic",
               title = "Statistics",
               description = "Check the work done by each employee",
               icon = Icons.AutoMirrored.Outlined.ShowChart,
               iconDescription = "Statistics Icon"
           )

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
