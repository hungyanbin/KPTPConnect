package com.linein.ptplib.packets

import com.linein.ptplib.packets.utils.DateTimePacketField
import com.linein.ptplib.packets.utils.IntPacketField
import com.linein.ptplib.packets.utils.ObjectFormatPacketField
import com.linein.ptplib.packets.utils.ShortPacketField
import com.linein.ptplib.packets.utils.StringPacketField

open class Packet2(val data: ByteArray)

class ObjectInfoPacket(
    data: ByteArray
): Packet2(data) {

    constructor(packet: Packet): this(packet.packet)

    val storageId by IntPacketField(OFFSET_STORAGE_ID)
    val objectFormat by ObjectFormatPacketField(OFFSET_OBJECT_FORMAT)
    val protectionStatus by ShortPacketField(OFFSET_PROTECTION_STATUS)
    val objectCompressedSize by IntPacketField(OFFSET_OBJECT_COMPRESSED_SIZE)
    val thumbFormat by ObjectFormatPacketField(OFFSET_THUMB_FORMAT)
    val thumbCompressedSize by IntPacketField(OFFSET_THUMB_COMPRESSED_SIZE)
    val thumbPixWidth by IntPacketField(OFFSET_THUMB_PIX_WIDTH)
    val thumbPixHeight by IntPacketField(OFFSET_THUMB_PIX_HEIGHT)
    val imagePixWidth by IntPacketField(OFFSET_IMAGE_PIX_WIDTH)
    val imagePixHeight by IntPacketField(OFFSET_IMAGE_PIX_HEIGHT)
    val imageBitDepth by IntPacketField(OFFSET_IMAGE_BIT_DEPTH)
    val parentObject by IntPacketField(OFFSET_PARENT_OBJECT)
    val associationType by ShortPacketField(OFFSET_ASSOCIATION_TYPE)
    val associationDesc by IntPacketField(OFFSET_ASSOCIATION_DESC)
    val sequenceNumber by IntPacketField(OFFSET_SEQUENCE_NUMBER)
    val filename by StringPacketField(OFFSET_FILENAME)
    val dateCreated by DateTimePacketField(OFFSET_FILENAME + 1 + (filename.length + 1) * 2)

    companion object {
        private const val OFFSET_STORAGE_ID = 0x00
        private const val OFFSET_OBJECT_FORMAT = 0x04
        private const val OFFSET_PROTECTION_STATUS = 0x06
        private const val OFFSET_OBJECT_COMPRESSED_SIZE = 0x08
        private const val OFFSET_THUMB_FORMAT = 0x0c
        private const val OFFSET_THUMB_COMPRESSED_SIZE = 0x0e
        private const val OFFSET_THUMB_PIX_WIDTH = 0x12
        private const val OFFSET_THUMB_PIX_HEIGHT = 0x16
        private const val OFFSET_IMAGE_PIX_WIDTH = 0x1a
        private const val OFFSET_IMAGE_PIX_HEIGHT = 0x1e
        private const val OFFSET_IMAGE_BIT_DEPTH = 0x22
        private const val OFFSET_PARENT_OBJECT = 0x26
        private const val OFFSET_ASSOCIATION_TYPE = 0x2a
        private const val OFFSET_ASSOCIATION_DESC = 0x2c
        private const val OFFSET_SEQUENCE_NUMBER = 0x30
        private const val OFFSET_FILENAME = 0x34
    }
}