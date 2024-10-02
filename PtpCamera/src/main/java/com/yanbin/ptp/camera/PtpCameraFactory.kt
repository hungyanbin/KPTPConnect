package com.yanbin.ptp.camera

import android.content.Context
import android.hardware.usb.UsbDevice

class PtpCameraFactory(private val context: Context) {

    suspend fun create(
        usbDevice: UsbDevice
    ): IPtpCamera {
        return when(usbDevice.vendorId) {
            VENDOR_ID_CANON -> {
                CanonCamera(context).apply {
                    attachToDevice(usbDevice)
                }
            }
            VENDOR_ID_NIKON -> {
                NikonCamera(context).apply {
                    attachToDevice(usbDevice)
                }
            }
            VENDOR_ID_SONY -> {
                MtpCamera(context).apply {
                    attachToDevice(usbDevice)
                }
            }
            else -> {
                // Try to connect to unknown vendor using MTP, if it fails, throw an exception
                MtpCamera(context).apply {
                    attachToDevice(usbDevice)
                }
            }
        }
    }


    companion object {
        private const val VENDOR_ID_CANON = 0x04a9
        private const val VENDOR_ID_NIKON = 0x04b0
        private const val VENDOR_ID_SONY = 0x054c
    }
}