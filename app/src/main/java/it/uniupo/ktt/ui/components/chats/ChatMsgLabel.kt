package it.uniupo.ktt.ui.components.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uniupo.ktt.ui.common.formatTimeStamp

import it.uniupo.ktt.ui.pages.caregiver.chat.ChatOpen

@Composable
fun ChatMsgLable(
    isMine : Boolean,
    text : String,
    timeStamp : Long?,
    seen : Boolean,
    modifier: Modifier
) {


    val backGroundColor = if (isMine) Color(0xFF9C46FF) else Color(0xFFF5DFFA)
    val textColor = Color(0xFF0C0C0C)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = backGroundColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (timeStamp != null) {
                    Text(
                        text = formatTimeStamp(timeStamp),
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }

                if (isMine) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (seen) Icons.Default.DoneAll else Icons.Default.Done,
                        contentDescription = "Seen",
                        tint = if (seen) Color(0xFF4CAF50) else textColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

}


@Preview
@Composable
fun ChatMsgLabelPreview() {
    ChatMsgLable(
        isMine = true,
        text = "Testo di prova",
        timeStamp = 1712844000,
        seen = false,
        modifier = Modifier
    )
}
