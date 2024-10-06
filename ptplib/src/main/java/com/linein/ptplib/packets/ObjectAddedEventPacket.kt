package com.linein.ptplib.packets

import com.linein.ptplib.packets.utils.Packet3
import com.linein.ptplib.packets.utils.readPtpEvent

class ObjectAddedEventPacket(
    data: ByteArray
): Packet3(data) {

    val event = fieldReader.readPtpEvent()
    val objectId = fieldReader.readInt()
}