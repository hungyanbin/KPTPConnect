package com.linein.ptplib.packets

import com.linein.ptplib.packets.utils.IntPacketField
import com.linein.ptplib.packets.utils.PtpEventPacketField

class ObjectAddedEventPacket(
    data: ByteArray
): Packet2(data) {

    val event by PtpEventPacketField(0)
    val objectId by IntPacketField(OFFSET_OBJECT_ID)

    companion object {
        private const val OFFSET_OBJECT_ID = 0x02
    }
}