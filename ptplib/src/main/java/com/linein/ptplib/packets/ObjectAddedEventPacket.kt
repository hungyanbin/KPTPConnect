package com.linein.ptplib.packets

data class ObjectAddedEventPacket(
    val event: PtpEvent,
    val objectId: Int,
) {

    companion object {
        private const val offset_objectId = 0x02

        fun fromPacket(packet: Packet): ObjectAddedEventPacket {
            val event = PtpEvent.fromPacket(packet) ?: throw IllegalStateException("Should not be null!!")

            val objectId = packet.getIntL(offset_objectId)
            return ObjectAddedEventPacket(event, objectId)
        }
    }
}