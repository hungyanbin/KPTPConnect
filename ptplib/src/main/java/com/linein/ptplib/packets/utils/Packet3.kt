package com.linein.ptplib.packets.utils

import com.linein.ptplib.constants.ObjectFormat
import com.linein.ptplib.packets.PtpEvent

open class Packet3(
    private val data: ByteArray
) {
    private val fieldReader = FieldReader(data)

    protected fun readShort(): Short {
        return fieldReader.read(2) { data, offset ->
            getShortL(data, offset)
        }
    }

    protected fun readInt(): Int {
        return fieldReader.read(4) { data, offset ->
            getIntL(data, offset)
        }
    }

    protected fun readPtpEvent(): PtpEvent {
        return fieldReader.read(4) { data, offset ->
            val rawEventCode = getIntL(data, offset)
            PtpEvent.values().firstOrNull { it.eventCode == rawEventCode.toShort() }
                ?: throw IllegalArgumentException("Unknown event code: $rawEventCode")
        }
    }

    protected fun readObjectFormat(): ObjectFormat {
        return fieldReader.read(2) { data, offset ->
            val shortValue = getShortL(data, offset)
            ObjectFormat.values().firstOrNull { it.formatCode == shortValue }
                ?: throw IllegalArgumentException("Unknown format code: $shortValue")
        }
    }

    private fun getShortL(data: ByteArray, offset: Int): Short {
        return ((0x000000ff and data[offset].toInt())
                or ((0x000000ff and data[offset + 1].toInt()) shl 8)).toShort()
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
}

class FieldReader(
    private val data: ByteArray
) {
    private var currentOffset = 0
    
    fun <T> read(size: Int, reader: (ByteArray, Int) -> T): T {
        val value = reader(data, currentOffset)
        currentOffset += size
        return value
    }
}