package com.yanbin.ptpsample.usb

import kotlinx.coroutines.flow.Flow

interface UsbDeviceRepository {

    fun setUsbPermissionHelper(usbPermissionHelper: UsbPermissionHelper)
    fun getUsbDevices(): Flow<List<UsbDeviceItem>>
    fun requestPermission(usbDeviceItem: UsbDeviceItem, listener: UsbPermissionListener)
    fun setConnectingDevice(deviceId: Int)
    fun clearConnectingDevice()
}