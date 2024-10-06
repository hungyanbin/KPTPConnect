package com.linein.ptplib.packets.utils

import com.linein.ptplib.constants.ObjectFormat
import com.linein.ptplib.packets.Packet2
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ObjectFormatPacketField(private val offset: Int): ReadOnlyProperty<Packet2, ObjectFormat> {

    override fun getValue(thisRef: Packet2, property: KProperty<*>): ObjectFormat {
        val shortPacketField = ShortPacketField(offset)
        val shortValue = shortPacketField.getValue(thisRef, property)
        return ObjectFormat.values().firstOrNull { it.formatCode == shortValue }
            ?: throw IllegalArgumentException("Unknown format code: $shortValue")
    }

}