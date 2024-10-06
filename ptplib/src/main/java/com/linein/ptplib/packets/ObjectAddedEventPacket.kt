package com.linein.ptplib.packets

import com.yanbin.ptplib.annotation.PtpPacket

@PtpPacket
data class ObjectAddedEventPacket(
    val event: PtpEvent,
    val objectId: Int,
)