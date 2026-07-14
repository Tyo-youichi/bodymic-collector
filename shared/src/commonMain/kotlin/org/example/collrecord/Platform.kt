package org.example.collrecord

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform