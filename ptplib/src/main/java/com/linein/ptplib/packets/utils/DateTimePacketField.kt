package com.linein.ptplib.packets.utils

import com.linein.ptplib.packets.Packet2
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class DateTimePacketField(private val offset: Int): ReadOnlyProperty<Packet2, LocalDateTime> {

    override fun getValue(thisRef: Packet2, property: KProperty<*>): LocalDateTime {
        val stringPacketField = StringPacketField(offset)
        val dateTimeString = stringPacketField.getValue(thisRef, property)
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.0")
        return LocalDateTime.parse(dateTimeString, formatter)
    }
}