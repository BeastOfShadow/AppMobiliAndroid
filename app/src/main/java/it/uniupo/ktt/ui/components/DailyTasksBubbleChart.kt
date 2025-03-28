package it.uniupo.ktt.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity


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

    //Valori finali NUMBER
    val density = LocalDensity.current
    val finalTextSizePx = with(density) { 33.sp.toPx() } //dimensione numeri finale


                                        //ANIMAZIONI

    //Stato BubbleCharts
    var animateStart by remember { mutableStateOf(false) }
    //Stato Numbers
    var animateTextStart by remember { mutableStateOf(false) }
    //Stato BubbleChartBadge
    var animateBubbleChart by remember { mutableStateOf(false) }

    //Avvia Animazioni all'ingresso nella page
    LaunchedEffect(Unit) {
        animateStart = true                     //Avvia BubbleCharts
        kotlinx.coroutines.delay(500)  // Delay
        animateTextStart = true                 //Avvia Numers
        animateBubbleChart = true               //Avvia Badge BubbleChart
    }

                            //NUMBER ANIMATION (zoom + spring)
    val animatedTextSizeCompleted by animateFloatAsState(
        targetValue = if (animateTextStart) finalTextSizePx else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "textSizeCompleted"
    )

    val animatedTextSizeOngoing by animateFloatAsState(
        targetValue = if (animateTextStart) finalTextSizePx else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "textSizeOngoing"
    )

    val animatedTextSizeReady by animateFloatAsState(
        targetValue = if (animateTextStart) finalTextSizePx else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "textSizeReady"
    )


                            //BUBBLE ANIMATION (zoom: da "0" a "DIM finale passata")
    val animatedCompletedRadius by animateFloatAsState(
        targetValue = if (animateStart) completed * scale else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = 0),
        label = "completedRadius"
    )

    val animatedOngoingRadius by animateFloatAsState(
        targetValue = if (animateStart) ongoing * scale else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = 150),
        label = "ongoingRadius"
    )

    val animatedReadyRadius by animateFloatAsState(
        targetValue = if (animateStart) ready * scale else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = 300),
        label = "readyRadius"
    )

                            //BUBBLE-BADGE ANIMATION (zoom)
    val animatedBubbleScaleCompleted by animateFloatAsState(
        targetValue = if (animateBubbleChart) 0.63f else 0f,
        animationSpec = tween(durationMillis = 400, delayMillis = 0),
        label = "completedScale"
    )

    val animatedBubbleScaleOngoing by animateFloatAsState(
        targetValue = if (animateBubbleChart) 0.63f else 0f,
        animationSpec = tween(durationMillis = 400, delayMillis = 150),
        label = "ongoingScale"
    )

    val animatedBubbleScaleReady by animateFloatAsState(
        targetValue = if (animateBubbleChart) 0.63f else 0f,
        animationSpec = tween(durationMillis = 400, delayMillis = 300),
        label = "readyScale"
    )






    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f) //mantieni 1:1 anche dopo l'inserimento nella column
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            

                                    // COMPLETED TASKS
            // Completed Tasks Circle
            drawCircle(
                color = Color(0xFF6326A9),
                radius = animatedCompletedRadius,
                center = center + Offset(8f, -186f)
            )

            // Completed Task Number
            drawIntoCanvas { canvas ->
                val completedPaint = android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = animatedTextSizeCompleted // Adjust the text size as needed
                    color = android.graphics.Color.WHITE // Text color
                }
                // completedNumb
                canvas.nativeCanvas.drawText(
                    "$completedNumb",
                    (center.x + 8f),
                    (center.y - 154f),
                    completedPaint
                )

            }



                                        // READY TASKS
            // Ready Tasks Circle
            drawCircle(
                color = Color(0xFFF5DFFA),
                radius = animatedReadyRadius,
                center = center + Offset(117f, 131f)
            )
            // + border Circle
            drawCircle(
                color = Color(0xFF6326A9).copy(alpha = 0.2f),
                radius = animatedReadyRadius,
                center = center + Offset(117f, 131f),
                style = Stroke( // Set the style to Stroke to create a border
                    width = 1.dp.toPx() // Set the thickness of the border (3 pixels in this case)
                )

            )
            // Ready Task Number
            drawIntoCanvas { canvas ->
                val readyPaint = android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = animatedTextSizeReady // Adjust the text size as needed
                    color = Color(0xFF6326A9).toArgb() // Text color
                }
                // readyNumb
                canvas.nativeCanvas.drawText(
                    "$readyNumb",
                    (center.x + 116f),
                    (center.y + 163f),
                    readyPaint
                )
            }



                                        // ONGOING TASK
            // Ongoing Tasks Circle
            drawCircle(
                color = Color(0xFFA47BD4),
                radius = animatedOngoingRadius,
                center = center + Offset(-119f, 22f)

            )
            // + border Circle
            drawCircle(
                color = Color(0xFF6326A9).copy(alpha = 0.2f),
                radius = animatedOngoingRadius,
                center = center + Offset(-119f, 22f),
                style = Stroke( // Set the style to Stroke to create a border
                    width = 1.dp.toPx() // Set the thickness of the border (3 pixels in this case)
                )
            )
            // Ongoing Task Number
            drawIntoCanvas { canvas ->
                val ongoingPaint = android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = animatedTextSizeOngoing // Adjust the text size as needed
                    color = Color(0xFFF5DFFA).toArgb() /* Text color */
                }
                // onGoingNumb
                canvas.nativeCanvas.drawText(
                    "$onGoingNumb",
                    (center.x - 119f),
                    (center.y + 54f),
                    ongoingPaint
                )
            }

        }

                                    //BADGE-BUBBLECHART
        //COMPLETED TASKS BADGE
        StatStatusBadge(
            statusColor = Color(0xFF6326A9),
            statusText = "Completed",

            //Modifier aggiuntivi utili passabili
            modifier = Modifier

                .offset(x = (-40).dp, y = (-145).dp)
                .scale(animatedBubbleScaleCompleted)

        )
        //ONGOING TASKS BADGE
        StatStatusBadge(
            statusColor = Color(0xFFA47BD4),
            statusText = "Ongoing",

            //Modifier aggiuntivi utili passabili
            modifier = Modifier

                .offset(x = (-110).dp, y = (-35).dp)
                .scale(animatedBubbleScaleOngoing)

        )
        //READY TASKS BADGE
        StatStatusBadge(
            statusColor = Color(0xFFF5DFFA),
            statusText = "Ready",

            //Modifier aggiuntivi utili passabili
            modifier = Modifier

                .offset(x = (110).dp, y = (90).dp)
                .scale(animatedBubbleScaleReady)

        )




    }
}


@Preview(showBackground = true)
@Composable
fun DailyTasksBubbleChartPreview() {
    DailyTasksBubbleChart(80f, 80f, 80f, 200, 200 ,200)
}
