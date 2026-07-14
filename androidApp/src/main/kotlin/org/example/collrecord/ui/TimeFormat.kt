package org.example.collrecord.ui

/** "00:00:03" — dipakai buat timer besar. */
fun formatDurationMs(ms: Int): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

/** "00:02" — dipakai buat label ruler. */
fun formatDurationShort(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
