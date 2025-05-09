package it.uniupo.ktt.ui.components.statistics

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uniupo.ktt.R

@Composable
fun CircleDiagramLable(
    text : String,
    count : Int,
    statusColor: Color,
    modifier: Modifier = Modifier
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
//                .shadow(
//                    elevation = 6.dp,
//                    shape = CircleShape,
//                    clip = false
//                )
                .size(12.dp)
                .background(statusColor, shape = CircleShape)

        )

        Spacer(modifier = Modifier.width(5.dp))

        Column (
            modifier = Modifier
                .offset( y = 5.dp)
        ) {
            Text(
                text = text,
                fontFamily = FontFamily(Font(R.font.poppins_medium)),
                fontWeight = FontWeight(500),
                fontSize = 17.sp,
                color = Color(0xFF000000),
                modifier = Modifier
                    .offset(y = -(5.dp))
            )
            Text(
                text = "($count tasks)",
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                fontWeight = FontWeight(500),
                fontSize = 15.sp,
                color = Color(0xFF827676),
                modifier = Modifier
                    .offset(y = -(5.dp))
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CircleDiagramLablePreview() {
    CircleDiagramLable(
        text = "this year",
        count = 25,
        statusColor = Color(0xFF6326A9)
    )
}