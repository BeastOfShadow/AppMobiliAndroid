package it.uniupo.ktt.ui.components.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
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

    // calcolo dei Ratio
    val ratioYearly = (yearly.toFloat() / maxYearly).coerceIn(0f, 1f)
    val ratioMonthly = (monthly.toFloat() / maxMonthly).coerceIn(0f, 1f)
    val ratioWeekly = (weekly.toFloat() / maxWeekly).coerceIn(0f, 1f)

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
        val colors = listOf(
            Color(0xFF6326A9),
            Color(0xFFA47BD4),
            Color(0xFFF5DFFA)
        )
        val ratios = listOf(ratioYearly, ratioMonthly, ratioWeekly)

        for (i in ratios.indices) {
            drawArc(
                color = colors[i],
                startAngle = startAngle,
                sweepAngle = sweepAngleMax * ratios[i],
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