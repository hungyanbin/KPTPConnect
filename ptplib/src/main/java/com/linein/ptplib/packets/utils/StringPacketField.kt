package com.linein.ptplib.packets.utils

import com.linein.ptplib.packets.Packet2
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class StringPacketField(private val offset: Int): ReadOnlyProperty<Packet2, String> {

    override fun getValue(thisRef: Packet2, property: KProperty<*>): String {
        return getCompactString(thisRef.data, offset)
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
}