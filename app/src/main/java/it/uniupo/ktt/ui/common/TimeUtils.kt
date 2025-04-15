package it.uniupo.ktt.ui.common

fun formatTimeStamp(timeStamp: Long): String {
    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timeStamp))
}
