package it.uniupo.ktt.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.drawText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DailyTasksBubbleChart(
    completed: Float,
    ongoing: Float,
    ready: Float,
    completedNumb: Int,
    onGoingNumb: Int,
    readyNumb: Int
) {
    //val maxValue = maxOf(completed, ongoing, ready)
    val scale = 2.5f // fattore moltiplicativo per dimensionare bene i cerchi

    val completedRadius = completed * scale
    val ongoingRadius = ongoing * scale
    val readyRadius = ready * scale

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()
            .aspectRatio(1f) //mantieni 1:1 anche dopo l'inserimento nella column
        ) {
            val center = Offset(size.width / 2, size.height / 2)




                                    // Completed Tasks
            // Completed Tasks Circle
            drawCircle(
                color = Color(0xFF6326A9),
                radius = completedRadius,
                center = center + Offset(8f+20f, -56f-40f)
            )


                                        // Ready Tasks
            // Ready Tasks Circle
            drawCircle(
                color = Color(0xFFF5DFFA),
                radius = readyRadius,
                center = center + Offset(37f+100f, 41f+180f)
            )
            // + border Circle
            drawCircle(
                color = Color(0xFF6326A9).copy(alpha = 0.2f),
                radius = readyRadius,
                center = center + Offset(37f+100f, 41f+180f),
                style = Stroke( // Set the style to Stroke to create a border
                    width = 1.dp.toPx() // Set the thickness of the border (3 pixels in this case)
                )

            )


                                        // OnGoing Tasks
            // Ongoing Tasks Circle
            drawCircle(
                color = Color(0xFFA47BD4),
                radius = ongoingRadius,
                center = center + Offset(-39f-60f, 12f+100f)

            )
            // + border Circle
            drawCircle(
                color = Color(0xFF6326A9).copy(alpha = 0.2f),
                radius = ongoingRadius,
                center = center + Offset(-39f-60f, 12f+100f),
                style = Stroke( // Set the style to Stroke to create a border
                    width = 1.dp.toPx() // Set the thickness of the border (3 pixels in this case)
                )
            )


            //Scrivi il numero dei Tasks al centro dei Cerchi
            drawIntoCanvas { canvas ->
                val completedPaint = android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 33.sp.toPx() // Adjust the text size as needed
                    color = android.graphics.Color.WHITE // Text color
                }
                val ongoingPaint = android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 33.sp.toPx() // Adjust the text size as needed
                    color = android.graphics.Color.BLACK // Text color
                }
                val readyPaint = android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 33.sp.toPx() // Adjust the text size as needed
                    color = android.graphics.Color.WHITE // Text color
                }

                // completedNumb
                canvas.nativeCanvas.drawText(
                    "$completedNumb",
                    (center.x + 8f + 20f),
                    (center.y - 56f - 8f),
                    completedPaint
                )

                // readyNumb
                canvas.nativeCanvas.drawText(
                    "$readyNumb",
                    (center.x + 37f + 99f) ,
                    (center.y + 41f + 212f),
                    readyPaint
                )

                // onGoingNumb
                canvas.nativeCanvas.drawText(
                    "$onGoingNumb",
                    (center.x - 39f - 60f),
                    (center.y + 12f + 132f),
                    ongoingPaint
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DailyTasksBubbleChartPreview() {
    DailyTasksBubbleChart(15f, 15f, 15f, 0, 0 ,0)
}
