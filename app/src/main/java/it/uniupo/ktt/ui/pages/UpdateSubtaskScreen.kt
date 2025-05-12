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
import it.uniupo.ktt.ui.components.task.ReadOnlyTextField
import it.uniupo.ktt.ui.components.task.ReadOnlyTextFieldPreview
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

            Spacer(modifier = Modifier.size(30.dp))

            ReadOnlyTextField(
                label = "Task name:",
                value = "Task name"
            )

            Spacer(modifier = Modifier.size(20.dp))

            ReadOnlyTextField(
                label = "Employee:",
                value = "Task name"
            )
        }
    }
}


@Preview
@Composable
fun UpdateSubtaskScreen() {
    UpdateSubtaskScreen(navController = NavController(context = LocalContext.current))
}
