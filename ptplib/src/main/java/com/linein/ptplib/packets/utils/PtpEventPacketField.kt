package com.linein.ptplib.packets.utils

import com.linein.ptplib.packets.Packet2
import com.linein.ptplib.packets.PtpEvent
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PtpEventPacketField(private val offset: Int): ReadOnlyProperty<Packet2, PtpEvent> {

    override fun getValue(thisRef: Packet2, property: KProperty<*>): PtpEvent {
        val intPacketField = IntPacketField(offset)
        val rawEventCode = intPacketField.getValue(thisRef, property)
        return PtpEvent.values().firstOrNull { it.eventCode == rawEventCode.toShort() }
            ?: throw IllegalArgumentException("Unknown event code: $rawEventCode")
    }

}