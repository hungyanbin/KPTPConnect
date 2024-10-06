package com.linein.ptplib.packets

import com.linein.ptplib.constants.ObjectFormat
import com.yanbin.ptplib.annotation.PtpPacket
import java.time.LocalDateTime

@PtpPacket
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
    val dateModified: LocalDateTime,
    val keywords: String,
)