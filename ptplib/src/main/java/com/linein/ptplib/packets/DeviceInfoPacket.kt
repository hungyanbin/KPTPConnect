package com.linein.ptplib.packets

import com.yanbin.ptplib.annotation.PtpPacket

@PtpPacket
data class DeviceInfoPacket(
    val standardVersion: Short,
    val vendorExtensionId: Int,
    val vendorExtensionVersion: Short,
    val vendorExtensions: String,
    val functionalMode: Short,
    val operationsSupported: List<Short>,
    val eventsSupported: List<Short>,
    val devicePropertiesSupported: List<Short>,
    val captureFormats: List<Short>,
    val playbackFormats: List<Short>,
    val manufacturer: String,
    val model:String,
    val deviceVersion:String,
    val serialNumber: String,
)
