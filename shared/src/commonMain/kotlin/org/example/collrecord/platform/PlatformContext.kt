package org.example.collrecord.platform

/**
 * Wrapper platform-agnostic untuk "context" native tiap platform.
 * Android: bungkus android.content.Context.
 * iOS: belum dipakai (kosong) — placeholder biar shared module tetap compile untuk iOS target.
 */
expect class PlatformContext
