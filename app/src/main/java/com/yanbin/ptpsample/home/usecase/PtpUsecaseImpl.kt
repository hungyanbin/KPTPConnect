package com.yanbin.ptpsample.home.usecase

import android.content.Context
import com.yanbin.ptp.camera.CameraImage
import com.yanbin.ptp.camera.IPtpCamera
import com.yanbin.ptp.camera.PtpCameraFactory
import com.yanbin.ptpsample.usb.UsbDeviceItem
import com.yanbin.ptpsample.usb.UsbDeviceRepository
import com.yanbin.ptpsample.usb.UsbFacade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PtpUsecaseImpl @Inject constructor(
    private val context: Context,
    private val usbFacade: UsbFacade,
    private val usbDeviceRepository: UsbDeviceRepository,
): PtpUsecase {

    override suspend fun createCameraDevice(deviceItem: UsbDeviceItem): IPtpCamera = withContext(Dispatchers.IO) {
        val usbDevice = usbFacade.getConnectingDevices()
            .find { it.deviceId == deviceItem.deviceId } ?: throw IllegalStateException("Device not found")

        val ptpCameraFactory = PtpCameraFactory(context)
        ptpCameraFactory.create(usbDevice).also {
            usbDeviceRepository.setConnectingDevice(usbDevice.deviceId)
        }
    }

    override suspend fun downloadImage(camera: IPtpCamera, cameraImage: CameraImage) = withContext(Dispatchers.IO) {
//        val file = storageManager.createRandomExternalFile(FileExtension.Jpeg)
//        camera.downloadImage(cameraImage.id, file)
//        file
        TODO()
    }

    override suspend fun downloadThumbnail(camera: IPtpCamera, cameraImage: CameraImage) = withContext(Dispatchers.IO) {
//        val file = storageManager.createRandomExternalFile(FileExtension.Jpeg)
//        camera.downloadThumbnail(cameraImage.id, file)
//        file
        TODO()
    }

    override suspend fun getCameraConnectionStatus(camera: IPtpCamera): Flow<Boolean> {
        return usbDeviceRepository.getUsbDevices()
            .map { usbDevices ->
                val connectedDevice = usbDevices.find { it.deviceId == camera.getUsbDeviceId() }
                connectedDevice != null
            }
    }
}