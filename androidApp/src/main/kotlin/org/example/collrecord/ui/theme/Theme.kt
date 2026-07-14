package org.example.collrecord.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CollectorColorScheme = lightColorScheme(
    primary = CollectorBlue,
    onPrimary = Color.White,
    secondary = CollectorBlueDark,
    background = Color.White,
    surface = Color.White
)

/** Theme app-wide — ganti default Material3 (ungu) jadi biru. */
@Composable
fun CollectorTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CollectorColorScheme, content = content)
}
