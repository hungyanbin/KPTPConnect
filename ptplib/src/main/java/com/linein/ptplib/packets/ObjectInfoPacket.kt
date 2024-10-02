package com.linein.ptplib.packets

import com.linein.ptplib.constants.ObjectFormat
import java.time.LocalDateTime

data class ObjectInfoPacket(
    val storageId: Int,
    val objectFormat: ObjectFormat,
    val protectionStatus: Short,
    val objectCompressedSize: Int,
    val thumbFormat: ObjectFormat,
    val thumbCompressedSize: Int,
    val thumbPixWidth: Int,
    val thumbPixHeight: Int,
    val imagePixWidth: Int,
    val imagePixHeight: Int,
    val imageBitDepth: Int,
    val parentObject: Int,
    val associationType: Short,
    val associationDesc: Int,
    val sequenceNumber: Int,
    val filename: String,
    val dateCreated: LocalDateTime,
) {

    companion object {
        private const val offset_storageId = 0x00
        private const val offset_objectFormat = 0x04
        private const val offset_protectionStatus = 0x06
        private const val offset_objectCompressedSize = 0x08
        private const val offset_thumbFormat = 0x0c
        private const val offset_thumbCompressedSize = 0x0e
        private const val offset_thumbPixWidth = 0x12
        private const val offset_thumbPixHeight = 0x16
        private const val offset_imagePixWidth = 0x1a
        private const val offset_imagePixHeight = 0x1e
        private const val offset_imageBitDepth = 0x22
        private const val offset_parentObject = 0x26
        private const val offset_associationType = 0x2a
        private const val offset_associationDesc = 0x2c
        private const val offset_sequenceNumber = 0x30
        private const val offset_filename = 0x34

        fun fromPacket(packet: Packet): ObjectInfoPacket {
            val storageId = packet.getIntL(offset_storageId)
            val objectFormat = packet.getShortL(offset_objectFormat)
            val protectionStatus = packet.getShortL(offset_protectionStatus)
            val objectCompressedSize = packet.getIntL(offset_objectCompressedSize)
            val thumbFormat = packet.getShortL(offset_thumbFormat)
            val thumbCompressedSize = packet.getIntL(offset_thumbCompressedSize)
            val thumbPixWidth = packet.getIntL(offset_thumbPixWidth)
            val thumbPixHeight = packet.getIntL(offset_thumbPixHeight)
            val imagePixWidth = packet.getIntL(offset_imagePixWidth)
            val imagePixHeight = packet.getIntL(offset_imagePixHeight)
            val imageBitDepth = packet.getIntL(offset_imageBitDepth)
            val parentObject = packet.getIntL(offset_parentObject)
            val associationType = packet.getShortL(offset_associationType)
            val associationDesc = packet.getIntL(offset_associationDesc)
            val sequenceNumber = packet.getIntL(offset_sequenceNumber)
            val filename = packet.getCompactString(offset_filename)

            val dateCreated = packet.getDateTime(offset_filename + 1 + (filename.length + 1)*2)
            return ObjectInfoPacket(
                storageId,
                ObjectFormat.values().firstOrNull { it.formatCode == objectFormat }
                    ?: throw IllegalArgumentException("Unknown format code: $objectFormat"),
                protectionStatus,
                objectCompressedSize,
                ObjectFormat.values().firstOrNull { it.formatCode == thumbFormat }
                    ?: throw IllegalArgumentException("Unknown format code: $thumbFormat"),
                thumbCompressedSize,
                thumbPixWidth,
                thumbPixHeight,
                imagePixWidth,
                imagePixHeight,
                imageBitDepth,
                parentObject,
                associationType,
                associationDesc,
                sequenceNumber,
                filename,
                dateCreated,
            )
        }
    }
}