package it.uniupo.ktt.ui.components.statistics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uniupo.ktt.R


@Composable
fun CircleDiagram(
    yearly: Int,
    monthly: Int,
    weekly: Int
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Completed Tasks",
            fontFamily = FontFamily(Font(R.font.poppins_medium)),

            fontSize = 22.sp,
            fontWeight = FontWeight(500),

            color = Color(0xFF2A2525)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            // CircleDiagram -> 3 archi concentrici
            CircleDiagramProgressBars(
                yearly = yearly,
                monthly = monthly,
                weekly = weekly
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                CircleDiagramLable(
                    text = "this year",
                    count = yearly,
                    statusColor = Color(0xFF6326A9)
                )
                Spacer(modifier = Modifier.height(6.dp))
                CircleDiagramLable(
                    text = "this month",
                    count = monthly,
                    statusColor = Color(0xFFA47BD4),
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                CircleDiagramLable(
                    text = "this week",
                    count = weekly,
                    statusColor = Color(0xFFF5DFFA),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CircleDiagramPreview() {
    CircleDiagram(
        yearly= 100,
        monthly= 100,
        weekly= 100
    )
}