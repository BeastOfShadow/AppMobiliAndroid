package it.uniupo.ktt.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.annotations.concurrent.Background
import it.uniupo.ktt.R
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
    image: Int,
    imageDescription: String
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
                        .size(60.dp)
                        .background(
                            Color(0xFFA47BD4),
                            shape = CircleShape
                        )
                        .padding(7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = imageDescription,
                        modifier = Modifier.size(120.dp)
                    )
                }
                Column (
                    modifier = Modifier.padding(start = 10.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = title,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontSize = 20.sp,
                        fontWeight = FontWeight(400),
                        color = Color.Black
                    )

                    Text(
                        text = description,

                        fontFamily = FontFamily(Font(R.font.poppins_light)),
                        style = MaterialTheme.typography.bodySmall, //Poppins
                        fontSize = 16.sp,
                        fontWeight = FontWeight(400),

                        color = Color(0xFF746767),
                        modifier = Modifier.padding(start = 8.dp)
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
        image = R.drawable.menu_chat,
        imageDescription = "Titolo di prova"
    )
}