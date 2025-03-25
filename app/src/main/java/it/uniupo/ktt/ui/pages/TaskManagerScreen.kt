package it.uniupo.ktt.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.ktt.ui.components.PageTitle
import it.uniupo.ktt.ui.theme.buttonTextColor
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.tertiary

@Composable
fun TaskManagerScreen(navController: NavController) {
    if (!LocalInspectionMode.current && FirebaseAuth.getInstance().currentUser == null) {
        navController.navigate("landing") {
            popUpTo("task manager") { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Contenuto scrollabile
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()) // Scroll solo per il contenuto
        ) {
            PageTitle(
                navController = navController,
                title = "Task Manager"
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Ready",
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Ongoing",
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.size(20.dp))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Completed",
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }


        SmallFloatingActionButton(
            onClick = { navController.navigate("new task") },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd) // Posiziona in basso a destra
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
