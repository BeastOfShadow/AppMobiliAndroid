package it.uniupo.ktt.ui.components.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uniupo.ktt.R
import it.uniupo.ktt.ui.common.formatTimeStamp

@Composable
fun ChatMsgBubble(
    isMine: Boolean,
    text: String,
    timeStamp: Long,
    seen: Boolean
) {
    val bubbleColor = if (isMine) Color(0xFF9C46FF) else Color(0xFFC5B5D8)
    val textColor = if (isMine) Color.White else Color.Black
    val alignment = if (isMine) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Column(
            modifier = Modifier
                .background(bubbleColor, shape = RoundedCornerShape(20.dp))
                .padding(13.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(
                text = text,

                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                fontWeight = FontWeight(400),
                fontSize = 17.sp,

                color = textColor
                )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatTimeStamp(timeStamp),

                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                fontWeight = FontWeight(400),
                fontSize = 13.sp,

                color = textColor,

                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
