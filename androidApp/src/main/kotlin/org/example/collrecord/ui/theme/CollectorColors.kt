package org.example.collrecord.ui.theme

import androidx.compose.ui.graphics.Color

// Palet biru (ganti default Material3 yang ungu).
val CollectorBlue = Color(0xFF4361EE)
val CollectorBlueDark = Color(0xFF3046C4)
val CollectorBlueLight = Color(0xFFEDF2FF)

data class AccentColor(val solid: Color, val background: Color)

// Warna aksen per kategori (business_unit) — bukan biru semua, biar tetap ada variasi
// kayak referensi, cuma nggak ada ungu di dalamnya.
private val accentPalette = listOf(
    AccentColor(Color(0xFFFFA94D), Color(0xFFFFF3E0)), // oranye
    AccentColor(Color(0xFF3BC9DB), Color(0xFFE3FAFC)), // teal
    AccentColor(Color(0xFF4C6EF5), Color(0xFFEDF2FF)), // biru
    AccentColor(Color(0xFFFF8787), Color(0xFFFFF0F0)), // merah muda
    AccentColor(Color(0xFFFCC419), Color(0xFFFFF9DB))  // kuning
)

fun accentColorFor(key: String): AccentColor {
    val index = kotlin.math.abs(key.hashCode()) % accentPalette.size
    return accentPalette[index]
}
