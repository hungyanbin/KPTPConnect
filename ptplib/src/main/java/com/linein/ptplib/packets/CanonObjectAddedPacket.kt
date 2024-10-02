package com.linein.ptplib.packets

import com.linein.ptplib.constants.ObjectFormat


data class CanonObjectAddedPacket(
    val size: Int,
    val event: CannonEvent,
    val objectId: Int,
    val parentId: Int,
    val storageId: Int,
    val format: ObjectFormat,
    val compressedSize: Int,
    val fileName: String
) {

    companion object {
        private const val offset_objectId = 0x08
        private const val offset_storageId = 0x0c
        private const val offset_format_code = 0x10
        private const val offset_compressedSize = 0x1c
        private const val offset_parent = 0x20
        private const val offset_file_name = 0x28

        fun fromPacket(packet: Packet): CanonObjectAddedPacket {
            val (size, eventCode) = CanonEventPacket.parseSizeAndEventCode(packet) ?: throw IllegalStateException("Should not be null!!")

            val objectId = packet.getIntL(offset_objectId)
            val storageId = packet.getIntL(offset_storageId)
            val rawFormat = packet.getShortL(offset_format_code)
            val compressedSize = packet.getIntL(offset_compressedSize)
            val parentId = packet.getIntL(offset_parent)
            val fileName = packet.getCompactString(offset_file_name)
            val format = ObjectFormat.values().firstOrNull { it.formatCode == rawFormat }
                ?: throw IllegalArgumentException("Unknown format code: $rawFormat")
            return CanonObjectAddedPacket(size, eventCode, objectId, parentId, storageId, format, compressedSize, fileName)
        }
    }
}