package it.uniupo.ktt.ui.components.statistics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CircleDiagramProgressBars(
    yearly: Int,
    monthly: Int,
    weekly: Int
) {
    // definizione Massimi per le 3 progressBar
    val maxYearly = 100
    val maxMonthly = 50
    val maxWeekly = 20

    // calcolo dei Ratio (in Lista per animation)
    val ratioList = if(yearly == 0){
        listOf(1f, 1f, 1f)
    }
    else{
        listOf(
            (yearly.toFloat() / maxYearly).coerceIn(0f, 1f),
            (monthly.toFloat() / maxMonthly).coerceIn(0f, 1f),
            (weekly.toFloat() / maxWeekly).coerceIn(0f, 1f)
        )
    }

                            //ANIMAZIONE 3 Bars -> (Uso "Animatable" per gestire meglio 3 animazioni in parallelo)
    val animatedRatios = remember {
        List(3) { Animatable(0f) }
    }
    // Avvia le animazioni solo una volta
    LaunchedEffect(Unit) {
        animatedRatios.forEachIndexed { i, animatable ->
            launch {
                animatable.animateTo(
                    targetValue = ratioList[i],
                    animationSpec = tween(durationMillis = 800)
                )
            }
        }
    }



    Canvas(
        modifier = Modifier
            .size(160.dp)
    ) {
        val center = size.center
        val strokeWidth = 26f       // spessore arco
        val cap = StrokeCap.Round   // arrotondamento estremità

        val startAngle = -90f
        val sweepAngleMax = 360f

        // Raggi rispetto al "center"
        val circles = listOf(
            200f,
            160f,
            115f
        )

        // Colori
        val colors = if(yearly == 0){
            listOf(
                Color(0xFFF5DFFA),
                Color(0xFFF5DFFA),
                Color(0xFFF5DFFA)
            )
        }
        else{
            listOf(
                Color(0xFF6326A9),
                Color(0xFFA47BD4),
                Color(0xFFF5DFFA))
        }

        for (i in 0..2) {
            drawArc(
                color = colors[i],
                startAngle = startAngle,
                sweepAngle = sweepAngleMax * animatedRatios[i].value,
                useCenter = false,
                topLeft = Offset(center.x - circles[i], center.y - circles[i]),
                size = Size(circles[i] * 2, circles[i] * 2),
                style = Stroke(width = strokeWidth, cap = cap)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CircleDiagramProgressBarPreview() {
    CircleDiagramProgressBars(
        yearly = 80,
        monthly = 25,
        weekly = 6
    )
}