package com.yanbin.ptp.camera

import android.hardware.usb.UsbDevice
import java.io.File

interface IPtpCamera {

    suspend fun attachToDevice(usbDevice: UsbDevice)

    /**
     *  @return list of camera images, it will always return the same list of images for the same instance of camera,
     *  to refresh the list, create a new instance of camera
     */
    suspend fun getCameraImages(): List<CameraImage>

    suspend fun getCameraImage(imageId: Int): CameraImage?

    suspend fun downloadImage(imageId: Int, file: File)
    suspend fun downloadThumbnail(imageId: Int, file: File)

    suspend fun setEventMode(enable: Boolean)
    suspend fun getEvents(): List<CameraEvent>
    fun isEventModeSupported(): Boolean

    fun getName(): String
    fun getSerialNumber(): String
    fun getManufacturer(): String
    fun getUsbDeviceId(): Int

    fun release()
}
