package it.uniupo.ktt.ui.components.homePage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.theme.primary


@Composable
fun TargetButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
){
    Box(
        modifier = modifier
            .clickable { onClick() }
    ) {

        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            // contenitore Rectangle(Lilla) + cursore
            Box(
                modifier = Modifier
                    .offset(x = 7.dp, y = 0.dp)
                    .size(width = 70.dp, height = 60.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFF5DFFA)),
                contentAlignment = Alignment.CenterEnd
            ){
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(15.dp)
                        .padding(end= 3.dp)
                )

            }

            // contenitore Circle(Lilla) + Circle(Viole) + Sticker
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5DFFA)),
                contentAlignment = Alignment.Center
            ){

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFA47BD4)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.menu_target),
                        contentDescription = "Target Icon",
                        modifier = Modifier.size(38.dp),
                        //tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun TargetButtonPreview() {
    val navController = rememberNavController()

    TargetButton(
    )
}