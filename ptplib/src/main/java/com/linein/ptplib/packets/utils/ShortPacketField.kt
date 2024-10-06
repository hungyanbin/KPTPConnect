package com.linein.ptplib.packets.utils

import com.linein.ptplib.packets.Packet2
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ShortPacketField(private val offset: Int): ReadOnlyProperty<Packet2, Short> {

    override fun getValue(thisRef: Packet2, property: KProperty<*>): Short {
        return getShortL(thisRef.data, offset)
    }

    private fun getShortL(data: ByteArray, offset: Int): Short {
        return ((0x000000ff and data[offset].toInt())
                or ((0x000000ff and data[offset + 1].toInt()) shl 8)).toShort()
    }
}