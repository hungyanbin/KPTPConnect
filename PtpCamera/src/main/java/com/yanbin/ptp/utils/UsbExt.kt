package com.yanbin.ptp.utils

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface

fun UsbInterface.getEndPoints(): List<UsbEndpoint> {
    val endPoints = mutableListOf<UsbEndpoint>()
    for (i in 0 until endpointCount) {
        endPoints.add(getEndpoint(i))
    }
    return endPoints
}

fun UsbDevice.getInterfaces(): List<UsbInterface> {
    val interfaces = mutableListOf<UsbInterface>()
    for (i in 0 until interfaceCount) {
        interfaces.add(getInterface(i))
    }
    return interfaces
}