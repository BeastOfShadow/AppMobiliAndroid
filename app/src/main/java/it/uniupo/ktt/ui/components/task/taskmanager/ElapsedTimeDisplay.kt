package it.uniupo.ktt.ui.components.task.taskmanager

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import it.uniupo.ktt.ui.model.Task
import it.uniupo.ktt.ui.theme.subtitleColor
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@SuppressLint("DefaultLocale")
@Composable
fun ElapsedTimeDisplay(task: Task) {
    var elapsedTimeText by remember { mutableStateOf("00:00") }

    val maxMillis = TimeUnit.HOURS.toMillis(99) + TimeUnit.MINUTES.toMillis(59)
    if(task.active) {
        LaunchedEffect(task.timeStampStart) {

            var keepUpdating = true
            while (keepUpdating) {
                val currentMillis = System.currentTimeMillis()
                val startMillis = task.timeStampStart.toDate().time
                val elapsedMillis = currentMillis - startMillis

                val cappedMillis = elapsedMillis.coerceAtMost(maxMillis)
                val hours = TimeUnit.MILLISECONDS.toHours(cappedMillis)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(cappedMillis) % 60

                elapsedTimeText = String.format("%02d:%02d", hours, minutes)

                if (elapsedMillis >= maxMillis) {
                    keepUpdating = false // stop when reaching 99:59
                } else {
                    delay(60_000) // update every minute
                }
            }
        }
    }

    Text(
        text = elapsedTimeText,
        fontSize = 12.sp,
        color = subtitleColor,
        textAlign = TextAlign.Center
    )
}

