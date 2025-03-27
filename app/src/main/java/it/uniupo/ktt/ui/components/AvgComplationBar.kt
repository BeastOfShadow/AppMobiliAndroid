package it.uniupo.ktt.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AvgComplationBar(
    todayTime: Float,
    generalTime: Float,
    ratio: Float
) {
    val backgroundBarColor = Color(0xFFF5DFFA)
    val fillBarColor = Color(0xFFA47BD4)


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
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(50))
            .background(backgroundBarColor)
            .border(
                width = 1.dp,
                color = Color(0xFF6326A9).copy(alpha = 0.2f), // stesso stile bordo cerchi
                shape = RoundedCornerShape(50)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedRatio)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(fillBarColor)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AvgCompletionBarPreview() {
    AvgComplationBar(35.40.toFloat(), 40.12.toFloat(), (35.40/40.12).toFloat())
}
