package com.linein.ptplib.packets

import com.linein.ptplib.packets.utils.Packet3
import com.linein.ptplib.packets.utils.readDateTime
import com.linein.ptplib.packets.utils.readObjectFormat

class ObjectInfoPacket(
    data: ByteArray
): Packet3(data) {

    constructor(packet: Packet): this(packet.packet)

    val storageId = fieldReader.readInt()
    val objectFormat = fieldReader.readObjectFormat()
    val protectionStatus = fieldReader.readShort()
    val objectCompressedSize = fieldReader.readInt()
    val thumbFormat = fieldReader.readObjectFormat()
    val thumbCompressedSize = fieldReader.readInt()
    val thumbPixWidth = fieldReader.readInt()
    val thumbPixHeight = fieldReader.readInt()
    val imagePixWidth = fieldReader.readInt()
    val imagePixHeight = fieldReader.readInt()
    val imageBitDepth = fieldReader.readInt()
    val parentObject = fieldReader.readInt()
    val associationType = fieldReader.readShort()
    val associationDesc = fieldReader.readInt()
    val sequenceNumber = fieldReader.readInt()
    val filename = fieldReader.readString()
    val dateCreated = fieldReader.readDateTime()
    val dateModified = fieldReader.readDateTime()
    val keywords = fieldReader.readString()
}