package it.uniupo.ktt.ui.components.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import it.uniupo.ktt.R


@Composable
fun ChatContactLable(
    nome: AnnotatedString,
    lastMessage: String,
    imgUrl: String,   // UrlDownload
    showBadge: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {


    Box(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
            .background(Color(0xFFF5DFFA))
            .width(220.dp)
    ) {

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()

        ) {

            IconButton(onClick = { /* zoom foto */ },
                modifier = Modifier
                    .shadow(4.dp, shape = CircleShape, clip = false)
                    .size(48.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                AsyncImage(
                    model = imgUrl,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .scale(0.8f)
                )

            }

            Spacer(modifier = Modifier.width(8.dp))

            Column (
                modifier= Modifier
                    .width(140.dp)
            ){
                val msgPreview= if (lastMessage.length <= 13) {
                lastMessage }
                else
                { lastMessage.take(13) + "..." }

                Text(
                    text = nome,
                    fontFamily = FontFamily(Font(R.font.poppins_medium)),
                    fontWeight = FontWeight(500)
                )
                Text(
                    text = msgPreview,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFF615252)
                )
            }

            // show ureadBadge
            if (showBadge) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF9A63F9), shape = CircleShape)

                )
            }
        }
    }

}
