package it.uniupo.ktt.ui.components.homePage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import it.uniupo.ktt.ui.theme.primary

@Composable
fun AvatarSticker(
    avatarUrl: String,
    onClick: () -> Unit = {}
)
{
    // AVATAR Icon BOX
    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(1.2f)
    ){

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
                .padding(10.dp)
                .clickable(onClick = onClick) // Open Modal -> scelta avatar lista head->tail collegata
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ){
                if(avatarUrl == "null" || avatarUrl.isBlank()){
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Profile Icon",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                else {
                    // Mostra immagine se c'è avatar url
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(85.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun AvatarStickerPreview() {
    val navController = rememberNavController()

    AvatarSticker(
        avatarUrl = "Titolo di prova"
    )
}