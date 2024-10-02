package com.yanbin.ptpsample.usb

data class UsbDeviceItem(
    val deviceId: Int,
    val name: String,
    val hasPermission: Boolean,
    val isConnecting: Boolean
) {
}