package it.uniupo.ktt.ui.components.task

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.uniupo.ktt.ui.components.task.taskmanager.ElapsedTimeDisplay
import it.uniupo.ktt.ui.model.Task
import it.uniupo.ktt.ui.theme.secondary
import it.uniupo.ktt.ui.theme.tertiary
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Composable
fun CircularTimer(task: Task) {
    val startMillis = task.timeStampStart.toDate().time
    val estimatedDurationMillis = (task.completionTimeEstimate * 1000L)
        .coerceAtLeast(1000L) // evita divisione per 0 o valori nulli

    var progress by remember { mutableStateOf(0f) }

    if (task.active) {
        LaunchedEffect(task.timeStampStart, task.completionTimeEstimate) {
            var keepUpdating = true
            while (keepUpdating) {
                val currentMillis = System.currentTimeMillis()
                val elapsedMillis = currentMillis - startMillis
                val cappedMillis = elapsedMillis.coerceAtMost(estimatedDurationMillis)

                progress = (cappedMillis.toFloat() / estimatedDurationMillis).coerceIn(0f, 1f)

                keepUpdating = true // continua anche dopo che è piena
                delay(1000L) // aggiornamento ogni secondo
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp)
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = tertiary,
            strokeWidth = 6.dp,
            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
        )
        ElapsedTimeDisplay(task = task) // mostra solo HH:MM
    }
}


