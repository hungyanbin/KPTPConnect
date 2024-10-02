package com.yanbin.ptp.camera

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.mtp.MtpConstants
import android.mtp.MtpDevice
import com.yanbin.ptp.utils.asLocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MtpCamera(
    context: Context,
) : IPtpCamera {

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var mtpDevice: MtpDevice? = null
    private var usbDeviceConnection: UsbDeviceConnection? = null
    private var usbDevice: UsbDevice? = null
    private var serialNumber: String? = null
    private var manufacturer: String? = null
    private var name: String? = null

    override suspend fun attachToDevice(usbDevice: UsbDevice) {
        name = usbDevice.productName
        mtpDevice = MtpDevice(usbDevice).also { device ->
            val usbDeviceConnection = usbManager.openDevice(usbDevice)
            this@MtpCamera.usbDeviceConnection = usbDeviceConnection
            this@MtpCamera.usbDevice = usbDevice
            device.open(usbDeviceConnection)

            val deviceInfo = device.deviceInfo
            this.serialNumber = deviceInfo?.serialNumber
            this.manufacturer = deviceInfo?.manufacturer
        }
    }

    override suspend fun getCameraImages(): List<CameraImage> = withContext(Dispatchers.IO) {
        val mtpDevice = mtpDevice ?: throw PtpConnectionError("Missing mtpDevice")
        val storageIds = mtpDevice.storageIds?.toList() ?: emptyList()

        storageIds.flatMap { id ->
            val objectHandles = mtpDevice.getObjectHandles(id, 0, 0)?.toList() ?: emptyList()
            objectHandles.mapNotNull(::getCameraImageFromObjectHandle)
        }
    }

    private fun getCameraImageFromObjectHandle(objectHandle: Int): CameraImage? {
        val objectInfo = mtpDevice?.getObjectInfo(objectHandle) ?: return null
        val imageWidth = objectInfo.imagePixWidth
        val imageHeight = objectInfo.imagePixHeight
        val imageFormat = objectInfo.format
        val dateCreated = objectInfo.dateCreated.asLocalDateTime()
        val format = if (imageFormat == MtpConstants.FORMAT_EXIF_JPEG) {
            "jpg"
        } else {
            null
        }
        val uniqueName = objectInfo.dateCreated.toString() + "_" + objectInfo.name

        return if (format != null) {
            CameraImage(
                id = objectHandle,
                format = format,
                width = imageWidth,
                height = imageHeight,
                fileName = uniqueName,
                dataCreated = dateCreated
            )
        } else {
            null
        }
    }

    override suspend fun getCameraImage(imageId: Int): CameraImage? {
        return getCameraImageFromObjectHandle(imageId)
    }

    override fun getSerialNumber(): String {
        return serialNumber ?: throw PtpConnectionError("usbDevice not initialized")
    }

    override fun getManufacturer(): String {
        return manufacturer ?: throw PtpConnectionError("usbDevice not initialized")
    }

    override fun getName(): String {
        return name ?: throw PtpConnectionError("usbDevice not initialized")
    }

    override fun getUsbDeviceId(): Int {
        return usbDevice?.deviceId ?: throw PtpConnectionError("usbDevice not initialized")
    }

    override suspend fun downloadImage(imageId: Int, file: File) = withContext(Dispatchers.IO) {
        val mtpDevice = mtpDevice ?: throw PtpConnectionError("Missing mtpDevice")
        mtpDevice.importFile(imageId, file.absolutePath)
        Unit
    }

    override suspend fun downloadThumbnail(imageId: Int, file: File) = withContext(Dispatchers.IO) {
        val mtpDevice = mtpDevice ?: throw PtpConnectionError("Missing mtpDevice")
        val thumbnailByteArray = mtpDevice.getThumbnail(imageId) ?: throw PtpConnectionError("Missing thumbnail")
        file.writeBytes(thumbnailByteArray)
    }

    override fun release() {
        mtpDevice?.close()
        usbDeviceConnection?.close()
    }

    override fun isEventModeSupported(): Boolean {
        return false
    }

    override suspend fun setEventMode(enable: Boolean) {
        throw UnsupportedOperationException("MtpCamera does not support EventMode")
    }

    override suspend fun getEvents(): List<CameraEvent> {
        throw UnsupportedOperationException("MtpCamera does not support EventMode")
    }
}