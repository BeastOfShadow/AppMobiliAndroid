package it.uniupo.ktt.time

fun parseDurationToSeconds(duration: String): Int {
    val parts = duration.split(":")
    val hours = parts[0].toIntOrNull() ?: 0
    val minutes = parts[1].toIntOrNull() ?: 0

    return (hours * 3600) + (minutes * 60)
}