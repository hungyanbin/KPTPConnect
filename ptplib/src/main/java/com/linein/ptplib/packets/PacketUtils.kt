package com.linein.ptplib.packets

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Packet.getDateTime(offset: Int): LocalDateTime {
    val dateTimeString = getCompactString(offset)
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.0")
    return LocalDateTime.parse(dateTimeString, formatter)
}