package it.uniupo.ktt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.PhoneMissed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import it.uniupo.ktt.ui.theme.lighterSubtitle
import it.uniupo.ktt.ui.theme.lighterTitle
import it.uniupo.ktt.ui.theme.primary
import it.uniupo.ktt.ui.theme.secondary

@Composable
fun MenuLabel(
    navController: NavController,
    navPage: String,
    title: String,
    description: String,
    icon: ImageVector,
    iconDescription: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.extraLarge)
            .clickable {navController.navigate(navPage)}
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = primary,
                    shape = MaterialTheme.shapes.extraLarge
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
                            secondary,
                            shape = CircleShape
                        )
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = iconDescription,
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
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = lighterTitle
                    )

                    Text(
                        text = description,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp,
                        color = lighterSubtitle
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                    contentDescription = "Back",
                    modifier = Modifier.size(30.dp)
                        .align(Alignment.CenterVertically)
                        .padding(end = 10.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuLabel() {
    val navController = rememberNavController()

    MenuLabel(
        navController = navController,
        navPage = "Titolo di prova",
        title = "Titolo di prova",
        description = "Titolo di prova",
        icon = Icons.AutoMirrored.Outlined.PhoneMissed,
        iconDescription = "Titolo di prova"
    )
}