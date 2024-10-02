package com.yanbin.ptpsample.usb

import android.hardware.usb.UsbDevice

interface UsbPermissionHelper {
    fun requestPermission(usbDevice: UsbDevice, listener: UsbPermissionListener)
}

interface UsbPermissionListener {
    fun onPermissionGranted(usbDevice: UsbDevice)
    fun onPermissionDenied()
}