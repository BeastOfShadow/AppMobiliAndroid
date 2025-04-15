package it.uniupo.ktt.ui.components.chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.uniupo.ktt.R


@Composable
fun ChatContactLable(
    nome: String,
    lastMessage: String,
    imgId: Int,   //R.drawable?
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

        ) {
            IconButton(onClick = { /* opzionale: zoom foto */ },
                modifier = Modifier
                    .shadow(4.dp, shape = CircleShape, clip = false)
                    .size(48.dp)
                    .background(Color.White, shape = CircleShape)
            ) {
                Image(
                    //painter = painterResource(imgId),
                    painter = painterResource(imgId),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .offset(x= 0.dp, y= 0.dp)
                        .scale(0.8f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                val msgPreview= if (lastMessage.length <= 15) {
                lastMessage }
                else
                { lastMessage.take(25) + "..." }

                Text(
                    text = nome,
                    style = MaterialTheme.typography.bodyLarge ,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = msgPreview,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }

}
