package it.uniupo.ktt.time

import com.google.firebase.Timestamp
import java.time.ZoneId
import java.time.ZonedDateTime

fun parseDurationToSeconds(duration: String): Int {
    val parts = duration.split(":")
    val hours = parts[0].toIntOrNull() ?: 0
    val minutes = parts[1].toIntOrNull() ?: 0

    return (hours * 3600) + (minutes * 60)
}

fun formatSecondsToHHMM(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return String.format("%02d:%02d", hours, minutes)
}


fun isToday(timestamp: Timestamp): Boolean {
    val localDateTime = timestamp.toDate().toInstant()
        .atZone(ZoneId.systemDefault())

    val now = ZonedDateTime.now(ZoneId.systemDefault())

    return localDateTime.year == now.year &&
            localDateTime.dayOfYear == now.dayOfYear
}
