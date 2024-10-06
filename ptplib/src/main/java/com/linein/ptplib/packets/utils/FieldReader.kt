package com.linein.ptplib.packets.utils

import com.linein.ptplib.constants.ObjectFormat
import com.linein.ptplib.packets.PtpEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FieldReader(
    private val data: ByteArray
) {
    private var currentOffset = 0

    fun readInt(): Int {
        val reader = { data: ByteArray, offset: Int -> getIntL(data, offset) }
        val stepper = { _: ByteArray, _: Int -> 4 }
        return readValue(reader, stepper)
    }

    fun readShort(): Short {
        val reader = { data: ByteArray, offset: Int -> getShortL(data, offset) }
        val stepper = { _: ByteArray, _: Int -> 2 }
        return readValue(reader, stepper)
    }

    fun readString(): String {
        val reader = { data: ByteArray, offset: Int -> getCompactString(data, offset) }
        val stepper = { data: ByteArray, offset: Int ->
            val stringLength = data[offset].toInt()
            stringLength * 2 + 1
        }
        return readValue(reader, stepper)
    }

    fun readShortArray(): ShortArray {
        val length = readInt()
        return 0.until(length).map { readShort() }.toShortArray()
    }

    fun <T> readValue(reader: (ByteArray, Int) -> T, stepper: (ByteArray, Int) -> Int): T {
        val value = reader(data, currentOffset)
        stepOffset(stepper(data, currentOffset))
        return value
    }

    private fun getCompactString(data: ByteArray, offset: Int): String {
        var offset = offset
        val no: Int = data[offset++].toInt()

        val charbuffer = CharArray(no)
        var len = 0
        while (len < charbuffer.size && offset < data.size) {
            charbuffer[len] = Char(data[offset].toUShort())
            if (charbuffer[len] == '\u0000') {
                break
            }
            len++
            offset += 2
        }
        return String(charbuffer, 0, len)
    }

    private fun stepOffset(size: Int) {
        currentOffset += size
    }

    private fun getIntL(data: ByteArray, offset: Int): Int {
        var value = 0

        var i = 0
        var shift = 0
        while (i < 4 && offset + i < data.size) {
            value = value or ((0x000000ff and data[offset + i].toInt()) shl shift)
            i++
            shift += 8
        }
        return value
    }

    private fun getShortL(data: ByteArray, offset: Int): Short {
        return ((0x000000ff and data[offset].toInt())
                or ((0x000000ff and data[offset + 1].toInt()) shl 8)).toShort()
    }
}

fun FieldReader.readPtpEvent(): PtpEvent {
    val rawEventCode = readInt()
    return PtpEvent.values().firstOrNull { it.eventCode == rawEventCode.toShort() }
        ?: throw IllegalArgumentException("Unknown event code: $rawEventCode")
}

fun FieldReader.readObjectFormat(): ObjectFormat {
    val shortValue = readShort()
    return ObjectFormat.values().firstOrNull { it.formatCode == shortValue }
        ?: ObjectFormat.Undefined
}

fun FieldReader.readDateTime(): LocalDateTime {
    val stringValue = readString()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.0")
    return LocalDateTime.parse(stringValue, formatter)
}