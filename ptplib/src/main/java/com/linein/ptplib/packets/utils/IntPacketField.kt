package com.linein.ptplib.packets.utils

import com.linein.ptplib.packets.Packet2
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class IntPacketField(private val offset: Int): ReadOnlyProperty<Packet2, Int> {

    override fun getValue(thisRef: Packet2, property: KProperty<*>): Int {
        return getIntL(thisRef.data, offset)
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