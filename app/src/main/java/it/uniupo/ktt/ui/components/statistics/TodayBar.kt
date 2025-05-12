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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import it.uniupo.ktt.ui.theme.titleColor

@Composable
fun TodayBar(
    ratio : Float,
    doneCount : Int,
    totalCount : Int
) {
    val backgroundBarColor = Color(0xFFF5DFFA)
    val fillBarColor = Color(0xFF6326A9)


                            //ANIMAZIONE

    //stato interno per animare 1 sola volta la barra
    var animatedStart by remember { mutableStateOf(false) }
    //scelta animazione
    val animatedRatio by animateFloatAsState(
        targetValue = if (animatedStart) ratio.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "animated ratio"
    )
    // Attiva l’animazione una volta al primo recomposition
    LaunchedEffect(Unit) {
        animatedStart = true
    }


    Box(
        modifier = Modifier
            .height(100.dp)
    ){

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Today's Tasks",
                fontFamily = FontFamily(Font(R.font.poppins_medium)),

                fontSize = 22.sp,
                fontWeight = FontWeight(500),

                color = Color(0xFF2A2525)
            )
            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)

            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Left label
                    StatTodayLabel(value = doneCount, label = "(done)")

                    Spacer(modifier = Modifier.width(8.dp))

                                            // BAR
                    Box( // Barra 1
                        modifier = Modifier
                            .width(200.dp)
                            .height(22.dp)
                            .offset( y = -(3.dp))
                            .clip(RoundedCornerShape(50))
                            .background(backgroundBarColor)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF6326A9).copy(alpha = 0.2f), // bordo
                                shape = RoundedCornerShape(50)
                            )
                    ) {
                        Box( // Barra 2
                            modifier = Modifier
                                .fillMaxWidth(animatedRatio) // piena fino al valore "animatedRatio" (derivato da "ratio")
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(50))
                                .background(fillBarColor)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Right label
                    StatTodayLabel(value = totalCount, label = "(toDo)")

                }

            }


        }


    }

}

@Preview(showBackground = true)
@Composable
fun TodayBarPreview() {
    TodayBar(
        1f,
        doneCount = 5,
        totalCount = 10
    )
}