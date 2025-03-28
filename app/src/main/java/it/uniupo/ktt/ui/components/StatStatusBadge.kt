package it.uniupo.ktt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip

import androidx.compose.ui.draw.shadow

@Composable
fun StatStatusBadge(
    statusColor : Color,
    statusText : String,
    modifier : Modifier = Modifier,
    ){

    Box(
        modifier = modifier
//            .shadow(elevation = 4.dp, shape = RoundedCornerShape(50), clip = false, ambientColor = Color.Black.copy(alpha = 1f), spotColor = Color.Black.copy(alpha = 1f))
//            .clip(RoundedCornerShape(50))
            .border(2.dp, Color(0xFF403E3E), RoundedCornerShape(50))
            .background(Color(0xFFFFFFFF), shape= RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)

    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            //cerchio (alternativa canvas)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(statusColor, shape = CircleShape)
            )

            Spacer(modifier = Modifier.width(6.dp))

            //text ()
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyLarge, //Poppins

                fontSize = 22.sp,
                fontWeight = FontWeight(500),

                color = Color(0xFF000000)
            )


        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatStatusBadgePreview() {
    StatStatusBadge(Color(0xFF6326A9), "Ongoing")
}
