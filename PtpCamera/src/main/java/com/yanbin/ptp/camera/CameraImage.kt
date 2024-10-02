package com.yanbin.ptp.camera

import java.time.LocalDateTime

data class CameraImage(
    val id: Int,
    val format: String,
    val width: Int,
    val height: Int,
    val fileName: String,
    val dataCreated: LocalDateTime,
    val sourceUrl: String? = null,
    val thumbUrl: String? = null,
) {
}