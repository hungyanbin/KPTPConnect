package com.yanbin.ptp.utils

import timber.log.Timber
import java.time.LocalDateTime

fun <T> tryOrNull(block: () -> T): T? {
    return try {
        block()
    } catch (e: Throwable) {
        Timber.e(e)
        null
    }
}

suspend fun <T> suspendTryOrNull(block: suspend () -> T): T? {
    return try {
        block()
    } catch (e: Throwable) {
        Timber.e(e)
        null
    }
}

fun LocalDateTime.toTimestamp(): Long {
    return this.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun Long.asLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(this), java.time.ZoneId.systemDefault())
}

fun measureTime(block: () -> Unit) {
    val start = System.currentTimeMillis()
    block()
    val end = System.currentTimeMillis()
    Timber.d("Time: ${end - start}ms")
}
