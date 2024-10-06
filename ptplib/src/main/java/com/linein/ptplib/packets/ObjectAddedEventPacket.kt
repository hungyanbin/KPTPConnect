package com.linein.ptplib.packets

import com.linein.ptplib.packets.utils.Packet3

class ObjectAddedEventPacket(
    data: ByteArray
): Packet3(data) {

    val event = readPtpEvent()
    val objectId = readInt()
}