package com.linein.ptplib.packets

import com.linein.ptplib.packets.utils.Packet3

class DeviceInfoPacket(
    data: ByteArray
): Packet3(data) {

    val standardVersion = fieldReader.readShort()
    val vendorExtensionId = fieldReader.readInt()
    val vendorExtensionVersion = fieldReader.readShort()
    val vendorExtensions = fieldReader.readString()
    val functionalMode = fieldReader.readShort()
    val operationsSupported = fieldReader.readShortArray()
    val eventsSupported = fieldReader.readShortArray()
    val devicePropertiesSupported = fieldReader.readShortArray()
    val captureFormats = fieldReader.readShortArray()
    val playbackFormats = fieldReader.readShortArray()
    val manufacturer = fieldReader.readString()
    val model = fieldReader.readString()
    val deviceVersion = fieldReader.readString()
    val serialNumber = fieldReader.readString()
}